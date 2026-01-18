package com.limspyne.anon_vote.poll.application.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "poll_question_answer")
@NoArgsConstructor
@Getter
public class PollQuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "poll_answer_record_id")
    @Setter
    private PollAnswerRecord answerRecord;

    @Column(length = 1024)
    private String answer;

    public PollQuestionAnswer(Question question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}
