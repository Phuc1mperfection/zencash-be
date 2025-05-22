package com.example.zencash.controller;

import com.example.zencash.dto.GoalOverviewResponse;
import com.example.zencash.dto.GoalRequest;
import com.example.zencash.dto.GoalResponse;
import com.example.zencash.service.GoalSpendingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalSpendingController {

    @Autowired private GoalSpendingService goalSpendingService;

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@RequestBody @Valid GoalRequest request) {
        GoalResponse response = goalSpendingService.createGoal(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(@PathVariable Long id, @RequestBody @Valid GoalRequest request) {
        GoalResponse response = goalSpendingService.updateGoal(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id) {
        goalSpendingService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }

    //Lấy Goal thuộc Budget
    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<List<GoalResponse>> getGoalsByBudget(@PathVariable Long budgetId) {
        List<GoalResponse> goals = goalSpendingService.getGoalsByBudget(budgetId);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/overview")
    public GoalOverviewResponse getGoalOverview(
            @RequestParam Long budgetId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        return goalSpendingService.getGoalOverview(budgetId, month.withDayOfMonth(1));
    }



}

