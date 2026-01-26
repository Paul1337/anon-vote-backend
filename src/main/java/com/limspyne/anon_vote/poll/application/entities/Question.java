package com.limspyne.anon_vote.poll.application.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "question")
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    @Column(nullable = false)
    @Getter
    private String text;

    @ManyToOne
    @JoinColumn(name = "poll_id", nullable = false)
    @Getter
    @Setter
    private Poll poll;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "question_options",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @Column(name = "option_text", nullable = false)
    @BatchSize(size = 20)
    @Getter
    private List<String> options;

    @Column(name = "position", nullable = false)
    @Getter
    private int position;

    public Question(String text, List<String> options, int position) {
        this.text = text;
        this.options = options;
        this.position = position;
    }

}
