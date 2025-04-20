package com.example.zencash.service;

import com.example.zencash.dto.BudgetOverviewResponse;
import com.example.zencash.dto.BudgetRequest;
import com.example.zencash.entity.Budget;
import com.example.zencash.entity.Transaction;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.BudgetRepository;
import com.example.zencash.repository.CategoryRepository;
import com.example.zencash.repository.TransactionRepository;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.utils.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository ;

    public Budget createBudget(BudgetRequest budgetRequest, User user) {
        boolean exists = budgetRepository.existsByNameIgnoreCaseAndUser(budgetRequest.getName(), user);
        if (exists) {
            throw new AppException(ErrorCode.BUDGET_NAME_ALREADY_EXISTS);
        }

        if (budgetRequest.getAmount() == null || budgetRequest.getAmount().compareTo(BigDecimal.ONE) < 0) {
            throw new AppException(ErrorCode.INVALID_AMOUNT);
        }

        Budget budget = new Budget();
        budget.setName(budgetRequest.getName());
        budget.setTotalAmount(budgetRequest.getAmount());
        budget.setRemainingAmount(budgetRequest.getAmount());
        budget.setUser(user);
        budget.setCreatedAt(LocalDateTime.now());
        budget.setUpdatedAt(LocalDateTime.now());

        return budgetRepository.save(budget);
    }

    public Budget updateBudget(Long id, BudgetRequest request, User user) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_BUDGET_ACTION);
        }

        boolean exists = budgetRepository.existsByNameIgnoreCaseAndUserAndIdNot(
                request.getName(), user, id);
        if (exists) {
            throw new AppException(ErrorCode.BUDGET_NAME_ALREADY_EXISTS);
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ONE) < 0) {
            throw new AppException(ErrorCode.INVALID_AMOUNT);
        }

        budget.setName(request.getName());
        budget.setTotalAmount(request.getAmount());
        updateRemainingAmount(budget);
        budget.setUpdatedAt(LocalDateTime.now());

        return budgetRepository.save(budget);
    }


    public void deleteBudget(Long id, User user) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUDGET_NOT_FOUND));

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_BUDGET_ACTION);
        }

        budgetRepository.delete(budget);
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

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
    public BudgetOverviewResponse getBudgetOverview(UUID userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);

        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalSpent = BigDecimal.ZERO;

        for (Budget budget : budgets) {
            totalBudget = totalBudget.add(budget.getTotalAmount());
            BigDecimal spent = budget.getTotalAmount().subtract(budget.getRemainingAmount());
            totalSpent = totalSpent.add(spent);
        }

        BigDecimal totalRemaining = totalBudget.subtract(totalSpent);

        // Nếu totalSpent là âm, set về 0
        if (totalSpent.compareTo(BigDecimal.ZERO) < 0) {
            totalSpent = BigDecimal.ZERO;
        }

        // Tính tỷ lệ phần trăm chi tiêu
        int spentPercentage = totalBudget.compareTo(BigDecimal.ZERO) > 0
                ? totalSpent.multiply(BigDecimal.valueOf(100))
                .divide(totalBudget, 0, RoundingMode.HALF_UP)
                .intValue()
                : 0;

        // Trả về response
        return new BudgetOverviewResponse(
                totalBudget,
                totalSpent,
                totalRemaining,
                spentPercentage
        );
    }


    public Optional<Budget> getBudgetByIdAndUser(UUID userId, Long budgetId) {
        return budgetRepository.findByIdAndUserId(budgetId, userId);
    }
}


