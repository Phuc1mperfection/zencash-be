package com.example.zencash.dto;

import com.example.zencash.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class StatisticResultResponse {
    private List<Transaction> transactions;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
}

