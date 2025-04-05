package com.example.zencash.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetRequest {
    private String name;
    private BigDecimal amount;
}