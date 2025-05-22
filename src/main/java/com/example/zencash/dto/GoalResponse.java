package com.example.zencash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class GoalResponse {
    private Long id;
    private Long budgetId;
    private Long categoryGroupId;
    private BigDecimal goalAmount;
    private BigDecimal totalExpense;
    private LocalDate month;
    private Boolean repeatMonth;
    private Boolean warning; // true nếu chi >= 80%
}


