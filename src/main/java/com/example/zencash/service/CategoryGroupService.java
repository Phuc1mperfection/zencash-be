package com.example.zencash.service;

import com.example.zencash.dto.CategoryGroupResponse;
import com.example.zencash.entity.Budget;
import com.example.zencash.entity.CategoryGroup;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.BudgetRepository;
import com.example.zencash.repository.CategoryGroupRepository;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.utils.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryGroupService {

    @Autowired
    private CategoryGroupRepository categoryGroupRepo;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    public CategoryGroupResponse createCategoryGroup(CategoryGroupResponse request) {
        User user = getCurrentUser();

        Budget budget = budgetRepository.findById(request.getBudgetId())
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_BUDGET_ACCESS);
        }

        if (categoryGroupRepo.existsByNameIgnoreCaseAndBudgetId(request.getName(), budget.getId())) {
            throw new AppException(ErrorCode.CATEGORY_GROUP_ALREADY_EXISTS);
        }

        CategoryGroup group = new CategoryGroup();
        group.setName(request.getName());
        group.setCreateAt(LocalDateTime.now());
        group.setUpdateAt(LocalDateTime.now());
        group.setCgDefault(false); // user-created
        group.setBudget(budget);

        return mapToResponse(categoryGroupRepo.save(group));
    }

    public CategoryGroupResponse updateCategoryGroup(Long id, CategoryGroupResponse request) {
        User user = getCurrentUser();

        CategoryGroup categoryGroup = categoryGroupRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_GROUP_NOT_FOUND));

        if (categoryGroup.getCgDefault()) {
            throw new AppException(ErrorCode.UNAUTHORIZED_DEFAULT_CATEGORY_ACTION);
        }

        if (!categoryGroup.getBudget().getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_CATEGORY_GROUP_ACTION);
        }

        // Check trùng tên (bỏ qua nếu trùng chính nó)
        boolean nameExists = categoryGroupRepo.existsByNameIgnoreCaseAndBudgetId(request.getName(), categoryGroup.getBudget().getId()) &&
                !categoryGroup.getName().equalsIgnoreCase(request.getName());
        if (nameExists) {
            throw new AppException(ErrorCode.CATEGORY_GROUP_ALREADY_EXISTS);
        }

        categoryGroup.setName(request.getName());
        categoryGroup.setUpdateAt(LocalDateTime.now());

        return mapToResponse(categoryGroupRepo.save(categoryGroup));
    }

    public void deleteCategoryGroup(Long id) {
        User user = getCurrentUser();

        CategoryGroup group = categoryGroupRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_GROUP_NOT_FOUND));

        if (group.getCgDefault()) {
            throw new AppException(ErrorCode.UNAUTHORIZED_DEFAULT_CATEGORY_ACTION);
        }

        if (!group.getBudget().getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_CATEGORY_GROUP_ACTION);
        }

        categoryGroupRepo.delete(group);
    }

    public List<CategoryGroupResponse> getAllCategoryGroups(Long budgetId) {
        User user = getCurrentUser();

        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_BUDGET_ACCESS);
        }

        List<CategoryGroup> groups = categoryGroupRepo.findByBudgetId(budgetId);
        return groups.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private CategoryGroupResponse mapToResponse(CategoryGroup categoryGroup) {
        return new CategoryGroupResponse(
                categoryGroup.getId(),
                categoryGroup.getName(),
                categoryGroup.getBudget() != null ? categoryGroup.getBudget().getId() : null,
                categoryGroup.getCgDefault()
        );
    }
}
