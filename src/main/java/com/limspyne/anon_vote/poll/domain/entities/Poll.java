package com.limspyne.anon_vote.poll.domain.entities;

import com.limspyne.anon_vote.users.domain.entities.User;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlSchemaTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

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
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(
            name = "poll_tag_relation",
            joinColumns = @JoinColumn(name = "poll_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Setter
    private Set<PollTag> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "poll_answered_users",
            joinColumns = @JoinColumn(name = "poll_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Setter
    private Set<User> attemptedUsers = new HashSet<>();

    public Poll(String title, PollCategory category, User author) {
        this.title = title;
        questions = new ArrayList<>();
        setCategory(category);
        this.author = author;
    }

    public void addQuestion(Question question) {
        questions.add(question);
        question.setPoll(this);
    }

    public void addAttemptedUser(User user) {
        attemptedUsers.add(user);
    }

}
