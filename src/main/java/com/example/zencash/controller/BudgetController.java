package com.example.zencash.controller;

import com.example.zencash.dto.BudgetRequest;
import com.example.zencash.dto.BudgetResponse;
import com.example.zencash.entity.Budget;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.service.BudgetService;
import com.example.zencash.utils.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    private final UserRepository userRepository;

    public BudgetController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @RequestBody BudgetRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Budget budget = budgetService.createBudget(request, user);

        BudgetResponse response = new BudgetResponse(
                budget.getId(),
                budget.getName(),
                budget.getTotalAmount(),
                budget.getRemainingAmount()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getUserBudgets(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Budget> budgets = budgetService.getBudgetsForUser(user);
        List<BudgetResponse> responses = budgets.stream().map(b -> new BudgetResponse(
                b.getId(), b.getName(), b.getTotalAmount(), b.getRemainingAmount()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,
            @RequestBody BudgetRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Budget updated = budgetService.updateBudget(id, request, user);

        BudgetResponse response = new BudgetResponse(
                updated.getId(),
                updated.getName(),
                updated.getTotalAmount(),
                updated.getRemainingAmount()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        budgetService.deleteBudget(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total-remaining")
    public ResponseEntity<Map<String, BigDecimal>> getUserTotalRemaining(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        BigDecimal totalRemaining = budgetService.calculateUserTotalRemaining(user);

        Map<String, BigDecimal> response = new HashMap<>();
        response.put("total-amount", totalRemaining);

        return ResponseEntity.ok(response);
    }
}


