package com.smartfinance.controller;

import com.smartfinance.model.Transaction;
import com.smartfinance.service.TransactionService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<Transaction>> getUserTransactions(
            Authentication authentication,
            Pageable pageable) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(transactionService.getUserTransactions(userEmail, pageable));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<Page<Transaction>> getAccountTransactions(
            Authentication authentication,
            @PathVariable Long accountId,
            Pageable pageable) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(transactionService.getAccountTransactions(userEmail, accountId, pageable));
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            Authentication authentication,
            @Valid @RequestBody CreateTransactionRequest request) {
        String userEmail = authentication.getName();
        Transaction transaction = new Transaction();
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setCurrency(request.getCurrency());
        transaction.setRecurring(request.getRecurring());
        transaction.setRecurringFrequency(request.getRecurringFrequency());
        transaction.setTags(request.getTags());
        
        // Set account
        Transaction.Account account = new Transaction.Account();
        account.setId(request.getAccountId());
        transaction.setAccount(account);
        
        return ResponseEntity.ok(transactionService.createTransaction(userEmail, transaction));
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Transaction> updateTransaction(
            Authentication authentication,
            @PathVariable Long transactionId,
            @Valid @RequestBody UpdateTransactionRequest request) {
        String userEmail = authentication.getName();
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setTags(request.getTags());
        
        return ResponseEntity.ok(transactionService.updateTransaction(userEmail, transactionId, transaction));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            Authentication authentication,
            @PathVariable Long transactionId) {
        String userEmail = authentication.getName();
        transactionService.deleteTransaction(userEmail, transactionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<Transaction>> getTransactionsByDateRange(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(transactionService.getTransactionsByDateRange(userEmail, startDate, endDate, pageable));
    }

    @GetMapping("/category")
    public ResponseEntity<Page<Transaction>> getTransactionsByCategory(
            Authentication authentication,
            @RequestParam String category,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(transactionService.getTransactionsByCategory(userEmail, category, startDate, endDate, pageable));
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalAmountByType(
            Authentication authentication,
            @RequestParam Transaction.TransactionType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(transactionService.getTotalAmountByType(userEmail, type, startDate, endDate));
    }

    @Data
    public static class CreateTransactionRequest {
        @NotNull
        private Transaction.TransactionType type;

        @NotNull
        private BigDecimal amount;

        @NotBlank
        private String category;

        private String description;

        @NotNull
        private LocalDateTime transactionDate;

        @NotBlank
        private String currency;

        private Boolean recurring;

        private Transaction.RecurringFrequency recurringFrequency;

        private String tags;

        @NotNull
        private Long accountId;
    }

    @Data
    public static class UpdateTransactionRequest {
        @NotNull
        private BigDecimal amount;

        @NotBlank
        private String category;

        private String description;

        @NotNull
        private LocalDateTime transactionDate;

        private String tags;
    }
} 