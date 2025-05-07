package com.example.zencash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetComparisonResponse {
    private Long budgetId;
    private String budgetName;
    private BigDecimal allocatedAmount; // Số tiền đã phân bổ cho ngân sách
    private BigDecimal actualSpent; // Số tiền thực tế đã chi
    private BigDecimal remaining; // Số tiền còn lại
    private int usagePercentage; // Phần trăm đã sử dụng
}