package com.example.zencash.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;  // Mối quan hệ với ngân sách

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal allocatedAmount;  // Số tiền đã phân bổ cho danh mục

    @Column(nullable = false)
    private BigDecimal spentAmount;  // Số tiền đã chi trong danh mục
}

