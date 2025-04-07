package com.example.zencash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
// CategoryRequest
public class CategoryRequest {
    private String name;
    private Long categoryGroupId;
    private Long budgetId;
    private boolean isDefault;
}
