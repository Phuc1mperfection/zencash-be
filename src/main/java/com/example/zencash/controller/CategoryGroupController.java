package com.example.zencash.controller;

import com.example.zencash.dto.CategoryGroupResponse;
import com.example.zencash.service.CategoryGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category-groups")
public class CategoryGroupController {

    @Autowired
    private CategoryGroupService categoryGroupService;

    // Thêm CategoryGroup
    @PostMapping
    public ResponseEntity<CategoryGroupResponse> createCategoryGroup(@RequestBody CategoryGroupResponse request) {
        CategoryGroupResponse response = categoryGroupService.createCategoryGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Sửa CategoryGroup
    @PutMapping("/{id}")
    public ResponseEntity<CategoryGroupResponse> update(@PathVariable Long id,
                                                        @RequestBody CategoryGroupResponse request) {
        return ResponseEntity.ok(categoryGroupService.updateCategoryGroup(id, request));
    }

    // Xóa CategoryGroup
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryGroupService.deleteCategoryGroup(id);
        return ResponseEntity.ok().build();
    }
}
