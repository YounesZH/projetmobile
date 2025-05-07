package com.smartfinance.repository;

import com.smartfinance.model.SavingsGoal;
import com.smartfinance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUser(User user);
    List<SavingsGoal> findByUserAndIsActiveTrue(User user);
    Optional<SavingsGoal> findByIdAndUser(Long id, User user);
    
    @Query("SELECT sg FROM SavingsGoal sg WHERE sg.user = ?1 AND sg.targetDate >= ?2 ORDER BY sg.targetDate ASC")
    List<SavingsGoal> findActiveGoalsWithUpcomingTargetDate(User user, LocalDateTime currentDate);
    
    @Query("SELECT sg FROM SavingsGoal sg WHERE sg.user = ?1 AND sg.currentAmount >= sg.targetAmount AND sg.isActive = true")
    List<SavingsGoal> findCompletedGoals(User user);
    
    @Query("SELECT sg FROM SavingsGoal sg WHERE sg.user = ?1 AND sg.targetDate <= ?2 AND sg.isActive = true")
    List<SavingsGoal> findOverdueGoals(User user, LocalDateTime currentDate);
} 