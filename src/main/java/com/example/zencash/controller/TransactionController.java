package com.example.zencash.controller;

import com.example.zencash.dto.CategoryGroupStatisticResponse;
import com.example.zencash.dto.TransactionRequest;
import com.example.zencash.dto.TransactionResponse;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.service.TransactionService;
import com.example.zencash.utils.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.createTransaction(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(@PathVariable Long id, @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean deleted = transactionService.deleteTransaction(id);
        return ResponseEntity.ok(Collections.singletonMap("deleted", deleted));
    }

    //Lấy danh sách transaction theo budget
    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<List<TransactionResponse>> getByBudget(@PathVariable Long budgetId) {
        return ResponseEntity.ok(transactionService.getByBudget(budgetId));
    }

//    //Thống kê thu chi toàn bộ
//    @GetMapping("/summary")
//    public ResponseEntity<Map<String, BigDecimal>> getUserIncomeExpense(
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        User user = userRepository.findByEmail(userDetails.getUsername())
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//
//        Map<String, BigDecimal> result = transactionService.calculateUserIncomeExpense(user);
//        return ResponseEntity.ok(result);
//    }
//
//    //Thống kê theo CategoryGroup
//    @GetMapping("/statistics/category-group/{budgetId}")
//    public ResponseEntity<List<CategoryGroupStatisticResponse>> getStatisticsByCategoryGroup(@PathVariable Long budgetId) {
//        List<CategoryGroupStatisticResponse> stats = transactionService.getCategoryGroupStatistics(budgetId);
//        return ResponseEntity.ok(stats);
//    }
//    @GetMapping("/top-expenses")
//    public ResponseEntity<List<TransactionResponse>> getTopExpenses(
//            @RequestParam(defaultValue = "10") int limit,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        User user = userRepository.findByEmail(userDetails.getUsername())
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//
//        return ResponseEntity.ok(transactionService.getTopExpenses(limit, user));
//    }

}
