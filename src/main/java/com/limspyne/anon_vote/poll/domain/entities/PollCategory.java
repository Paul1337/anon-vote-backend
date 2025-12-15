package com.limspyne.anon_vote.poll.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "poll_category", indexes = @Index(name = "idx_poll_category_name", columnList = "name"))
@NoArgsConstructor
@Getter
public class PollCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 128)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private PollCategory parentCategory;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentCategory")
    @Setter
    private List<PollCategory> childCategories;

    @Column(name = "path", columnDefinition = "TEXT")
    private String path;

    public PollCategory(String name, PollCategory parentCategory, List<PollCategory> childCategories) {
        this.name = name;
        this.parentCategory = parentCategory;
        this.childCategories = childCategories;
    }

    @PrePersist
    @PreUpdate
    private void calculatePath() {
        if (parentCategory == null) {
            this.path = id != null ? id + "/" : "/";
        } else {
            this.path = parentCategory.getPath() + (id != null ? id + "/" : "/");
        }
    }
}
