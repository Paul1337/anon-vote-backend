package com.limspyne.anon_vote.poll.web.controllers;

import com.limspyne.anon_vote.poll.domain.services.CategoryQueryService;
import com.limspyne.anon_vote.poll.web.dto.GetCategory;
import com.limspyne.anon_vote.poll.domain.entities.PollCategory;
import com.limspyne.anon_vote.poll.domain.exceptions.CategoryNotFoundException;
import com.limspyne.anon_vote.poll.infrastructure.repositories.CategoryRepository;
import com.limspyne.anon_vote.poll.infrastructure.mappers.CategoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryQueryService categoryQueryService;

    @GetMapping({ "/search" })
    @Operation(summary = "Search categories by name and paging, depth is 3")
    public ResponseEntity<List<GetCategory.ResponseWithPathDto>> searchCategories(@RequestParam(defaultValue = "") String name, @PageableDefault(size = 100, sort = "name") Pageable pageable) {
        var filteredCategoriesDto = categoryQueryService.searchCategories(name, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(filteredCategoriesDto);
    }

    @GetMapping({"", "/"})
    @Operation(summary = "Find all root categories with specified depth") // добавить paging?
    public ResponseEntity<List<GetCategory.ResponseDto>> findRootCategories(@RequestParam(name = "depth", defaultValue = "1") @Min(1) @Max(10) int depth) {
        var categoriesDto =  categoryQueryService.findRootCategoriesWithDepth(depth);
        return ResponseEntity.status(HttpStatus.OK).body(categoriesDto);
    }

    @GetMapping({"/{id}", "/{id}/"})
    @Operation(summary = "Find all child categories with specified depth") // добавить paging?
    public ResponseEntity<List<GetCategory.ResponseDto>> findChildCategories(
            @PathVariable(name = "id") String parentCategoryId,
            @RequestParam(defaultValue = "1") @Min(1) @Max(10) int depth) {
        var categoriesDto = categoryQueryService.findChildCategoriesWithDepth(parentCategoryId, depth);
        return ResponseEntity.status(HttpStatus.OK).body(categoriesDto);
    }
}
