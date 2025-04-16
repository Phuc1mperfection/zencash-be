package com.example.zencash.service;

import com.example.zencash.dto.StatisticResultResponse;
import com.example.zencash.dto.TopTransactionResponse;
import com.example.zencash.entity.Transaction;
import com.example.zencash.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final TransactionRepository transactionRepository;

    public TopTransactionResponse getTopTransactions(Long budgetId) {
        List<Transaction> newest = transactionRepository.findTop5ByBudgetIdOrderByDateDesc(budgetId);
        List<Transaction> oldest = transactionRepository.findTop5ByBudgetIdOrderByDateAsc(budgetId);
        return new TopTransactionResponse(newest, oldest);
    }

    // Lấy các giao dịch theo ngày
    public List<Transaction> getTransactionsByDate(Long budgetId, LocalDate date) {
        return transactionRepository.findByBudgetIdAndDateEquals(budgetId, date);
    }

    // Lấy các giao dịch theo tuần
    public List<Transaction> getTransactionsByWeek(Long budgetId, LocalDate startDate) {
        return transactionRepository.findByBudgetIdAndWeek(budgetId, startDate);
    }

    // Lấy các giao dịch theo tháng
    public List<Transaction> getTransactionsByMonth(Long budgetId, int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return transactionRepository.findByBudgetIdAndDateBetween(budgetId, startDate, endDate);
    }

    // Lấy các giao dịch theo năm
    public List<Transaction> getTransactionsByYear(Long budgetId, int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return transactionRepository.findByBudgetIdAndDateBetween(budgetId, startDate, endDate);
    }

    // Lấy tất cả giao dịch của người dùng
    public List<Transaction> getAllTransactionsByUser(UUID userId) {
        return transactionRepository.findByBudget_UserId(userId);
    }

    // Hàm thống kê thu/chi
    public StatisticResultResponse buildStatisticResult(List<Transaction> transactions) {
        BigDecimal income = transactions.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expense = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new StatisticResultResponse(transactions, income, expense);
    }
}

