package com.limspyne.anon_vote.poll.controllers;

import com.limspyne.anon_vote.poll.dto.SearchPolls;
import com.limspyne.anon_vote.poll.entities.PollTag;
import com.limspyne.anon_vote.poll.entities.Question;
import com.limspyne.anon_vote.poll.exceptions.CategoryNotFoundException;
import com.limspyne.anon_vote.poll.repositories.CategoryRepository;
import com.limspyne.anon_vote.poll.repositories.PollRepository;
import com.limspyne.anon_vote.poll.dto.CreatePoll;
import com.limspyne.anon_vote.poll.dto.GetPoll;
import com.limspyne.anon_vote.poll.entities.Poll;
import com.limspyne.anon_vote.poll.exceptions.PollNotFoundException;
import com.limspyne.anon_vote.poll.services.PollTagService;
import com.limspyne.anon_vote.shared.dto.PageResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/polls")
public class PollController {
    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

//    @Autowired
//    private PollTagRepository pollTagRepository;

    @Autowired
    private PollTagService pollTagService;

    @PostMapping()
    public ResponseEntity<GetPoll.Response> createPoll(@RequestBody @Valid CreatePoll.Request dto) {
        UUID categoryId = UUID.fromString(dto.categoryId());
        var pollCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));

        Poll poll = new Poll(dto.title(), pollCategory);

        Set<PollTag> tags = pollTagService.findOrCreateTagsOfNames(dto.tags());
        poll.setTags(tags);

        List<Question> questions = dto.questions().stream().map(qstDto -> {
            var question = new Question(qstDto.getText(), qstDto.getOptions());
            poll.addQuestion(question);
            return question;
        }).toList();

        poll.setQuestions(questions);

        pollRepository.save(poll);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(modelMapper.map(poll, GetPoll.Response.class));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<GetPoll.Response>> searchPolls(@Parameter(description = "Search parameters for polls") @ModelAttribute @Validated SearchPolls.Request request) {
        Page<Poll> pollsPage;
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        if (request.getTags().isEmpty()) {
            pollsPage = pollRepository.findAllByTitle(request.getTitle(), pageRequest);
        } else {
            pollsPage = pollRepository.findAllByTitleAndTags(request.getTitle(), request.getTags(), pageRequest);
        }
        List<GetPoll.Response> pollsDtos = pollsPage.stream().map(poll -> modelMapper.map(poll, GetPoll.Response.class)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(new PageResponseDto<>(pollsDtos, pollsPage.hasNext()));
    }

    @PostMapping("/{id}/submit")
    public void submitPoll() {

    }

    @GetMapping("/{id}")
    public ResponseEntity<GetPoll.Response> getQuiz(@PathVariable(name = "id") String id) {
        UUID uuid = UUID.fromString(id);
        var poll = pollRepository.findById(uuid).orElseThrow(() -> new PollNotFoundException(uuid));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(modelMapper.map(poll, GetPoll.Response.class));
    }


}
