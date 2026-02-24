package com.limspyne.anon_vote.voting.entities;

import com.limspyne.anon_vote.poll.application.entities.Poll;
import com.limspyne.anon_vote.users.application.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(
        name = "poll_vote",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"author_id", "poll_id"}
        )
)
@NoArgsConstructor
public class PollVote {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "poll_id")
    private Poll poll;

    @Column(name = "vote_type")
    @Enumerated(EnumType.STRING)
    private VoteType voteType = VoteType.None;

    public enum VoteType {
        Up,
        Down,
        None
    }

    private PollVote(User author, Poll poll) {
        this.author = author;
        this.poll = poll;
    }

    public static PollVote ofDefault(User author, Poll poll) {
        return new PollVote(author, poll);
    }

    public void updateType(VoteType type) {
        this.voteType = type;
    }
}
