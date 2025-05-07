package com.example.zencash.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStatisticResponse {
    private Long categoryId;
    private String categoryName;
    private BigDecimal totalAmount;
    private Long count;
    
 
}