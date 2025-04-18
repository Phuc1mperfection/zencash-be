package com.example.zencash.repository;

import com.example.zencash.entity.Category;
import com.example.zencash.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByCategoryGroupId(Long categoryGroupId);
    List<Category> findByBudgetIdOrDefaultCatTrue(Long budgetId);
    boolean existsByNameIgnoreCaseAndBudgetId(String name, Long id);

    Optional<Category> findFirstByBudgetId(Long id);

    Optional<Category> findFirstByUser(User user);
}
