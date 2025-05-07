package com.smartfinance.controller;

import com.smartfinance.model.SavingsGoal;
import com.smartfinance.service.SavingsGoalService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/savings-goals")
@CrossOrigin(origins = "http://localhost:3000")
public class SavingsGoalController {

    @Autowired
    private SavingsGoalService savingsGoalService;

    @GetMapping
    public ResponseEntity<List<SavingsGoal>> getUserSavingsGoals(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(savingsGoalService.getUserSavingsGoals(userEmail));
    }

    @PostMapping
    public ResponseEntity<SavingsGoal> createSavingsGoal(
            Authentication authentication,
            @Valid @RequestBody CreateSavingsGoalRequest request) {
        String userEmail = authentication.getName();
        SavingsGoal savingsGoal = new SavingsGoal();
        savingsGoal.setName(request.getName());
        savingsGoal.setDescription(request.getDescription());
        savingsGoal.setTargetAmount(request.getTargetAmount());
        savingsGoal.setTargetDate(request.getTargetDate());
        
        return ResponseEntity.ok(savingsGoalService.createSavingsGoal(userEmail, savingsGoal));
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<SavingsGoal> updateSavingsGoal(
            Authentication authentication,
            @PathVariable Long goalId,
            @Valid @RequestBody UpdateSavingsGoalRequest request) {
        String userEmail = authentication.getName();
        SavingsGoal savingsGoal = new SavingsGoal();
        savingsGoal.setName(request.getName());
        savingsGoal.setDescription(request.getDescription());
        savingsGoal.setTargetAmount(request.getTargetAmount());
        savingsGoal.setTargetDate(request.getTargetDate());
        
        return ResponseEntity.ok(savingsGoalService.updateSavingsGoal(userEmail, goalId, savingsGoal));
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteSavingsGoal(
            Authentication authentication,
            @PathVariable Long goalId) {
        String userEmail = authentication.getName();
        savingsGoalService.deleteSavingsGoal(userEmail, goalId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<SavingsGoal> getSavingsGoal(
            Authentication authentication,
            @PathVariable Long goalId) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(savingsGoalService.getSavingsGoal(userEmail, goalId));
    }

    @PostMapping("/{goalId}/contribute")
    public ResponseEntity<SavingsGoal> addContribution(
            Authentication authentication,
            @PathVariable Long goalId,
            @Valid @RequestBody ContributionRequest request) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(savingsGoalService.addContribution(userEmail, goalId, request.getAmount()));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<SavingsGoal>> getUpcomingGoals(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(savingsGoalService.getUpcomingGoals(userEmail));
    }

    @GetMapping("/completed")
    public ResponseEntity<List<SavingsGoal>> getCompletedGoals(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(savingsGoalService.getCompletedGoals(userEmail));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<SavingsGoal>> getOverdueGoals(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(savingsGoalService.getOverdueGoals(userEmail));
    }

    @GetMapping("/{goalId}/progress")
    public ResponseEntity<BigDecimal> getProgressPercentage(
            Authentication authentication,
            @PathVariable Long goalId) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(savingsGoalService.getProgressPercentage(userEmail, goalId));
    }

    @Data
    public static class CreateSavingsGoalRequest {
        @NotBlank
        private String name;

        private String description;

        @NotNull
        private BigDecimal targetAmount;

        @NotNull
        private LocalDateTime targetDate;
    }

    @Data
    public static class UpdateSavingsGoalRequest {
        @NotBlank
        private String name;

        private String description;

        @NotNull
        private BigDecimal targetAmount;

        @NotNull
        private LocalDateTime targetDate;
    }

    @Data
    public static class ContributionRequest {
        @NotNull
        private BigDecimal amount;
    }
} 