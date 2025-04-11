package com.example.zencash.controller;

import com.example.zencash.dto.CategoryGroupResponse;
import com.example.zencash.service.CategoryGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category-groups")
public class CategoryGroupController {

    @Autowired
    private CategoryGroupService categoryGroupService;

    @PostMapping
    public ResponseEntity<CategoryGroupResponse> createCategoryGroup(@RequestBody CategoryGroupResponse request) {
        CategoryGroupResponse response = categoryGroupService.createCategoryGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryGroupResponse> update(@PathVariable Long id,
                                                        @RequestBody CategoryGroupResponse request) {
        return ResponseEntity.ok(categoryGroupService.updateCategoryGroup(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryGroupService.deleteCategoryGroup(id);
        return ResponseEntity.ok().build();
    }

    // Lấy tất cả CategoryGroup theo budgetId
    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<List<CategoryGroupResponse>> getAllCategoryGroups(@PathVariable Long budgetId) {
        List<CategoryGroupResponse> responses = categoryGroupService.getAllCategoryGroups(budgetId);
        return ResponseEntity.ok(responses);
    }
}
