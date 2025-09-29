package com.limspyne.anon_vote.poll.services;

import com.limspyne.anon_vote.poll.dto.GetCategory;
import com.limspyne.anon_vote.poll.entities.PollCategory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryMapper {
    @Autowired
    private ModelMapper modelMapper;

    public GetCategory.ResponseWithPathDto toDtoWithPath(PollCategory category, int pathDepth) {
        List<String> path = new ArrayList<>();
        PollCategory currentCategory = category;
        while (path.size() < pathDepth && currentCategory.getParentCategory() != null) {
            path.add(currentCategory.getParentCategory().getName());
            currentCategory = currentCategory.getParentCategory();
        }

        if (path.size() < pathDepth) {
            path.add("");
        }

        return new GetCategory.ResponseWithPathDto(category.getId().toString(), category.getName(), path.reversed());
    }

    public GetCategory.ResponseDto toDto(PollCategory category, int maxDepth) {
        return toDto(category, maxDepth, 0);
    }

    private GetCategory.ResponseDto toDto(PollCategory category, int maxDepth, int currentDepth) {
        GetCategory.ResponseDto dto = modelMapper.map(category, GetCategory.ResponseDto.class);
        if (currentDepth < maxDepth) {
            List<GetCategory.ResponseDto> children = category.getChildCategories().stream()
                    .map(child -> toDto(child, maxDepth, currentDepth + 1))
                    .sorted(Comparator.comparing(item -> item.name))
                    .collect(Collectors.toList());
            dto.setChildren(children);
        } else {
            dto.setChildren(Collections.emptyList());
        }
        return dto;
    }
}
