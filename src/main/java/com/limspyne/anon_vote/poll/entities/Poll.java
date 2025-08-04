package com.limspyne.anon_vote.poll.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "poll")
    @Getter
    private List<Question> questions;

//    @ManyToMany(fetch = FetchType.LAZY)
//    @Getter
//    private Set<User> usersAttempted;

    public Poll(String title) {
        this.title = title;
    }

}
