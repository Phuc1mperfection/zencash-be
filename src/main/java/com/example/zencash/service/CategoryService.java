package com.example.zencash.service;

import com.example.zencash.dto.CategoryResponse;
import com.example.zencash.entity.Budget;
import com.example.zencash.entity.Category;
import com.example.zencash.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory(CategoryResponse categoryResponse, Budget budget) {
        Category category = new Category();
        category.setName(categoryResponse.getName());
        category.setAllocatedAmount(categoryResponse.getAllocatedAmount());
        category.setSpentAmount(BigDecimal.valueOf(0.0));
        category.setBudget(budget);

        return categoryRepository.save(category);
    }
}
