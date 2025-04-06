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
    @GeneratedValue
    private long id;

    @Getter
    @Column
    private String name;

    @Column
    private LocalDateTime createAt = LocalDateTime.now();

    @Column
    private LocalDateTime updateAt = LocalDateTime.now();

    @OneToMany(mappedBy = "categoryGroup")
    private List<Category> categories;


}

