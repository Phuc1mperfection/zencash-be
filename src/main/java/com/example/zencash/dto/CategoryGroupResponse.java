package com.example.zencash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryGroupResponse {
    private Long id;
    private String name;
    private Long budgetId;
    private Boolean cgDefault;
}

