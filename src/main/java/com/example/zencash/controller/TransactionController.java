package com.example.zencash.controller;

import com.example.zencash.dto.TransactionRequest;
import com.example.zencash.dto.TransactionResponse;
import com.example.zencash.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

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

    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<List<TransactionResponse>> getByBudget(@PathVariable Long budgetId) {
        return ResponseEntity.ok(transactionService.getByBudget(budgetId));
    }
}
