package com.limspyne.anon_vote.poll.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "poll_answer_record")
@NoArgsConstructor
public class PollAnswerRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private String id;

    @Column
    @Getter
    private String email;

//    @OneToMany(mappedBy = "quizAnswerRecord", cascade = CascadeType.ALL, orphanRemoval = true)
//    @MapKeyJoinColumn(name = "question_id")  // вопрос будет ключом в Map
//    private Map<Question, QuestionAnswer> questionToAnswer = new HashMap<>();

}
