package com.limspyne.anon_vote.poll.application.services;

import com.limspyne.anon_vote.poll.application.entities.Poll;
import com.limspyne.anon_vote.category.application.entities.PollCategory;
import com.limspyne.anon_vote.poll.application.entities.PollTag;
import com.limspyne.anon_vote.poll.application.entities.Question;
import com.limspyne.anon_vote.category.application.exceptions.CategoryNotFoundException;
import com.limspyne.anon_vote.poll.application.exceptions.DuplicateAnswerInPollException;
import com.limspyne.anon_vote.poll.infrastructure.mappers.PollMapper;
import com.limspyne.anon_vote.category.infrastructure.repositories.CategoryRepository;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import com.limspyne.anon_vote.poll.presenter.dto.CreatePoll;
import com.limspyne.anon_vote.poll.presenter.dto.GetPoll;
import com.limspyne.anon_vote.users.application.services.UserService;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import com.limspyne.anon_vote.users.instrastructure.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PollCreationService {
    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final UserService userService;

    private final PollTagProviderService pollTagProviderService;

    private final PollRepository pollRepository;

    private final PollMapper pollMapper;

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public GetPoll.Response createPoll(CreatePoll.Request dto, AppUserDetails appUserDetails) {
        UUID categoryId = UUID.fromString(dto.categoryId());
        PollCategory categoryForCreatedPoll = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));

        // Если указано кастомное название категории, создаем подкатегорию
        // Это позволяет пользователям уточнять тему внутри существующей иерархии
        if (dto.categoryName() != null) {
            categoryForCreatedPoll = new PollCategory(dto.categoryName(), categoryForCreatedPoll, List.of());
            categoryRepository.save(categoryForCreatedPoll);
        }

        var author = userService.getUserById(appUserDetails.getId());

        Poll poll = new Poll(dto.title(), categoryForCreatedPoll, author);

        Set<PollTag> tags = pollTagProviderService.provideTagsOfNames(dto.tags());
        poll.setTags(tags);

        dto.questions().forEach(qstDto -> {
            if (hasDuplicateOptions(qstDto)) {
                throw new DuplicateAnswerInPollException();
            }

            var question = new Question(qstDto.getText(), qstDto.getOptions(), poll.getQuestions().size());
            poll.addQuestion(question);
        });

        pollRepository.save(poll);

        return pollMapper.toResponseForAnonymousUser(poll);
    }

    private boolean hasDuplicateOptions(CreatePoll.QuestionDto questionDto) {
        var options = new HashSet<>();

        for (var option: questionDto.getOptions()) {
            if (!options.add(option)) {
                return true;
            }
        }

        return false;
    }
}
