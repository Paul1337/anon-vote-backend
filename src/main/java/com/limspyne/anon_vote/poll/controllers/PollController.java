package com.limspyne.anon_vote.poll.controllers;

import com.limspyne.anon_vote.poll.entities.Question;
import com.limspyne.anon_vote.poll.exceptions.CategoryNotFoundException;
import com.limspyne.anon_vote.poll.repositories.CategoryRepository;
import com.limspyne.anon_vote.poll.repositories.PollRepository;
import com.limspyne.anon_vote.poll.dto.CreatePoll;
import com.limspyne.anon_vote.poll.dto.GetPoll;
import com.limspyne.anon_vote.poll.entities.Poll;
import com.limspyne.anon_vote.poll.exceptions.PollNotFoundException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/polls")
public class PollController {
    @Autowired
    private PollRepository quizRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping()
    public ResponseEntity<CreatePoll.Response> createPoll(@RequestBody @Valid CreatePoll.Request dto) {
        UUID categoryId = UUID.fromString(dto.categoryId());
        var pollCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));

        Poll poll = new Poll(dto.title(), pollCategory);

        List<Question> questions = dto.questions().stream().map(qstDto -> {
            var question = new Question(qstDto.getText(), qstDto.getOptions());
            poll.addQuestion(question);
            return question;
        }).toList();

        poll.setQuestions(questions);

        quizRepository.save(poll);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(modelMapper.map(poll, CreatePoll.Response.class));
    }

    @PostMapping("/{id}/submit")
    public void submitPoll() {

    }

    @GetMapping("/{id}")
    public ResponseEntity<GetPoll.Response> getQuiz(@PathVariable(name = "id") String id) {
        UUID uuid = UUID.fromString(id);
        var poll = quizRepository.findById(uuid).orElseThrow(() -> new PollNotFoundException(uuid));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GetPoll.Response(poll.getId().toString(), poll.getTitle()));
    }


}
