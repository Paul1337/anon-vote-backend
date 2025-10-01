package com.limspyne.anon_vote.poll.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.*;

@Entity
@NoArgsConstructor
@Table(name = "poll")
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    @Column(length = 100, nullable = false)
    @Getter
    @Setter
    private String title;

    @CreationTimestamp
    @Getter
    private Instant createdAt;

    @UpdateTimestamp
    @Getter
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    @BatchSize(size = 20)
    private List<Question> questions;

    @ManyToOne()
    @JoinColumn(name = "category_id")
    @Getter
    @Setter
    private PollCategory category;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "poll_tag_relation",
            joinColumns = @JoinColumn(name = "poll_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Getter
    @Setter
    private Set<PollTag> tags = new HashSet<>();

//    @ManyToMany(fetch = FetchType.LAZY)
//    @Getter
//    private Set<User> usersAttempted;

    public Poll(String title, PollCategory category) {
        this.title = title;
        questions = new ArrayList<>();
        setCategory(category);
        category.addPoll(this);
    }

    public void addQuestion(Question question) {
        questions.add(question);
        question.setPoll(this);
    }

}
