package com.limspyne.anon_vote.poll.controllers;

import com.limspyne.anon_vote.poll.dto.GetCategory;
import com.limspyne.anon_vote.poll.entities.PollCategory;
import com.limspyne.anon_vote.poll.exceptions.CategoryNotFoundException;
import com.limspyne.anon_vote.poll.repositories.CategoryRepository;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("categories")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

//    public GetCategory.Response mapCategoryToResponse(PollCategory category) {
//        return new GetCategory.Response(category.getId().toString(), category.getName(), new ArrayList<>());
//    }

    @GetMapping({"/{id}", "/"})
    ResponseEntity<List<GetCategory.ResponseDto>> getCategories(@PathVariable(name = "id", required = false) String categoryId, @RequestParam(name = "depth", defaultValue = "2") @Min(1) @Max(10) int depth) {
        List<PollCategory> level = null;
        if (categoryId == null) {
            level = categoryRepository.findByParentCategoryIdNull();
        } else {
            UUID categoryUUID = UUID.fromString(categoryId);
            var parentCategory = categoryRepository.findById(categoryUUID).orElseThrow(() -> new CategoryNotFoundException(categoryUUID));
            level = parentCategory.getChildCategories();
        }

        List<GetCategory.ResponseDto> responseDtoCategories = new ArrayList<>();
        Map<UUID, GetCategory.ResponseDto> pollCategoryResponseMap = level.stream().collect(Collectors.toMap(PollCategory::getId, item -> modelMapper.map(item, GetCategory.ResponseDto.class)));
        pollCategoryResponseMap.forEach((key, value) -> responseDtoCategories.add(value));
        while (depth > 1) {
            ArrayList<PollCategory> newLevel = new ArrayList<>();
            level.forEach(category -> {
                List<PollCategory> children = category.getChildCategories();
                newLevel.addAll(children);
                for (var child: children) {
                    pollCategoryResponseMap.put(child.getId(), modelMapper.map(child, GetCategory.ResponseDto.class));
                }
                pollCategoryResponseMap.get(category.getId()).children = children.stream().map(item -> pollCategoryResponseMap.get(item.getId())).toList();
            });
            level = newLevel;
            depth--;
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseDtoCategories);
    }
}
