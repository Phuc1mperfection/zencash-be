package com.example.zencash.service;

import com.example.zencash.entity.Budget;
import com.example.zencash.entity.Category;
import com.example.zencash.entity.CategoryGroup;
import com.example.zencash.entity.User;
import com.example.zencash.repository.BudgetRepository;
import com.example.zencash.repository.CategoryGroupRepository;
import com.example.zencash.repository.CategoryRepository;
import com.example.zencash.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class DefaultDataService {

    private final BudgetRepository budgetRepository;
    private final CategoryGroupRepository categoryGroupRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetService budgetService;


    @Transactional
    public void createDefaultDataForUser(User user) {
        // Create monthly budget
        Budget monthlyBudget = createMonthlyBudget(user);
        
    }

    private Budget createMonthlyBudget(User user) {
        // Create budget
        Budget budget = new Budget();
        budget.setName("Cuộc sống hàng tháng");
        budget.setTotalAmount(new BigDecimal("5000000"));
        budget.setRemainingAmount(new BigDecimal("5000000"));
        budget.setUser(user);
        budget.setCreatedAt(LocalDateTime.now());
        budget.setUpdatedAt(LocalDateTime.now());
        Budget savedBudget = budgetRepository.save(budget);
        
        // Create category groups and categories
        
        // 1. Sinh hoạt group
        CategoryGroup livingGroup = createCategoryGroup("Sinh hoạt", savedBudget, false);
        
        // Categories for Sinh hoạt
        createCategory("Ăn uống", "fa-solid fa-bowl-food", livingGroup, savedBudget, user, false);
        createCategory("Mua sắm", "fa-solid fa-cart-shopping", livingGroup, savedBudget, user, false);
        
        // 2. Hóa đơn & tiện ích group
        CategoryGroup billsGroup = createCategoryGroup("Hóa đơn & tiện ích", savedBudget, false);
        
        // Categories for Hóa đơn & tiện ích
        createCategory("Tiền điện", "fa-solid fa-bolt", billsGroup, savedBudget, user, false);
        createCategory("Tiền nước", "fa-solid fa-droplet", billsGroup, savedBudget, user, false);
        
        // 3. Di chuyển group
        CategoryGroup transportGroup = createCategoryGroup("Di chuyển", savedBudget, false);
        
        // Categories for Di chuyển
        createCategory("Tiền xăng", "fa-solid fa-motorcycle", transportGroup, savedBudget, user, false);
        
        return savedBudget;
    }
    


    private CategoryGroup createCategoryGroup(String name, Budget budget, boolean isDefault) {
        CategoryGroup group = new CategoryGroup();
        group.setName(name);
        group.setBudget(budget);
        group.setCgDefault(isDefault);
        group.setCreateAt(LocalDateTime.now());
        group.setUpdateAt(LocalDateTime.now());
        return categoryGroupRepository.save(group);
    }
    

    private void createCategory(String name, String icon, CategoryGroup categoryGroup, Budget budget, User user, boolean isDefault) {
        Category category = new Category();
        category.setName(name);
        category.setIcon(icon);
        category.setCategoryGroup(categoryGroup);
        category.setBudget(budget);
        category.setDefaultCat(isDefault);
        category.setCreateAt(LocalDateTime.now());
        category.setUpdateAt(LocalDateTime.now());
        
        // Only set user if it's a user-specific category
        if (!isDefault) {
            category.setUser(user);
        }

        categoryRepository.save(category);
    }
}