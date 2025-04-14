package com.example.zencash.repository;

import com.example.zencash.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByCategoryGroupId(Long categoryGroupId);
    List<Category> findByBudgetIdOrDefaultCatTrue(Long budgetId);
    boolean existsByNameIgnoreCaseAndBudgetId(String name, Long id);
}
