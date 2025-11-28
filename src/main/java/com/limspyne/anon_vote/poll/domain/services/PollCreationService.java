package com.limspyne.anon_vote.poll.domain.services;

import com.limspyne.anon_vote.poll.domain.entities.Poll;
import com.limspyne.anon_vote.poll.domain.entities.PollCategory;
import com.limspyne.anon_vote.poll.domain.entities.PollTag;
import com.limspyne.anon_vote.poll.domain.entities.Question;
import com.limspyne.anon_vote.poll.domain.exceptions.CategoryNotFoundException;
import com.limspyne.anon_vote.poll.domain.mappers.PollMapper;
import com.limspyne.anon_vote.poll.infrastructure.repositories.CategoryRepository;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import com.limspyne.anon_vote.poll.web.dto.CreatePoll;
import com.limspyne.anon_vote.poll.web.dto.GetPoll;
import com.limspyne.anon_vote.users.domain.services.UserService;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import com.limspyne.anon_vote.users.instrastructure.security.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PollCreationService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PollTagProviderService pollTagProviderService;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollMapper pollMapper;

    @Transactional
    public GetPoll.Response createPoll(CreatePoll.Request dto, AppUserDetails appUserDetails) {
        UUID categoryId = UUID.fromString(dto.categoryId());
        PollCategory categoryForCreatedPoll = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));

        if (dto.categoryName() != null) {
            categoryForCreatedPoll = new PollCategory(dto.categoryName(), categoryForCreatedPoll, List.of());
            categoryRepository.save(categoryForCreatedPoll);
        }

        var author = userService.getUserById(appUserDetails.getId());

        Poll poll = new Poll(dto.title(), categoryForCreatedPoll, author);

        Set<PollTag> tags = pollTagProviderService.provideTagsOfNames(dto.tags());
        poll.setTags(tags);

        List<Question> questions = dto.questions().stream().map(qstDto -> {
            var question = new Question(qstDto.getText(), qstDto.getOptions());
            poll.addQuestion(question);
            return question;
        }).toList();

        poll.setQuestions(questions);

        pollRepository.save(poll);

        return pollMapper.toResponseForAnonymousUser(poll);
    }
}
