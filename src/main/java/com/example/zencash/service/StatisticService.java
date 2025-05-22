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

