package com.smartfinance.service;

import com.smartfinance.model.SavingsGoal;
import com.smartfinance.model.User;
import com.smartfinance.repository.SavingsGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SavingsGoalService {

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private UserService userService;

    public List<SavingsGoal> getUserSavingsGoals(String userEmail) {
        User user = userService.findByEmail(userEmail);
        return savingsGoalRepository.findByUserAndIsActiveTrue(user);
    }

    @Transactional
    public SavingsGoal createSavingsGoal(String userEmail, SavingsGoal savingsGoal) {
        User user = userService.findByEmail(userEmail);
        
        if (savingsGoal.getTargetDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Target date cannot be in the past");
        }
        
        if (savingsGoal.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Target amount must be greater than zero");
        }

        savingsGoal.setUser(user);
        savingsGoal.setActive(true);
        savingsGoal.setCurrentAmount(BigDecimal.ZERO);
        savingsGoal.setCreatedAt(LocalDateTime.now());
        return savingsGoalRepository.save(savingsGoal);
    }

    @Transactional
    public SavingsGoal updateSavingsGoal(String userEmail, Long goalId, SavingsGoal updatedGoal) {
        User user = userService.findByEmail(userEmail);
        SavingsGoal existingGoal = savingsGoalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new RuntimeException("Savings goal not found"));

        if (updatedGoal.getTargetDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Target date cannot be in the past");
        }
        
        if (updatedGoal.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Target amount must be greater than zero");
        }

        existingGoal.setName(updatedGoal.getName());
        existingGoal.setDescription(updatedGoal.getDescription());
        existingGoal.setTargetAmount(updatedGoal.getTargetAmount());
        existingGoal.setTargetDate(updatedGoal.getTargetDate());
        
        return savingsGoalRepository.save(existingGoal);
    }

    @Transactional
    public void deleteSavingsGoal(String userEmail, Long goalId) {
        User user = userService.findByEmail(userEmail);
        SavingsGoal goal = savingsGoalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new RuntimeException("Savings goal not found"));
        
        goal.setActive(false);
        savingsGoalRepository.save(goal);
    }

    public SavingsGoal getSavingsGoal(String userEmail, Long goalId) {
        User user = userService.findByEmail(userEmail);
        return savingsGoalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new RuntimeException("Savings goal not found"));
    }

    @Transactional
    public SavingsGoal addContribution(String userEmail, Long goalId, BigDecimal amount) {
        User user = userService.findByEmail(userEmail);
        SavingsGoal goal = savingsGoalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new RuntimeException("Savings goal not found"));

        if (!goal.isActive()) {
            throw new RuntimeException("Cannot contribute to an inactive goal");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Contribution amount must be greater than zero");
        }

        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
        goal.setLastContributionDate(LocalDateTime.now());
        
        // Check if goal is completed
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setCompleted(true);
            goal.setCompletedDate(LocalDateTime.now());
        }
        
        return savingsGoalRepository.save(goal);
    }

    public List<SavingsGoal> getUpcomingGoals(String userEmail) {
        User user = userService.findByEmail(userEmail);
        return savingsGoalRepository.findActiveGoalsWithUpcomingTargetDate(user, LocalDateTime.now());
    }

    public List<SavingsGoal> getCompletedGoals(String userEmail) {
        User user = userService.findByEmail(userEmail);
        return savingsGoalRepository.findCompletedGoals(user);
    }

    public List<SavingsGoal> getOverdueGoals(String userEmail) {
        User user = userService.findByEmail(userEmail);
        return savingsGoalRepository.findOverdueGoals(user, LocalDateTime.now());
    }

    public BigDecimal getProgressPercentage(String userEmail, Long goalId) {
        SavingsGoal goal = getSavingsGoal(userEmail, goalId);
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return goal.getCurrentAmount()
                .divide(goal.getTargetAmount(), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }
} 