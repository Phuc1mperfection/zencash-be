package com.example.zencash.repository;

import com.example.zencash.entity.Transaction;
import com.example.zencash.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByBudgetId(Long budgetId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.budget.id = :budgetId AND t.type = :type")
    BigDecimal sumAmountByBudgetIdAndType(@Param("budgetId") Long budgetId, @Param("type") String type);

    @Query("SELECT t FROM Transaction t WHERE t.budget.user = :user")
    List<Transaction> findAllByUser(@Param("user") User user);

    @Query("""
    SELECT new com.example.zencash.dto.CategoryGroupStatisticResponse(
        cg.id, cg.name, SUM(t.amount)
    )
    FROM Transaction t
    JOIN t.category c
    JOIN c.categoryGroup cg
    WHERE t.budget.id = :budgetId
    GROUP BY cg.id, cg.name""")

    List<Transaction> findTop5ByBudgetIdOrderByDateDesc(Long budgetId);

    List<Transaction> findTop5ByBudgetIdOrderByDateAsc(Long budgetId);

    // Lấy giao dịch theo ngày
    List<Transaction> findByBudgetIdAndDateEquals(Long budgetId, LocalDate date);

    // Lấy giao dịch trong tuần
    @Query("SELECT t FROM Transaction t WHERE t.budget.id = :budgetId AND WEEK(t.date) = WEEK(:startDate) AND YEAR(t.date) = YEAR(:startDate)")
    List<Transaction> findByBudgetIdAndWeek(Long budgetId, LocalDate startDate);

    // Lấy giao dịch theo tháng
    List<Transaction> findByBudgetIdAndDateBetween(Long budgetId, LocalDate startDate, LocalDate endDate);

    // Lấy tất cả giao dịch của người dùng (dựa trên ví)
    List<Transaction> findByBudget_UserId(UUID userId);

}



