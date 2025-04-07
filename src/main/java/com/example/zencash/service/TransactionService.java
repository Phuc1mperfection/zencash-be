package com.example.zencash.service;

import com.example.zencash.dto.TransactionRequest;
import com.example.zencash.dto.TransactionResponse;
import com.example.zencash.entity.Budget;
import com.example.zencash.entity.Category;
import com.example.zencash.entity.Transaction;
import com.example.zencash.repository.BudgetRepository;
import com.example.zencash.repository.CategoryRepository;
import com.example.zencash.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BudgetRepository budgetRepository;

    public TransactionResponse createTransaction(TransactionRequest request) {

        Budget budget = budgetRepository.findById(request.getBudgetId())
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setNote(request.getNote());
        transaction.setType(request.getType());
        transaction.setDate(request.getDate());
        transaction.setCreateAt(LocalDateTime.now());
        transaction.setBudget(budget);
        transaction.setCategory(category);

        transaction = transactionRepository.save(transaction);
        updateBudgetBalance(budget, request.getAmount(), request.getType(), true); // true = thêm tiền mới

        return mapToResponse(transaction);
    }

    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Trả lại số tiền cũ
        updateBudgetBalance(existing.getBudget(), existing.getAmount(), existing.getType(), false);

        // Áp dụng số tiền mới
        existing.setAmount(request.getAmount());
        existing.setNote(request.getNote());
        existing.setDate(request.getDate());
        existing.setType(request.getType());
        existing.setUpdateAt(LocalDateTime.now());

        if (!existing.getCategory().getId().equals(request.getCategoryId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existing.setCategory(newCategory);
        }

        Transaction updated = transactionRepository.save(existing);
        updateBudgetBalance(updated.getBudget(), updated.getAmount(), updated.getType(), true);

        return mapToResponse(updated);
    }

    public boolean deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        updateBudgetBalance(transaction.getBudget(), transaction.getAmount(), transaction.getType(), false);
        transactionRepository.delete(transaction);
        return true;
    }

    public List<TransactionResponse> getByBudget(Long budgetId) {
        return transactionRepository.findByBudgetId(budgetId)
                .stream().map(this::mapToResponse)
                .toList();
    }

    private void updateBudgetBalance(Budget budget, BigDecimal amount, String type, boolean isAdd) {
        BigDecimal remaining = budget.getRemainingAmount();
        if (type.equalsIgnoreCase("EXPENSE")) {
            remaining = isAdd ? remaining.subtract(amount) : remaining.add(amount);
        } else {
            remaining = isAdd ? remaining.add(amount) : remaining.subtract(amount);
        }
        budget.setRemainingAmount(remaining);
        budget.setUpdatedAt(LocalDateTime.now());
        budgetRepository.save(budget);
    }

    private TransactionResponse mapToResponse(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getBudget().getId(),
                tx.getCategory().getId(),
                tx.getAmount(),
                tx.getType(),
                tx.getNote(),
                tx.getDate()
        );
    }
}


