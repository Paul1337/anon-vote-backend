package com.limspyne.anon_vote.poll.application.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "poll_tag")
@NoArgsConstructor
public class PollTag {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    @Column(name = "name", length = 100, unique = true)
    @Getter
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Poll> polls = new HashSet<>();

    public PollTag(String name) {
        this.name = name;
    }
}
