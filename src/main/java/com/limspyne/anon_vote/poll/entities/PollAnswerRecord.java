package com.limspyne.anon_vote.poll.entities;

import com.limspyne.anon_vote.users.domain.entities.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "poll_answer_record")
@NoArgsConstructor
@Getter
public class PollAnswerRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "fingerprint")
    @Setter
    private String fingerprint;

    @ManyToOne
    @JoinColumn(name = "poll_id")
    private Poll poll;

    @OneToMany(mappedBy = "answerRecord", cascade = CascadeType.ALL)
    private List<PollQuestionAnswer> answers = new ArrayList<>();

    public PollAnswerRecord(User user, Poll poll, List<PollQuestionAnswer> answers) {
        this.user = user;
        this.poll = poll;
        this.answers = answers;

        this.answers.forEach(answer -> answer.setAnswerRecord(this));
    }
}
