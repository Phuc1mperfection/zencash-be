package com.example.zencash.controller;

import com.example.zencash.dto.CategoryRequest;
import com.example.zencash.dto.CategoryResponse;
import com.example.zencash.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> addCategory(@RequestBody CategoryRequest categoryRequest) {
        try {
            CategoryResponse categoryResponse = categoryService.addCategory(categoryRequest);
            return new ResponseEntity<>(categoryResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryRequest categoryRequest
    ) {
        CategoryResponse categoryResponse = categoryService.updateCategory(categoryId, categoryRequest);
        if (categoryResponse != null) {
            return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        if (categoryService.deleteCategory(categoryId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Lấy tất cả Category theo CategoryGroup
    @GetMapping("/categoryGroup/{categoryGroupId}")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByCategoryGroup(@PathVariable Long categoryGroupId) {
        List<CategoryResponse> categories = categoryService.getCategoriesByCategoryGroup(categoryGroupId);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Lấy tất cả Category theo Budget
    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByBudget(@PathVariable Long budgetId) {
        List<CategoryResponse> categories = categoryService.getCategoriesByBudget(budgetId);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
}