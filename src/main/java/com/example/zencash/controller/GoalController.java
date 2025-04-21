package com.example.zencash.controller;

import com.example.zencash.dto.GoalRequest;
import com.example.zencash.dto.GoalResponse;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.service.GoalService;
import com.example.zencash.utils.ErrorCode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired private GoalService goalService;

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@RequestBody @Valid GoalRequest request) {
        GoalResponse response = goalService.createGoal(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(@PathVariable Long id, @RequestBody @Valid GoalRequest request) {
        GoalResponse response = goalService.updateGoal(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<List<GoalResponse>> getGoalsByBudget(@PathVariable Long budgetId) {
        List<GoalResponse> goals = goalService.getGoalsByBudget(budgetId);
        return ResponseEntity.ok(goals);
    }
}

