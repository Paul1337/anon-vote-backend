package com.limspyne.anon_vote.poll.controllers;

import com.limspyne.anon_vote.poll.dto.GetCategory;
import com.limspyne.anon_vote.poll.entities.PollCategory;
import com.limspyne.anon_vote.poll.exceptions.CategoryNotFoundException;
import com.limspyne.anon_vote.poll.repositories.CategoryRepository;
import com.limspyne.anon_vote.poll.services.CategoryMapper;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("categories")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping({"/{id}", "/", ""})
    ResponseEntity<List<GetCategory.ResponseDto>> getCategories(@PathVariable(name = "id", required = false) String categoryId, @RequestParam(name = "depth", defaultValue = "1") @Min(1) @Max(10) int depth) {
        List<PollCategory> items = null;
        if (categoryId == null) {
            items = categoryRepository.findByParentCategoryIdNull();
        } else {
            UUID categoryUUID = UUID.fromString(categoryId);
            var parentCategory = categoryRepository.findById(categoryUUID).orElseThrow(() -> new CategoryNotFoundException(categoryUUID));
            items = parentCategory.getChildCategories();
        }

        List<GetCategory.ResponseDto> responseItems = items.stream().map(item -> categoryMapper.toDto(item, depth - 1)).sorted(Comparator.comparing(GetCategory.ResponseDto::getName)).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseItems);
    }
}
