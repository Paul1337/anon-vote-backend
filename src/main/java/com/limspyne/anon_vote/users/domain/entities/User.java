package com.limspyne.anon_vote.users.domain.entities;

import com.limspyne.anon_vote.poll.domain.entities.Poll;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public User(String email) {
        this.email = email;
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
}
