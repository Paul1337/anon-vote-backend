package com.limspyne.anon_vote.poll.application.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
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

    @Column(name = "fingerprint")
    @Setter
    private String fingerprint;

    @ManyToOne
    @JoinColumn(name = "poll_id")
    private Poll poll;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "answerRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PollQuestionAnswer> answers = new ArrayList<>();

    public PollAnswerRecord(Poll poll, List<PollQuestionAnswer> answers) {
        this.poll = poll;
        this.answers = answers;

        this.answers.forEach(answer -> answer.setAnswerRecord(this));
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
