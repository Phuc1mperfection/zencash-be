package com.example.zencash.service;

import com.example.zencash.dto.TransactionRequest;
import com.example.zencash.dto.TransactionResponse;
import com.example.zencash.entity.Budget;
import com.example.zencash.entity.Category;
import com.example.zencash.entity.Transaction;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.BudgetRepository;
import com.example.zencash.repository.CategoryRepository;
import com.example.zencash.repository.TransactionRepository;
import com.example.zencash.utils.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BudgetRepository budgetRepository;
    @Autowired private BudgetService budgetService;

    public TransactionResponse createTransaction(TransactionRequest request) {
        Budget budget = budgetService.getBudgetById(request.getBudgetId());
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setNote(request.getNote());
        transaction.setType(request.getType());
        transaction.setDate(request.getDate());
        transaction.setCreateAt(LocalDateTime.now());
        transaction.setBudget(budget);
        transaction.setCategory(category);

        transaction = transactionRepository.save(transaction);
        budgetService.updateRemainingAmount(budget);

        return mapToResponse(transaction);
    }

    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        existing.setAmount(request.getAmount());
        existing.setNote(request.getNote());
        existing.setDate(request.getDate());
        existing.setType(request.getType());
        existing.setUpdateAt(LocalDateTime.now());

        if (!existing.getCategory().getId().equals(request.getCategoryId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            existing.setCategory(newCategory);
        }

        Transaction updated = transactionRepository.save(existing);
        budgetService.updateRemainingAmount(updated.getBudget());

        return mapToResponse(updated);
    }

    public boolean deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        Budget budget = transaction.getBudget();
        transactionRepository.delete(transaction);
        budgetService.updateRemainingAmount(budget);
        return true;
    }

    public List<TransactionResponse> getByBudget(Long budgetId) {
        return transactionRepository.findByBudgetId(budgetId)
                .stream().map(this::mapToResponse)
                .toList();
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



