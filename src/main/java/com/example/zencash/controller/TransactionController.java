package com.example.zencash.controller;

import com.example.zencash.dto.BudgetComparisonResponse;
import com.example.zencash.dto.CategoryGroupStatisticResponse;
import com.example.zencash.dto.CategoryStatisticResponse;
import com.example.zencash.dto.SpendingTrendResponse;
import com.example.zencash.dto.TransactionRequest;
import com.example.zencash.dto.TransactionResponse;
import com.example.zencash.entity.Transaction;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.TransactionRepository;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.service.TransactionService;
import com.example.zencash.utils.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Year;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;

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

    // Lấy danh sách transaction theo budget
    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<List<TransactionResponse>> getByBudget(@PathVariable Long budgetId) {
        return ResponseEntity.ok(transactionService.getByBudget(budgetId));
    }

    // Thống kê thu chi toàn bộ
    @GetMapping("/summary")
    public ResponseEntity<Map<String, BigDecimal>> getUserIncomeExpense(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Map<String, BigDecimal> result = transactionService.calculateUserIncomeExpense(user);
        return ResponseEntity.ok(result);
    }

    // Thống kê theo CategoryGroup
    @GetMapping("/statistics/category-group/{budgetId}")
    public ResponseEntity<List<CategoryGroupStatisticResponse>> getStatisticsByCategoryGroup(
            @PathVariable Long budgetId) {
        List<CategoryGroupStatisticResponse> stats = transactionService.getCategoryGroupStatistics(budgetId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/category-group")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByCategoryGroup(
            @RequestParam Long budgetId,
            @RequestParam Long categoryGroupId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month
    ) {
        List<TransactionResponse> transactions = transactionService.getByCategoryGroup(budgetId, categoryGroupId, month);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/top-expenses")
    public ResponseEntity<List<TransactionResponse>> getTopExpenses(
            @RequestParam(defaultValue = "3") int limit,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return ResponseEntity.ok(transactionService.getTopExpenses(limit, user));
    }

    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlySummary(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "year", required = false) Integer year) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            int targetYear = (year != null) ? year : Year.now().getValue();

            List<Transaction> transactions = transactionRepository.findAllByUserAndYear(user.getId(), targetYear);

            Map<Integer, Map<String, BigDecimal>> monthlySummary = new HashMap<>();

            for (Transaction tx : transactions) {
                int month = tx.getDate().getMonthValue();
                String type = tx.getType();

                monthlySummary.putIfAbsent(month, new HashMap<>());
                Map<String, BigDecimal> summary = monthlySummary.get(month);
                summary.putIfAbsent("income", BigDecimal.ZERO);
                summary.putIfAbsent("expense", BigDecimal.ZERO);

                if ("INCOME".equalsIgnoreCase(type)) {
                    summary.put("income", summary.get("income").add(tx.getAmount()));
                } else if ("EXPENSE".equalsIgnoreCase(type)) {
                    summary.put("expense", summary.get("expense").add(tx.getAmount()));
                }
            }

            return ResponseEntity.ok(monthlySummary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<List<TransactionResponse>> getRecentTransactions(
            @RequestParam(defaultValue = "5") int limit,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<TransactionResponse> recentTransactions = transactionService.getRecentTransactions(limit, user);
        return ResponseEntity.ok(recentTransactions);
    }

    @GetMapping("/pie-chart/{type}")
    public ResponseEntity<List<CategoryStatisticResponse>> getTransactionPieChartData(
            @PathVariable String type,
            @RequestParam(value = "budgetId", required = false) Long budgetId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<CategoryStatisticResponse> pieChartData = budgetId != null
                ? transactionService.getTransactionStatsByTypeAndBudget(user, type, budgetId)
                : transactionService.getTransactionStatsByType(user, type);
        return ResponseEntity.ok(pieChartData);
    }

    @GetMapping("/pie-chart")
    public ResponseEntity<Map<String, List<CategoryStatisticResponse>>> getAllPieChartData(
            @RequestParam(value = "budgetId", required = false) Long budgetId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Map<String, List<CategoryStatisticResponse>> result = new HashMap<>();
        if (budgetId != null) {
            result.put("income", transactionService.getTransactionStatsByTypeAndBudget(user, "INCOME", budgetId));
            result.put("expense", transactionService.getTransactionStatsByTypeAndBudget(user, "EXPENSE", budgetId));
        } else {
            result.put("income", transactionService.getTransactionStatsByType(user, "INCOME"));
            result.put("expense", transactionService.getTransactionStatsByType(user, "EXPENSE"));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/trend")
    public ResponseEntity<List<SpendingTrendResponse>> getSpendingTrend(
            @RequestParam(value = "period", defaultValue = "MONTH") String period,
            @RequestParam(value = "limit", defaultValue = "6") int limit,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<SpendingTrendResponse> trendData = transactionService.getSpendingTrend(user, period, limit);
        return ResponseEntity.ok(trendData);
    }

    @GetMapping("/budget-comparison")
    public ResponseEntity<List<BudgetComparisonResponse>> getBudgetComparison(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<BudgetComparisonResponse> comparisonData = transactionService.getBudgetComparison(user);
        return ResponseEntity.ok(comparisonData);
    }

}
