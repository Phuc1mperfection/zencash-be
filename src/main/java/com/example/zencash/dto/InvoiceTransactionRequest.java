package com.example.zencash.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceTransactionRequest {
    private BigDecimal amount;
    private String note;
    private LocalDate date;
    private String type; // EXPENSE hoặc INCOME
    private String email;
    private Long budgetId;
    private Long categoryId;
}
