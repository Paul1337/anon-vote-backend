package com.limspyne.anon_vote.poll.application.entities;

import com.limspyne.anon_vote.users.application.entities.User;
import com.limspyne.anon_vote.voting.entities.PollVote;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
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

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    @BatchSize(size = 20)
    @OrderBy("position ASC")
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

    @Column(name = "votes")
    @Getter
    private long votes;

    @Version
    @Column(name = "version")
    private long version;

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

    @PrePersist
    protected void onCreate() {
        var now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void cancelVote(PollVote vote) {
        if (vote.getVoteType() == PollVote.VoteType.Up) {
            downVote();
        } else if (vote.getVoteType() == PollVote.VoteType.Down) {
            upVote();
        }
    }

    public void applyVote(PollVote vote) {
        if (vote.getVoteType() == PollVote.VoteType.Up) {
            upVote();
        } else if (vote.getVoteType() == PollVote.VoteType.Down) {
            downVote();
        }
    }

    private void upVote() {
        votes++;
    }

    private void downVote() {
        votes--;
    }
}
