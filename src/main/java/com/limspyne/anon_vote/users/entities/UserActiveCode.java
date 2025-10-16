package com.limspyne.anon_vote.users.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Entity
@Table(name = "user_active_code")
@NoArgsConstructor
@Getter
public class UserActiveCode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Setter
    private User user;

    @Column(name = "value", length = 8)
    private String value;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public static final Duration CODE_DURATION = Duration.of(10, ChronoUnit.MINUTES);

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public UserActiveCode(String value) {
        this.value = value;
    }
}
