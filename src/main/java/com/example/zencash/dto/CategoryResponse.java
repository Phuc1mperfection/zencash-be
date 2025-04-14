package com.example.zencash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String icon;
    private Long categoryGroupId;
    private UUID userId;
    private Long budgetId;
    private String budgetName;
    private boolean isDefaultCat;
}
