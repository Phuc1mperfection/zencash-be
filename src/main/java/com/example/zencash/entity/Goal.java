package com.example.zencash.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Getter
@Setter
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bid", nullable = false)
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "cgid", nullable = false)
    private CategoryGroup categoryGroup;

    @Column(nullable = false)
    private BigDecimal goalAmount;

    @Column(nullable = false)
    private LocalDate month; // ngày đầu tháng

    @Column
    private Boolean repeatMonth;

    @Column
    private Boolean warning = false;

    @Column(name = "create_at")
    private LocalDateTime createAt = LocalDateTime.now();

    @Column(name = "update_at")
    private LocalDateTime updateAt = LocalDateTime.now();
}

