package com.example.zencash.repository;

import com.example.zencash.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByBudgetId(Long budgetId);

    List<Goal> findByBudgetIdAndMonth(Long budgetId, LocalDate localDate);
}

