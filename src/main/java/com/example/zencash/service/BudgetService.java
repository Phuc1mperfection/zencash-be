package com.example.zencash.service;

import com.example.zencash.dto.BudgetRequest;
import com.example.zencash.dto.BudgetResponse;
import com.example.zencash.dto.CreateBudgetRequest;
import com.example.zencash.entity.Budget;
import com.example.zencash.entity.User;
import com.example.zencash.repository.BudgetRepository;
import com.example.zencash.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Budget createBudget(BudgetRequest budgetRequest, User user) {
        Budget budget = new Budget();
        budget.setName(budgetRequest.getName());
        budget.setTotalAmount(budgetRequest.getAmount());
        budget.setRemainingAmount(budgetRequest.getAmount());
        budget.setUser(user);

        return budgetRepository.save(budget);
    }

    public List<Budget> getBudgetsForUser(User user) {
        return budgetRepository.findByUser(user);
    }
}

