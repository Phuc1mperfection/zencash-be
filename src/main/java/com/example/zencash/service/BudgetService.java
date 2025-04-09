package com.example.zencash.service;

import com.example.zencash.dto.BudgetRequest;
import com.example.zencash.entity.Budget;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.BudgetRepository;
import com.example.zencash.repository.CategoryRepository;
import com.example.zencash.repository.TransactionRepository;
import com.example.zencash.utils.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Budget createBudget(BudgetRequest budgetRequest, User user) {
        Budget budget = new Budget();
        budget.setName(budgetRequest.getName());
        budget.setTotalAmount(budgetRequest.getAmount());
        budget.setRemainingAmount(budgetRequest.getAmount());
        budget.setUser(user);
        budget.setCreatedAt(LocalDateTime.now());
        budget.setUpdatedAt(LocalDateTime.now());

        return budgetRepository.save(budget);
    }

    public void updateRemainingAmount(Budget budget) {
        BigDecimal income = transactionRepository.sumAmountByBudgetIdAndType(budget.getId(), "INCOME");
        BigDecimal expense = transactionRepository.sumAmountByBudgetIdAndType(budget.getId(), "EXPENSE");

        BigDecimal remaining = budget.getTotalAmount()
                .add(income != null ? income : BigDecimal.ZERO)
                .subtract(expense != null ? expense : BigDecimal.ZERO);

        budget.setRemainingAmount(remaining);
        budget.setUpdatedAt(LocalDateTime.now());
        budgetRepository.save(budget);
    }

    public List<Budget> getBudgetsForUser(User user) {
        List<Budget> budgets = budgetRepository.findByUser(user);
        for (Budget b : budgets) {
            updateRemainingAmount(b);
        }
        return budgets;
    }

    public Budget getBudgetById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));
    }

    //Tính tổng số tiền trong các ví của User
    public BigDecimal calculateUserTotalRemaining(User user) {
        return budgetRepository.findByUser(user).stream()
                .map(Budget::getRemainingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}


