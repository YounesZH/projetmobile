package com.smartfinance.repository;

import com.smartfinance.model.Budget;
import com.smartfinance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser(User user);
    List<Budget> findByUserAndIsActiveTrue(User user);
    Optional<Budget> findByIdAndUser(Long id, User user);
    
    @Query("SELECT b FROM Budget b WHERE b.user = ?1 AND b.startDate <= ?2 AND b.endDate >= ?2")
    List<Budget> findActiveBudgetsForDate(User user, LocalDateTime date);
    
    @Query("SELECT b FROM Budget b WHERE b.user = ?1 AND b.category = ?2 AND b.startDate <= ?3 AND b.endDate >= ?3")
    Optional<Budget> findActiveBudgetByCategory(User user, String category, LocalDateTime date);
    
    boolean existsByUserAndCategoryAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            User user, String category, LocalDateTime startDate, LocalDateTime endDate);
} 