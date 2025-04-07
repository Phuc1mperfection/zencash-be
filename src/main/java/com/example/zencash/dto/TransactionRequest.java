package com.example.zencash.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {
    private Long budgetId;
    private Long categoryId;
    private BigDecimal amount;
    private String type; // "INCOME" / "EXPENSE"
    private LocalDate date;
    private String note;
}
