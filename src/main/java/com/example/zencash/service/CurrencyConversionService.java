package com.example.zencash.service;

import com.example.zencash.entity.Budget;
import com.example.zencash.entity.Transaction;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.BudgetRepository;
import com.example.zencash.repository.TransactionRepository;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.utils.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CurrencyConversionService {

    private static final BigDecimal CONVERSION_RATE = BigDecimal.valueOf(25000);
    private static final int USD_SCALE = 2;
    private static final int VND_SCALE = 0;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    public void convertCurrencyForUser(String email, String targetCurrency) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String currentCurrency = user.getCurrency() == null ? "VND" : user.getCurrency();

        if (currentCurrency.equals(targetCurrency)) return;

        List<Budget> budgets = budgetRepository.findByUserId(user.getId());

        for (Budget budget : budgets) {
            convertTransactions(budget.getId(), currentCurrency, targetCurrency);
            convertBudgetAmounts(budget, currentCurrency, targetCurrency);
        }

        user.setCurrency(targetCurrency);
        userRepository.save(user);
    }

    private void convertTransactions(Long budgetId, String fromCurrency, String toCurrency) {
        List<Transaction> transactions = transactionRepository.findByBudgetId(budgetId);
        for (Transaction transaction : transactions) {
            BigDecimal original = transaction.getAmount();
            BigDecimal converted = convertAmount(original, fromCurrency, toCurrency);

            if (original.compareTo(converted) != 0) {
                transaction.setAmount(converted);
                transactionRepository.save(transaction);
            }
        }
    }

    private void convertBudgetAmounts(Budget budget, String fromCurrency, String toCurrency) {
        boolean updated = false;

        BigDecimal newRemaining = convertAmount(budget.getRemainingAmount(), fromCurrency, toCurrency);
        if (budget.getRemainingAmount().compareTo(newRemaining) != 0) {
            budget.setRemainingAmount(newRemaining);
            updated = true;
        }

        BigDecimal newTotal = convertAmount(budget.getTotalAmount(), fromCurrency, toCurrency);
        if (budget.getTotalAmount().compareTo(newTotal) != 0) {
            budget.setTotalAmount(newTotal);
            updated = true;
        }

        if (updated) {
            budgetRepository.save(budget);
        }
    }

    private BigDecimal convertAmount(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals("VND") && toCurrency.equals("USD")) {
            return amount.divide(CONVERSION_RATE, USD_SCALE, RoundingMode.HALF_UP);
        }

        if (fromCurrency.equals("USD") && toCurrency.equals("VND")) {
            return amount.multiply(CONVERSION_RATE).setScale(VND_SCALE, RoundingMode.HALF_UP);
        }

        return amount;
    }
}
