package com.example.zencash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpendingTrendResponse {
    private String period; // Ví dụ: "Jan 2025", "Feb 2025", etc.
    private BigDecimal income; // Tổng thu nhập trong kỳ
    private BigDecimal expense; // Tổng chi tiêu trong kỳ
    private BigDecimal balance; // Chênh lệch (income - expense)
}