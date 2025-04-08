package com.example.zencash.service;

import com.example.zencash.dto.CategoryGroupResponse;
import com.example.zencash.entity.CategoryGroup;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.CategoryGroupRepository;
import com.example.zencash.utils.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryGroupService {

    @Autowired
    private CategoryGroupRepository categoryGroupRepo;

    public CategoryGroupResponse createCategoryGroup(CategoryGroupResponse request) {
        if (categoryGroupRepo.existsByNameIgnoreCase(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_GROUP_ALREADY_EXISTS);
        }

        CategoryGroup categoryGroup = new CategoryGroup();
        categoryGroup.setName(request.getName());
        categoryGroup.setCreateAt(LocalDateTime.now());
        categoryGroup.setUpdateAt(LocalDateTime.now());

        return mapToResponse(categoryGroupRepo.save(categoryGroup));
    }

    public CategoryGroupResponse updateCategoryGroup(Long id, CategoryGroupResponse request) {
        CategoryGroup categoryGroup = categoryGroupRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_GROUP_NOT_FOUND));

        boolean nameExists = categoryGroupRepo.existsByNameIgnoreCase(request.getName()) &&
                !categoryGroup.getName().equalsIgnoreCase(request.getName());
        if (nameExists) {
            throw new AppException(ErrorCode.CATEGORY_GROUP_ALREADY_EXISTS);
        }

        categoryGroup.setName(request.getName());
        categoryGroup.setUpdateAt(LocalDateTime.now());

        return mapToResponse(categoryGroupRepo.save(categoryGroup));
    }

    public void deleteCategoryGroup(Long id) {
        CategoryGroup categoryGroup = categoryGroupRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_GROUP_NOT_FOUND));
        categoryGroupRepo.delete(categoryGroup);
    }

    public List<CategoryGroupResponse> getAllCategoryGroups() {
        List<CategoryGroup> groups = categoryGroupRepo.findAll();
        return groups.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Chuyển từ Entity sang Response
    private CategoryGroupResponse mapToResponse(CategoryGroup categoryGroup) {
        CategoryGroupResponse response = new CategoryGroupResponse();
        response.setId(categoryGroup.getId());
        response.setName(categoryGroup.getName());
        return response;
    }
}

