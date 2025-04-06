package com.example.zencash.service;

import com.example.zencash.dto.CategoryGroupResponse;
import com.example.zencash.entity.CategoryGroup;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.CategoryGroupRepository;
import com.example.zencash.utils.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CategoryGroupService {

    @Autowired
    private CategoryGroupRepository categoryGroupRepo;

    // Thêm CategoryGroup
    public CategoryGroupResponse createCategoryGroup(CategoryGroupResponse request) {
        CategoryGroup categoryGroup = new CategoryGroup();
        categoryGroup.setName(request.getName());
        categoryGroup.setCreateAt(LocalDateTime.now());
        categoryGroup.setUpdateAt(LocalDateTime.now());

        CategoryGroup saved = categoryGroupRepo.save(categoryGroup);
        return mapToResponse(saved);
    }

    // Sửa CategoryGroup
    public CategoryGroupResponse updateCategoryGroup(Long id, CategoryGroupResponse request) {
        CategoryGroup categoryGroup = categoryGroupRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_GROUP_NOT_FOUND));

        categoryGroup.setName(request.getName());
        categoryGroup.setUpdateAt(LocalDateTime.now());

        return mapToResponse(categoryGroupRepo.save(categoryGroup));
    }

    // Xóa CategoryGroup
    public void deleteCategoryGroup(Long id) {
        CategoryGroup categoryGroup = categoryGroupRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_GROUP_NOT_FOUND));

        categoryGroupRepo.delete(categoryGroup);
    }

    // Chuyển từ Entity sang Response
    private CategoryGroupResponse mapToResponse(CategoryGroup categoryGroup) {
        CategoryGroupResponse response = new CategoryGroupResponse();
        response.setId(categoryGroup.getId());
        response.setName(categoryGroup.getName());
        return response;
    }
}

