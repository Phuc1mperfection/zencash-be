package com.example.zencash.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceExtractedDataResponse {
    private BigDecimal amount;
    private String note;
    private LocalDate date;
    private String type; // EXPENSE or INCOME
}
