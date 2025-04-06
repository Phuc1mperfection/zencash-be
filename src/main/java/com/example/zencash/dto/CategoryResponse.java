package com.example.zencash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor

public class CategoryResponse {
    private Long id;
    private String name;
    private Long categoryGroupId;
    private UUID userId;
    private Long budgetId;

    public CategoryResponse() {

    }
}