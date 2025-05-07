package com.example.zencash.repository;

import com.example.zencash.dto.CategoryGroupStatisticResponse;
import com.example.zencash.dto.CategoryStatisticResponse;
import com.example.zencash.entity.Transaction;
import com.example.zencash.entity.User;
import org.springframework.data.domain.Pageable;
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

    // boolean existsByNameIgnoreCase(String name);

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

    @Query("SELECT t FROM Transaction t WHERE t.budget.user = :user AND t.type = :type ORDER BY t.amount DESC")
    List<Transaction> findTopByUserAndTypeOrderByAmountDesc(@Param("user") User user,
            @Param("type") String type,
            Pageable pageable);

    // Hoặc nếu không dùng @Query thì có thể dùng cách đặt tên sau (nếu JPA hỗ trợ):
    List<Transaction> findByBudget_UserAndTypeOrderByAmountDesc(User user, String type, Pageable pageable);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.budget.user = :user AND t.type = 'EXPENSE'")
    BigDecimal sumTotalExpenseByUser(@Param("user") User user);

    List<Transaction> findAllByBudgetId(Long budgetId);

    @Query("SELECT t FROM Transaction t WHERE t.budget.user.id = :userId AND FUNCTION('YEAR', t.date) = :year")
    List<Transaction> findAllByUserAndYear(@Param("userId") UUID userId, @Param("year") int year);

    List<Transaction> findByBudget_UserOrderByDateDescCreateAtDesc(User user, Pageable pageable);

    List<Transaction> findByBudget_UserAndType(User user, String type);

    @Query(value = "SELECT NEW com.example.zencash.dto.CategoryStatisticResponse(" +
            "t.category.id, t.category.name, SUM(t.amount), COUNT(t)) " +
            "FROM Transaction t " +
            "WHERE t.budget.user = :user AND t.type = :type " +
            "GROUP BY t.category.id, t.category.name")
    List<CategoryStatisticResponse> getCategoryStatisticsByType(@Param("user") User user, @Param("type") String type);

    @Query("SELECT FUNCTION('YEAR', t.date) as year, FUNCTION('MONTH', t.date) as month, " +
            "t.type as type, SUM(t.amount) as amount " +
            "FROM Transaction t " +
            "WHERE t.budget.user = :user " +
            "AND t.date BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('YEAR', t.date), FUNCTION('MONTH', t.date), t.type " +
            "ORDER BY year, month")
    List<Object[]> getMonthlySpendingTrend(@Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT FUNCTION('YEAR', t.date) as year, FUNCTION('WEEK', t.date) as week, " +
            "t.type as type, SUM(t.amount) as amount " +
            "FROM Transaction t " +
            "WHERE t.budget.user = :user " +
            "AND t.date BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('YEAR', t.date), FUNCTION('WEEK', t.date), t.type " +
            "ORDER BY year, week")
    List<Object[]> getWeeklySpendingTrend(@Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT FUNCTION('YEAR', t.date) as year, " +
            "CASE " +
            "  WHEN FUNCTION('MONTH', t.date) BETWEEN 1 AND 3 THEN 1 " +
            "  WHEN FUNCTION('MONTH', t.date) BETWEEN 4 AND 6 THEN 2 " +
            "  WHEN FUNCTION('MONTH', t.date) BETWEEN 7 AND 9 THEN 3 " +
            "  ELSE 4 " +
            "END as quarter, " +
            "t.type as type, SUM(t.amount) as amount " +
            "FROM Transaction t " +
            "WHERE t.budget.user = :user " +
            "AND t.date BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('YEAR', t.date), " +
            "CASE " +
            "  WHEN FUNCTION('MONTH', t.date) BETWEEN 1 AND 3 THEN 1 " +
            "  WHEN FUNCTION('MONTH', t.date) BETWEEN 4 AND 6 THEN 2 " +
            "  WHEN FUNCTION('MONTH', t.date) BETWEEN 7 AND 9 THEN 3 " +
            "  ELSE 4 " +
            "END, t.type " +
            "ORDER BY year, quarter")
    List<Object[]> getQuarterlySpendingTrend(@Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
