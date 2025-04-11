package com.example.zencash.repository;

import com.example.zencash.dto.CategoryGroupStatisticResponse;
import com.example.zencash.entity.Transaction;
import com.example.zencash.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByBudgetId(Long budgetId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.budget.id = :budgetId AND t.type = :type")
    BigDecimal sumAmountByBudgetIdAndType(@Param("budgetId") Long budgetId, @Param("type") String type);

    @Query("SELECT t FROM Transaction t WHERE t.budget.user = :user")
    List<Transaction> findAllByUser(@Param("user") User user);

//    boolean existsByNameIgnoreCase(String name);

    @Query("""
    SELECT new com.example.zencash.dto.CategoryGroupStatisticResponse(
        cg.id, cg.name, SUM(t.amount)
    )
    FROM Transaction t
    JOIN t.category c
    JOIN c.categoryGroup cg
    WHERE t.budget.id = :budgetId
    GROUP BY cg.id, cg.name""")
    List<CategoryGroupStatisticResponse> getStatisticsByBudgetId(@Param("budgetId") Long budgetId);
}
