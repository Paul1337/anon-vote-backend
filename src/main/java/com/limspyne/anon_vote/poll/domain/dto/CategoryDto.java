package com.limspyne.anon_vote.poll.domain.dto;

import com.limspyne.anon_vote.poll.presentation.dto.GetCategory;

import java.util.List;

public class CategoryDto {
    public String id;
    public String name;
    public List<GetCategory.ResponseDto> children;
    public List<String> path;
}
