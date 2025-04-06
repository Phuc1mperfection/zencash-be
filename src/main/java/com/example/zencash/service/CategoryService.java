package com.example.zencash.service;

import com.example.zencash.dto.CategoryResponse;
import com.example.zencash.entity.Budget;
import com.example.zencash.entity.Category;
import com.example.zencash.entity.CategoryGroup;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.BudgetRepository;
import com.example.zencash.repository.CategoryGroupRepository;
import com.example.zencash.repository.CategoryRepository;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.utils.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private CategoryGroupRepository categoryGroupRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BudgetRepository budgetRepo;

    // Thêm Category
    public CategoryResponse createCategory(CategoryResponse request, User user) {
        if (!user.getRoles().contains("USER") && !user.getRoles().contains("ADMIN")) {
            throw new AppException(ErrorCode.UNAUTHORIZED_CATEGORY_ACTION);
        }

        CategoryGroup categoryGroup = categoryGroupRepo.findById(request.getCategoryGroupId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_GROUP_NOT_FOUND));

        User categoryUser = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED_CATEGORY_ACTION));

        Budget budget = budgetRepo.findById(request.getBudgetId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Category category = new Category();
        category.setName(request.getName());
        category.setCategoryGroup(categoryGroup);
        category.setUser(categoryUser);
        category.setBudget(budget);
        category.setCreateAt(LocalDateTime.now());
        category.setUpdateAt(LocalDateTime.now());

        Category saved = categoryRepo.save(category);
        return mapToResponse(saved);
    }

    // Sửa Category
    public CategoryResponse updateCategory(Long id, CategoryResponse request, User user) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (!user.getRoles().contains("USER") && !user.getRoles().contains("ADMIN")) {
            throw new AppException(ErrorCode.UNAUTHORIZED_CATEGORY_ACTION);
        }

        CategoryGroup categoryGroup = categoryGroupRepo.findById(request.getCategoryGroupId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_GROUP_NOT_FOUND));

        User categoryUser = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED_CATEGORY_ACTION));

        Budget budget = budgetRepo.findById(request.getBudgetId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        category.setName(request.getName());
        category.setCategoryGroup(categoryGroup);
        category.setUser(categoryUser);
        category.setBudget(budget);
        category.setUpdateAt(LocalDateTime.now());

        return mapToResponse(categoryRepo.save(category));
    }

    // Xóa Category
    public void deleteCategory(Long id, User user) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (!user.getRoles().contains("USER") && !user.getRoles().contains("ADMIN")) {
            throw new AppException(ErrorCode.UNAUTHORIZED_CATEGORY_ACTION);
        }

        categoryRepo.delete(category);
    }

    // Chuyển từ Entity sang Response
    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setCategoryGroupId(category.getCategoryGroup().getId());
        response.setUserId(category.getUser().getId());
        response.setBudgetId(category.getBudget().getId());
        return response;
    }
}
