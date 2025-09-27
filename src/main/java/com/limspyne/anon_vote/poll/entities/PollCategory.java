package com.limspyne.anon_vote.poll.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "poll_category", indexes = @Index(name = "idx_poll_category_name", columnList = "name"))
@NoArgsConstructor
public class PollCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    @Getter
    @Column(length = 128)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    private PollCategory parentCategory;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentCategory")
    @Getter
    @Setter
    private List<PollCategory> childCategories;

    @OneToMany(mappedBy = "category")
    @Getter
    private List<Poll> polls;

    public void addPoll(Poll poll) {
        if (poll != null && !polls.contains(poll)) {
            polls.add(poll);
            poll.setCategory(this);
        }
    }
}
