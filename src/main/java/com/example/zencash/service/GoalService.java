package com.example.zencash.service;

import com.example.zencash.dto.GoalRequest;
import com.example.zencash.dto.GoalResponse;
import com.example.zencash.entity.*;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.*;
import com.example.zencash.utils.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {
    @Autowired private GoalRepository goalRepository;
    @Autowired private CategoryGroupRepository categoryGroupRepository;
    @Autowired private BudgetRepository budgetRepository;
    @Autowired private TransactionRepository transactionRepository;

    public GoalResponse createGoal(GoalRequest request) {
        Budget budget = budgetRepository.findById(request.getBudgetId())
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));

        CategoryGroup categoryGroup = categoryGroupRepository.findById(request.getCategoryGroupId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_GROUP_NOT_FOUND));

        Goal goal = new Goal();
        goal.setBudget(budget);
        goal.setCategoryGroup(categoryGroup);
        goal.setGoalAmount(request.getGoalAmount());
        goal.setMonth(request.getMonth().withDayOfMonth(1)); // đảm bảo là ngày đầu tháng
        goal.setRepeatMonth(Boolean.TRUE.equals(request.getRepeatMonth()));
        goal.setCreateAt(LocalDateTime.now());
        goal.setUpdateAt(LocalDateTime.now());

        Goal saved = goalRepository.save(goal);

        BigDecimal totalSpent = calculateTotalSpent(budget.getId(), categoryGroup.getId(), saved.getMonth());

        return mapToResponse(saved, totalSpent);
    }

    public GoalResponse updateGoal(Long id, GoalRequest request) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.GOAL_NOT_FOUND));

        if (!goal.getBudget().getId().equals(request.getBudgetId())) {
            Budget budget = budgetRepository.findById(request.getBudgetId())
                    .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));
            goal.setBudget(budget);
        }

        if (!goal.getCategoryGroup().getId().equals(request.getCategoryGroupId())) {
            CategoryGroup categoryGroup = categoryGroupRepository.findById(request.getCategoryGroupId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_GROUP_NOT_FOUND));
            goal.setCategoryGroup(categoryGroup);
        }

        goal.setGoalAmount(request.getGoalAmount());
        goal.setMonth(request.getMonth().withDayOfMonth(1));
        goal.setRepeatMonth(Boolean.TRUE.equals(request.getRepeatMonth()));
        goal.setUpdateAt(LocalDateTime.now());

        Goal saved = goalRepository.save(goal);
        BigDecimal totalSpent = calculateTotalSpent(saved.getBudget().getId(), saved.getCategoryGroup().getId(), saved.getMonth());

        return mapToResponse(saved, totalSpent);
    }

    public void deleteGoal(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.GOAL_NOT_FOUND));
        goalRepository.delete(goal);
    }

    public List<GoalResponse> getGoalsByBudget(Long budgetId) {
        List<Goal> goals = goalRepository.findByBudgetId(budgetId);
        return goals.stream()
                .map(goal -> {
                    BigDecimal totalSpent = calculateTotalSpent(budgetId, goal.getCategoryGroup().getId(), goal.getMonth());
                    return mapToResponse(goal, totalSpent);
                }).collect(Collectors.toList());
    }

    private BigDecimal calculateTotalSpent(Long budgetId, Long categoryGroupId, LocalDate month) {
        int year = month.getYear();
        int monthValue = month.getMonthValue();

        return transactionRepository.getTotalExpenseByCategoryGroupInMonth(budgetId, categoryGroupId, year, monthValue);
    }

    private GoalResponse mapToResponse(Goal goal, BigDecimal totalSpent) {
        boolean warning = totalSpent.compareTo(goal.getGoalAmount().multiply(BigDecimal.valueOf(0.8))) >= 0;

        return new GoalResponse(
                goal.getId(),
                goal.getBudget().getId(),
                goal.getCategoryGroup().getId(),
                goal.getGoalAmount(),
                totalSpent,
                goal.getMonth(),
                goal.getRepeatMonth(),
                warning
        );
    }

}

