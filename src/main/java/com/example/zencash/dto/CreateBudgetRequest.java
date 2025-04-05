package com.example.zencash.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateBudgetRequest {
    private String name;
    private BigDecimal totalAmount;
    private List<CategoryAllocation> categories;

    @Data
    public static class CategoryAllocation {
        private String categoryName;
        private BigDecimal allocatedAmount;
    }
}
