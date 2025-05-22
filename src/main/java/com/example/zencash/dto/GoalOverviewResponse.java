package com.example.zencash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class GoalOverviewResponse {
    private BigDecimal totalGoalAmount;
    private BigDecimal totalSpent;
    private BigDecimal totalRemaining;
    private BigDecimal spentPercentage;
}
