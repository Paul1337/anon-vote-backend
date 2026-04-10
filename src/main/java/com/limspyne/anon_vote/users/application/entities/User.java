package com.limspyne.anon_vote.users.application.entities;

import com.limspyne.anon_vote.poll.application.entities.Poll;
import com.limspyne.anon_vote.users.application.exceptions.CodeSendLimitException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@NamedEntityGraph(name = "User.withActiveCodes", attributeNodes = { @NamedAttributeNode("activeCodes") })
@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 64, unique = true)
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    private List<UserActiveCode> activeCodes = new ArrayList<>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Poll> createdPolls = new ArrayList<>();

    @Column(name = "telegram_id")
    @Getter
    @Setter
    private Long telegramId;

    @Column(name = "confirmed_telegram")
    @Getter
    @Setter
    boolean confirmedTelegram;

    public static final long MIN_SECONDS_BETWEEN_REQUESTS = 60;

    public User(String email) {
        this.email = email;
    }

    public User(String email, long telegramId) {
        this.email = email;
        this.telegramId = telegramId;
    }

    public void addActiveCode(UserActiveCode code) {
        activeCodes.add(code);
        code.setUser(this);
    }

    public boolean tryConfirmCodeValue(String codeValue) {
        List<UserActiveCode> activeCodes = getActiveCodes();
        return activeCodes.stream().anyMatch(activeCode -> {
            boolean codeValueIsRight = activeCode.getValue().equals(codeValue);
            boolean codeIsNotExpired = Duration.between(activeCode.getCreatedAt(), LocalDateTime.now()).compareTo(UserActiveCode.CODE_DURATION) < 0;
            return codeValueIsRight && codeIsNotExpired;
        });
    }

    public boolean isTelegramConnected() {
        return telegramId != null;
    }

    public boolean canRequestNewCode() {
        UserActiveCode lastActiveCode = getActiveCodes().stream()
                .max(Comparator.comparing(UserActiveCode::getCreatedAt)).orElse(null);

        return (lastActiveCode == null
                || Duration.between(lastActiveCode.getCreatedAt(), LocalDateTime.now()).toSeconds() >= MIN_SECONDS_BETWEEN_REQUESTS);
    }
}
