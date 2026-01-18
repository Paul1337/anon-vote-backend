package com.limspyne.anon_vote.poll.application.dto;

import com.limspyne.anon_vote.poll.presenter.dto.GetCategory;

import java.util.List;

public class CategoryDto {
    public String id;
    public String name;
    public List<GetCategory.ResponseDto> children;
    public List<String> path;
}
