package com.example.zencash.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceExtractedDataResponse {
    private Long id;
    private Long budgetId;
    private Long categoryId;
    private BigDecimal amount;
    private String type; // EXPENSE or INCOME
    private String note;
    private LocalDate date;
}
