package com.limspyne.anon_vote.poll.repositories;

import com.limspyne.anon_vote.poll.entities.PollCategory;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
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
//    List<PollCategory> findByNameStartsWithIgnoreCase(String namePrefix, Pageable pageable);

//    @Query("SELECT DISTINCT pc FROM PollCategory pc " +
//            "LEFT JOIN FETCH pc.parentCategory p1" +  // Подгружаем parentCategory за один запрос
//            "LEFT JOIN FETCH p1.parentCategory p2 " +  // Для второго уровня
//            "LEFT JOIN FETCH p2.parentCategory p3 " +  // Для третьего уровня
//            "WHERE LOWER(pc.name) LIKE LOWER(CONCAT(:namePrefix, '%'))")
    @EntityGraph(attributePaths = {"parentCategory", "parentCategory.parentCategory", "parentCategory.parentCategory.parentCategory"})
    @Query("SELECT pc FROM PollCategory pc WHERE LOWER(pc.name) LIKE LOWER(CONCAT(:namePrefix, '%'))")
    List<PollCategory> findByNameStartsWithIgnoreCase(@Param("namePrefix") String namePrefix, Pageable pageable);
}
