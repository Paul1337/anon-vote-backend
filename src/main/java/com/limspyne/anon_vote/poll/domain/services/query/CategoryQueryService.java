package com.limspyne.anon_vote.poll.domain.services.query;

import com.limspyne.anon_vote.poll.domain.entities.PollCategory;
import com.limspyne.anon_vote.poll.domain.exceptions.CategoryNotFoundException;
import com.limspyne.anon_vote.poll.infrastructure.mappers.CategoryMapper;
import com.limspyne.anon_vote.poll.infrastructure.repositories.CategoryRepository;
import com.limspyne.anon_vote.poll.presentation.dto.GetCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryQueryService {
    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<GetCategory.ResponseWithPathDto> searchCategories(String name, Pageable pageable) {
        List<PollCategory> filteredCategories = categoryRepository.findByNameStartsWithIgnoreCaseWithDepth3(name, pageable);
        return filteredCategories.stream().map(item -> categoryMapper.toDtoWithPath(item, 3)).toList();
    }

    @Transactional(readOnly = true)
    public List<GetCategory.ResponseDto> findRootCategoriesWithDepth(int depth) {
        var allCategoriesInSubtree = categoryRepository.findAllChildrenByRootPathWithMaxDepth("", depth);
        var allCategoriesInSubtreeDto = mergeCategoriesChildren(allCategoriesInSubtree);

        Set<UUID> filteredIds = allCategoriesInSubtree.stream()
                .filter(category -> category.getParentCategory() == null)
                .map(PollCategory::getId)
                .collect(Collectors.toSet());

        return allCategoriesInSubtreeDto.stream()
                .filter(dto -> filteredIds.contains(dto.getId()))
                .sorted(Comparator.comparing(GetCategory.ResponseDto::getName))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GetCategory.ResponseDto> findChildCategoriesWithDepth(String parentCategoryId, int depth) {
        var parentCategory = categoryRepository.findById(UUID.fromString(parentCategoryId)).orElseThrow(CategoryNotFoundException::new);
        var allCategoriesInSubtree = categoryRepository.findAllChildrenByRootPathWithMaxDepth(parentCategory.getPath(), depth);
        var allCategoriesInSubtreeDto = mergeCategoriesChildren(allCategoriesInSubtree);
        var parentCategoryUUID = UUID.fromString(parentCategoryId);

        Set<UUID> filteredIds = allCategoriesInSubtree.stream()
                .filter(category -> category.getParentCategory().getId().equals(parentCategoryUUID))
                .map(PollCategory::getId)
                .collect(Collectors.toSet());

        return allCategoriesInSubtreeDto.stream()
                .filter(dto -> filteredIds.contains(dto.getId()))
                .sorted(Comparator.comparing(GetCategory.ResponseDto::getName))
                .collect(Collectors.toList());
    }

    private List<GetCategory.ResponseDto> mergeCategoriesChildren(List<PollCategory> categories) {
        Map<UUID, GetCategory.ResponseDto> categoryDtoById = categories.stream()
                .collect(Collectors.toMap(PollCategory::getId, category -> categoryMapper.toDto(category, 0)));
        for (var category: categories) {
            if (category.getParentCategory() != null) {
                var categoryParentDto = categoryDtoById.get(category.getParentCategory().getId());
                if (categoryParentDto != null) {
                    categoryParentDto.getChildren().add(categoryDtoById.get(category.getId()));
                }
            }
        }
        return categoryDtoById.values().stream().toList();
    }


}
