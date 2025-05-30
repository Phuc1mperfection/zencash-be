package com.example.zencash.repository;

import com.example.zencash.entity.CategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryGroupRepository extends JpaRepository<CategoryGroup, Long> {
    boolean existsByNameIgnoreCaseAndBudgetId(String name, Long budgetId);
    List<CategoryGroup> findByBudgetId(Long budgetId);

}
