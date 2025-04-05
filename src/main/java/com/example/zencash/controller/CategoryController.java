package com.example.zencash.controller;

import com.example.zencash.dto.CategoryResponse;
import com.example.zencash.entity.Budget;
import com.example.zencash.entity.Category;
import com.example.zencash.entity.User;
import com.example.zencash.repository.BudgetRepository;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BudgetRepository budgetRepository;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryResponse categoryResponse, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Find the associated budget for the category
        Budget budget = budgetRepository.findById(categoryResponse.getBudgetid())
                .orElseThrow(() -> new EntityNotFoundException("Budget not found"));

        Category category = categoryService.createCategory(categoryResponse, budget);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponse);
    }
}
