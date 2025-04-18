package com.example.zencash.repository;

import com.example.zencash.entity.Budget;
import com.example.zencash.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser(User user);
    List<Budget> findByUserId(UUID userId);
    boolean existsByNameIgnoreCaseAndUser(String name, User user);
    boolean existsByNameIgnoreCaseAndUserAndIdNot(String name, User user, Long id);

    Optional<Budget> findFirstByUser(User user);
}
