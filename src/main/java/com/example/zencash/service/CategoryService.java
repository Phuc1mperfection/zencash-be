package com.example.zencash.service;

import com.example.zencash.dto.CategoryRequest;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryGroupRepository categoryGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    public CategoryResponse addCategory(CategoryRequest categoryRequest) {
        User user = getCurrentUser();

        CategoryGroup categoryGroup = categoryGroupRepository.findById(categoryRequest.getCategoryGroupId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_GROUP_NOT_FOUND));

        Budget budget = budgetRepository.findById(categoryRequest.getBudgetId())
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));

        if (categoryRepository.existsByNameIgnoreCaseAndBudgetId(categoryRequest.getName(), budget.getId())) {
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        if (isValidIcon(categoryRequest.getIcon())) {
            throw new AppException(ErrorCode.ICON_NOT_FOUND);
        }

        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setIcon(categoryRequest.getIcon());
        category.setDefaultCat(categoryRequest.isDefaultCat());
        category.setCategoryGroup(categoryGroup);
        category.setBudget(budget);
        category.setCreateAt(LocalDateTime.now());

        if (!categoryRequest.isDefaultCat()) {
            category.setUser(user);
        }

        Category saved = categoryRepository.save(category);
        return mapToCategoryResponse(saved);
    }

    public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (category.isDefaultCat()) {
            throw new AppException(ErrorCode.UNAUTHORIZED_DEFAULT_CATEGORY_ACTION);
        }

        User user = getCurrentUser();

        if (category.getBudget() == null || !category.getBudget().getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_CATEGORY_ACTION);
        }

        if (!category.getName().equalsIgnoreCase(categoryRequest.getName()) &&
                categoryRepository.existsByNameIgnoreCaseAndBudgetId(categoryRequest.getName(), category.getBudget().getId())) {
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        if (isValidIcon(categoryRequest.getIcon())) {
            throw new AppException(ErrorCode.ICON_NOT_FOUND);
        }

        category.setName(categoryRequest.getName());
        category.setIcon(categoryRequest.getIcon());
        category.setDefaultCat(false);
        category.setUpdateAt(LocalDateTime.now());

        CategoryGroup categoryGroup = categoryGroupRepository.findById(categoryRequest.getCategoryGroupId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_GROUP_NOT_FOUND));
        category.setCategoryGroup(categoryGroup);

        Category updated = categoryRepository.save(category);
        return mapToCategoryResponse(updated);
    }

    public boolean deleteCategory(Long categoryId) {
        Optional<Category> existingCategory = categoryRepository.findById(categoryId);
        if (existingCategory.isPresent()) {
            categoryRepository.delete(existingCategory.get());
            return true;
        }
        return false;
    }

    public List<CategoryResponse> getCategoriesByCategoryGroup(Long categoryGroupId) {
        categoryGroupRepository.findById(categoryGroupId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_GROUP_NOT_FOUND));

        return categoryRepository.findByCategoryGroupId(categoryGroupId)
                .stream().map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getCategoriesByBudget(Long budgetId) {
        budgetRepository.findById(budgetId)
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));

        List<Category> categories = categoryRepository.findByBudgetIdOrDefaultCatTrue(budgetId);
        return categories.stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getIcon(),
                category.getCategoryGroup().getId(),
                category.getUser() != null ? category.getUser().getId() : null,
                category.getBudget().getId(),
                category.getBudget().getName(),
                category.isDefaultCat()
        );
    }

    private boolean isValidIcon(String iconName) {
        Path iconPath = Paths.get("src/main/resources/static/image/icon", iconName);
        return !Files.exists(iconPath);
    }

}
