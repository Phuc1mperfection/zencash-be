package com.example.zencash.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class CategoryGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column (nullable = false)
    private Boolean cgDefault;

    @Column
    private LocalDateTime createAt = LocalDateTime.now();

    @Column
    private LocalDateTime updateAt = LocalDateTime.now();

    @OneToMany(mappedBy = "categoryGroup")
    private List<Category> categories;

    @ManyToOne
    @JoinColumn(name = "bid")
    private Budget budget;

}

