package com.limspyne.anon_vote.poll.entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "poll_id", nullable = false)
    @Getter
    private Poll poll;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "question_options",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @Column(name = "option_text", nullable = false)
    private List<String> options;

    @Column
    private String rightAnswer;
}
