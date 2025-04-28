package com.example.zencash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long budgetId;
    private Long categoryId;
    private BigDecimal amount;
    private String type;
    private String note;
    private LocalDate date;
    private String categoryName;
}

