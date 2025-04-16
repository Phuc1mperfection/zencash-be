package com.example.zencash.controller;

import com.example.zencash.dto.StatisticResultResponse;
import com.example.zencash.dto.TopTransactionResponse;
import com.example.zencash.entity.Transaction;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.service.StatisticService;
import com.example.zencash.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    @GetMapping("/top-transactions")
    public ResponseEntity<TopTransactionResponse> getTopTransactions(@RequestParam Long budgetId) {
        return ResponseEntity.ok(statisticService.getTopTransactions(budgetId));
    }

    // Thống kê theo ngày
    @GetMapping("/by-day")
    public ResponseEntity<StatisticResultResponse> getStatisticByDay(@RequestParam("budgetId") Long budgetId, @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<Transaction> transactions = statisticService.getTransactionsByDate(budgetId, date);
        StatisticResultResponse response = statisticService.buildStatisticResult(transactions);
        return ResponseEntity.ok(response);
    }

    // Thống kê theo tuần
    @GetMapping("/by-week")
    public ResponseEntity<StatisticResultResponse> getStatisticByWeek(@RequestParam("budgetId") Long budgetId, @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate) {
        List<Transaction> transactions = statisticService.getTransactionsByWeek(budgetId, startDate);
        StatisticResultResponse response = statisticService.buildStatisticResult(transactions);
        return ResponseEntity.ok(response);
    }

    // Thống kê theo tháng
    @GetMapping("/by-month")
    public ResponseEntity<StatisticResultResponse> getStatisticByMonth(@RequestParam("budgetId") Long budgetId, @RequestParam("month") int month, @RequestParam("year") int year) {
        List<Transaction> transactions = statisticService.getTransactionsByMonth(budgetId, month, year);
        StatisticResultResponse response = statisticService.buildStatisticResult(transactions);
        return ResponseEntity.ok(response);
    }

    // Thống kê theo năm
    @GetMapping("/by-year")
    public ResponseEntity<StatisticResultResponse> getStatisticByYear(@RequestParam("budgetId") Long budgetId, @RequestParam("year") int year) {
        List<Transaction> transactions = statisticService.getTransactionsByYear(budgetId, year);
        StatisticResultResponse response = statisticService.buildStatisticResult(transactions);
        return ResponseEntity.ok(response);
    }

    // Thống kê tổng thu/chi theo ví của người dùng
    @GetMapping("/total")
    public ResponseEntity<StatisticResultResponse> getTotalStatistics(@RequestParam("userId") UUID userId) {
        List<Transaction> transactions = statisticService.getAllTransactionsByUser(userId);
        StatisticResultResponse response = statisticService.buildStatisticResult(transactions);
        return ResponseEntity.ok(response);
    }
}

