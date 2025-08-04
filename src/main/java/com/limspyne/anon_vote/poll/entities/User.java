package com.limspyne.anon_vote.poll.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private String id;

    @Column(length = 64)
    @Getter
    private String email;

    public User(String email) {
        this.email = email;
    }
}
