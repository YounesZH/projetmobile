package com.smartfinance.controller;

import com.smartfinance.model.Budget;
import com.smartfinance.service.BudgetService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "http://localhost:3000")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<Budget>> getUserBudgets(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(budgetService.getUserBudgets(userEmail));
    }

    @PostMapping
    public ResponseEntity<Budget> createBudget(
            Authentication authentication,
            @Valid @RequestBody CreateBudgetRequest request) {
        String userEmail = authentication.getName();
        Budget budget = new Budget();
        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setDescription(request.getDescription());
        
        return ResponseEntity.ok(budgetService.createBudget(userEmail, budget));
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<Budget> updateBudget(
            Authentication authentication,
            @PathVariable Long budgetId,
            @Valid @RequestBody UpdateBudgetRequest request) {
        String userEmail = authentication.getName();
        Budget budget = new Budget();
        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setDescription(request.getDescription());
        
        return ResponseEntity.ok(budgetService.updateBudget(userEmail, budgetId, budget));
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(
            Authentication authentication,
            @PathVariable Long budgetId) {
        String userEmail = authentication.getName();
        budgetService.deleteBudget(userEmail, budgetId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<Budget> getBudget(
            Authentication authentication,
            @PathVariable Long budgetId) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(budgetService.getBudget(userEmail, budgetId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Budget>> getActiveBudgetsForDate(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(budgetService.getActiveBudgetsForDate(userEmail, date));
    }

    @GetMapping("/{budgetId}/progress")
    public ResponseEntity<BigDecimal> getBudgetProgress(
            Authentication authentication,
            @PathVariable Long budgetId) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(budgetService.getBudgetProgress(userEmail, budgetId));
    }

    @Data
    public static class CreateBudgetRequest {
        @NotBlank
        private String category;

        @NotNull
        private BigDecimal amount;

        @NotNull
        private LocalDateTime startDate;

        @NotNull
        private LocalDateTime endDate;

        private String description;
    }

    @Data
    public static class UpdateBudgetRequest {
        @NotBlank
        private String category;

        @NotNull
        private BigDecimal amount;

        @NotNull
        private LocalDateTime startDate;

        @NotNull
        private LocalDateTime endDate;

        private String description;
    }
} 