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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        // Lấy thông tin user từ token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy CategoryGroup
        CategoryGroup categoryGroup = categoryGroupRepository.findById(categoryRequest.getCategoryGroupId())
                .orElseThrow(() -> new RuntimeException("Invalid CategoryGroup"));

        // Lấy Budget
        Budget budget = budgetRepository.findById(categoryRequest.getBudgetId())
                .orElseThrow(() -> new RuntimeException("Invalid Budget"));

        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setIsDefault(categoryRequest.isDefault());
        category.setCategoryGroup(categoryGroup);
        category.setBudget(budget);

        // Gán user vào category nếu không phải default
        if (!categoryRequest.isDefault()) {
            category.setUser(user);
        }

        Category saved = categoryRepository.save(category);

        return new CategoryResponse(
                saved.getId(),
                saved.getName(),
                saved.getCategoryGroup().getId(),
                saved.getUser() != null ? saved.getUser().getId() : null,
                saved.getBudget().getId(),
                saved.isDefault()
        );
    }


    public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        // Nếu là mặc định thì không cho sửa
        if (category.isDefault()) {
            throw new AppException(ErrorCode.UNAUTHORIZED_CATEGORY_ACTION);
        }

        // Lấy user hiện tại từ token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Chỉ cho phép nếu user là chủ sở hữu category
        if (category.getUser() == null || !category.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_CATEGORY_ACTION);
        }

        category.setName(categoryRequest.getName());
        category.setIsDefault(false); // luôn là false vì user không thể tạo default
        category.setUpdateAt(LocalDateTime.now());

        CategoryGroup categoryGroup = categoryGroupRepository.findById(categoryRequest.getCategoryGroupId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_GROUP_NOT_FOUND));
        category.setCategoryGroup(categoryGroup);

        Budget budget = budgetRepository.findById(categoryRequest.getBudgetId())
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));
        category.setBudget(budget);

        // Gán lại user phòng trường hợp null
        category.setUser(user);

        Category updated = categoryRepository.save(category);

        return new CategoryResponse(
                updated.getId(),
                updated.getName(),
                updated.getCategoryGroup().getId(),
                updated.getUser() != null ? updated.getUser().getId() : null,
                updated.getBudget().getId(),
                updated.isDefault()
        );
    }


    // Xoá Category
    public boolean deleteCategory(Long categoryId) {
        Optional<Category> existingCategory = categoryRepository.findById(categoryId);
        if (existingCategory.isPresent()) {
            categoryRepository.delete(existingCategory.get());
            return true;
        }
        return false;
    }

    // Lấy tất cả Category theo CategoryGroup
    public List<CategoryResponse> getCategoriesByCategoryGroup(Long categoryGroupId) {
        List<Category> categories = categoryRepository.findByCategoryGroupId(categoryGroupId);
        return categories.stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getCategoryGroup().getId(),
                        category.getUser() != null ? category.getUser().getId() : null,
                        category.getBudget().getId(),
                        category.isDefault()
                ))
                .toList();
    }

    // Lấy tất cả Category theo Budget
    public List<CategoryResponse> getCategoriesByBudget(Long budgetId) {
        List<Category> categories = categoryRepository.findByBudgetId(budgetId);
        return categories.stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getCategoryGroup().getId(),
                        category.getUser() != null ? category.getUser().getId() : null,
                        category.getBudget().getId(),
                        category.isDefault()
                ))
                .toList();
    }
}


