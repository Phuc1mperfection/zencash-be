package com.example.zencash.controller;

import com.example.zencash.dto.CategoryResponse;
import com.example.zencash.entity.User;
import com.example.zencash.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Thêm Category
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryResponse request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(categoryService.createCategory(request, user));
    }


    // Sửa Category
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id,
                                                   @RequestBody CategoryResponse request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request, null));
    }

    // Xóa Category
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id, null);
        return ResponseEntity.ok().build();
    }
}

