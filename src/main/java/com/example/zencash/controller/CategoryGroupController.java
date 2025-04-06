package com.example.zencash.controller;

import com.example.zencash.dto.CategoryGroupResponse;
import com.example.zencash.service.CategoryGroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category-groups")
public class CategoryGroupController {

    @Autowired
    private CategoryGroupService categoryGroupService;

    // Thêm CategoryGroup
    @PostMapping
    public ResponseEntity<CategoryGroupResponse> create(@RequestBody @Valid CategoryGroupResponse request) {
        return ResponseEntity.ok(categoryGroupService.createCategoryGroup(request));
    }

    // Sửa CategoryGroup
    @PutMapping("/{id}")
    public ResponseEntity<CategoryGroupResponse> update(@PathVariable Long id,
                                                        @RequestBody @Valid CategoryGroupResponse request) {
        return ResponseEntity.ok(categoryGroupService.updateCategoryGroup(id, request));
    }

    // Xóa CategoryGroup
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryGroupService.deleteCategoryGroup(id);
        return ResponseEntity.ok().build();
    }
}
