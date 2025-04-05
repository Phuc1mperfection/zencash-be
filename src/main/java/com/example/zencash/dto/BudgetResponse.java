package com.example.zencash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BudgetResponse {
    private Long id;
    private String name;
    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;
}
