package com.limspyne.anon_vote.poll.entities;

import com.limspyne.anon_vote.users.domain.entities.User;
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
@Getter
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 100, nullable = false)
    @Setter
    private String title;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    @BatchSize(size = 20)
    private List<Question> questions;

    @ManyToOne()
    @JoinColumn(name = "category_id")
    @Setter
    private PollCategory category;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "poll_tag_relation",
            joinColumns = @JoinColumn(name = "poll_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Setter
    private Set<PollTag> tags = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

//    @ManyToMany(fetch = FetchType.LAZY)
//    @Getter
//    private Set<User> usersAttempted;

    public Poll(String title, PollCategory category, User author) {
        this.title = title;
        questions = new ArrayList<>();
        setCategory(category);
        category.addPoll(this);
        this.author = author;
    }

    public void addQuestion(Question question) {
        questions.add(question);
        question.setPoll(this);
    }

}
