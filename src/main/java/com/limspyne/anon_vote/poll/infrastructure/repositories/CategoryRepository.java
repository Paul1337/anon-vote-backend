package com.limspyne.anon_vote.poll.infrastructure.repositories;

import com.limspyne.anon_vote.poll.domain.entities.PollCategory;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<PollCategory, UUID> {
    List<PollCategory> findByParentCategoryIdNull();

    @EntityGraph(attributePaths = {"parentCategory", "parentCategory.parentCategory", "parentCategory.parentCategory.parentCategory"})
    @Query("SELECT pc FROM PollCategory pc WHERE LOWER(pc.name) LIKE LOWER(CONCAT(:namePrefix, '%'))")
    List<PollCategory> findByNameStartsWithIgnoreCaseWithDepth3(@Param("namePrefix") String namePrefix, Pageable pageable);
}
