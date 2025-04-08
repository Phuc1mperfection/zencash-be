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

    private static final BigDecimal CONVERSION_RATE = BigDecimal.valueOf(25000); // Tỷ giá VND <-> USD

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    // Chuyển đổi tiền tệ của người dùng
    public void convertCurrencyForUser(String email, String targetCurrency) {
        // Lấy thông tin người dùng từ email (từ bearer token)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String currentCurrency = user.getCurrency(); // Lấy mệnh giá hiện tại của user

        // Nếu người dùng chưa có mệnh giá, mặc định là VND
        if (currentCurrency == null) {
            currentCurrency = "VND";
        }

        // Nếu currency hiện tại trùng với target, không cần chuyển đổi
        if (currentCurrency.equals(targetCurrency)) {
            return;
        }

        // Cập nhật mệnh giá trong các giao dịch của người dùng theo budgetId
        List<Budget> budgets = budgetRepository.findByUserId(user.getId());
        for (Budget budget : budgets) {
            List<Transaction> transactions = transactionRepository.findByBudgetId(budget.getId());
            for (Transaction transaction : transactions) {
                BigDecimal amount = transaction.getAmount();
                BigDecimal convertedAmount = convertAmount(amount, currentCurrency, targetCurrency);
                transaction.setAmount(convertedAmount);
                transactionRepository.save(transaction); // Lưu thay đổi vào cơ sở dữ liệu
            }

            // Cập nhật mệnh giá trong ngân sách của người dùng
            BigDecimal remainingAmount = budget.getRemainingAmount();
            BigDecimal convertedRemainingAmount = convertAmount(remainingAmount, currentCurrency, targetCurrency);
            budget.setRemainingAmount(convertedRemainingAmount);

            BigDecimal totalAmount = budget.getTotalAmount();
            BigDecimal convertedTotalAmount = convertAmount(totalAmount, currentCurrency, targetCurrency);
            budget.setTotalAmount(convertedTotalAmount);

            budgetRepository.save(budget); // Lưu thay đổi ngân sách
        }

        // Cập nhật lại mệnh giá của người dùng
        user.setCurrency(targetCurrency);
        userRepository.save(user);
    }

    private BigDecimal convertAmount(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals("VND") && toCurrency.equals("USD")) {
            return amount.divide(CONVERSION_RATE, 2, RoundingMode.HALF_UP); // Chuyển từ VND sang USD
        }
        if (fromCurrency.equals("USD") && toCurrency.equals("VND")) {
            return amount.multiply(CONVERSION_RATE); // Chuyển từ USD sang VND
        }
        return amount; // Nếu cùng tiền tệ, không thay đổi
    }
}
