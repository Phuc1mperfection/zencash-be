package com.example.zencash.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor

public class CategoryResponse {
    private Long budgetid;
    private String name;
    private BigDecimal allocatedAmount;
    private BigDecimal spentAmount;
}
