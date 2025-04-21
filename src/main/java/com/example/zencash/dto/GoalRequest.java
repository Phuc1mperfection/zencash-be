package com.example.zencash.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GoalRequest {
    private Long budgetId;
    private Long categoryGroupId;
    private BigDecimal goalAmount;
    private LocalDate month;
    private Boolean repeatMonth;
}

