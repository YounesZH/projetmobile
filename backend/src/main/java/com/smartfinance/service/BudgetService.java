package com.smartfinance.service;

import com.smartfinance.model.Budget;
import com.smartfinance.model.Transaction;
import com.smartfinance.model.User;
import com.smartfinance.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    public List<Budget> getUserBudgets(String userEmail) {
        User user = userService.findByEmail(userEmail);
        return budgetRepository.findByUserAndIsActiveTrue(user);
    }

    @Transactional
    public Budget createBudget(String userEmail, Budget budget) {
        User user = userService.findByEmail(userEmail);
        
        // Check for overlapping budgets in the same category
        if (budgetRepository.existsByUserAndCategoryAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                user, budget.getCategory(), budget.getStartDate(), budget.getEndDate())) {
            throw new RuntimeException("A budget already exists for this category in the specified date range");
        }

        budget.setUser(user);
        budget.setActive(true);
        budget.setSpentAmount(BigDecimal.ZERO);
        return budgetRepository.save(budget);
    }

    @Transactional
    public Budget updateBudget(String userEmail, Long budgetId, Budget updatedBudget) {
        User user = userService.findByEmail(userEmail);
        Budget existingBudget = budgetRepository.findByIdAndUser(budgetId, user)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        // Check for overlapping budgets if category or date range changed
        if (!existingBudget.getCategory().equals(updatedBudget.getCategory()) ||
            !existingBudget.getStartDate().equals(updatedBudget.getStartDate()) ||
            !existingBudget.getEndDate().equals(updatedBudget.getEndDate())) {
            
            if (budgetRepository.existsByUserAndCategoryAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                    user, updatedBudget.getCategory(), updatedBudget.getStartDate(), updatedBudget.getEndDate())) {
                throw new RuntimeException("A budget already exists for this category in the specified date range");
            }
        }

        existingBudget.setCategory(updatedBudget.getCategory());
        existingBudget.setAmount(updatedBudget.getAmount());
        existingBudget.setStartDate(updatedBudget.getStartDate());
        existingBudget.setEndDate(updatedBudget.getEndDate());
        existingBudget.setDescription(updatedBudget.getDescription());
        
        return budgetRepository.save(existingBudget);
    }

    @Transactional
    public void deleteBudget(String userEmail, Long budgetId) {
        User user = userService.findByEmail(userEmail);
        Budget budget = budgetRepository.findByIdAndUser(budgetId, user)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        
        budget.setActive(false);
        budgetRepository.save(budget);
    }

    public Budget getBudget(String userEmail, Long budgetId) {
        User user = userService.findByEmail(userEmail);
        return budgetRepository.findByIdAndUser(budgetId, user)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
    }

    @Transactional
    public void updateBudgetSpending(String userEmail, Transaction transaction) {
        User user = userService.findByEmail(userEmail);
        
        // Only update budget for expenses
        if (transaction.getType() != Transaction.TransactionType.EXPENSE) {
            return;
        }

        Optional<Budget> budgetOpt = budgetRepository.findActiveBudgetByCategory(
                user, transaction.getCategory(), transaction.getTransactionDate());

        if (budgetOpt.isPresent()) {
            Budget budget = budgetOpt.get();
            budget.setSpentAmount(budget.getSpentAmount().add(transaction.getAmount()));
            budgetRepository.save(budget);
        }
    }

    public List<Budget> getActiveBudgetsForDate(String userEmail, LocalDateTime date) {
        User user = userService.findByEmail(userEmail);
        return budgetRepository.findActiveBudgetsForDate(user, date);
    }

    public BigDecimal getBudgetProgress(String userEmail, Long budgetId) {
        Budget budget = getBudget(userEmail, budgetId);
        if (budget.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return budget.getSpentAmount().divide(budget.getAmount(), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }
} 