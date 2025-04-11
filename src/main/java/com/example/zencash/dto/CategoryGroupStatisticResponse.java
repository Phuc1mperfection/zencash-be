package com.example.zencash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryGroupStatisticResponse {
    private Long categoryGroupId;
    private String categoryGroupName;
    private BigDecimal totalAmount;
}

