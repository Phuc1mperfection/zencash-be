package com.example.zencash.repository;

import com.example.zencash.entity.Budget;
import com.example.zencash.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser(User user);
    List<Budget> findByUserId(UUID userId);

}
