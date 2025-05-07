package com.smartfinance.controller;

import com.smartfinance.model.Account;
import com.smartfinance.service.AccountService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:3000")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<List<Account>> getUserAccounts(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(accountService.getUserAccounts(userEmail));
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(
            Authentication authentication,
            @Valid @RequestBody CreateAccountRequest request) {
        String userEmail = authentication.getName();
        Account account = new Account();
        account.setName(request.getName());
        account.setType(request.getType());
        account.setInitialBalance(request.getInitialBalance());
        account.setCurrency(request.getCurrency());
        account.setDescription(request.getDescription());
        
        return ResponseEntity.ok(accountService.createAccount(userEmail, account));
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<Account> updateAccount(
            Authentication authentication,
            @PathVariable Long accountId,
            @Valid @RequestBody UpdateAccountRequest request) {
        String userEmail = authentication.getName();
        Account account = new Account();
        account.setName(request.getName());
        account.setType(request.getType());
        account.setCurrency(request.getCurrency());
        account.setDescription(request.getDescription());
        
        return ResponseEntity.ok(accountService.updateAccount(userEmail, accountId, account));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(
            Authentication authentication,
            @PathVariable Long accountId) {
        String userEmail = authentication.getName();
        accountService.deleteAccount(userEmail, accountId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/balance")
    public ResponseEntity<Account> updateBalance(
            Authentication authentication,
            @PathVariable Long accountId,
            @Valid @RequestBody UpdateBalanceRequest request) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(accountService.updateBalance(userEmail, accountId, request.getNewBalance()));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(
            Authentication authentication,
            @PathVariable Long accountId) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(accountService.getAccount(userEmail, accountId));
    }

    @Data
    public static class CreateAccountRequest {
        @NotBlank
        private String name;

        @NotNull
        private Account.AccountType type;

        @NotNull
        private BigDecimal initialBalance;

        @NotBlank
        private String currency;

        private String description;
    }

    @Data
    public static class UpdateAccountRequest {
        @NotBlank
        private String name;

        @NotNull
        private Account.AccountType type;

        @NotBlank
        private String currency;

        private String description;
    }

    @Data
    public static class UpdateBalanceRequest {
        @NotNull
        private BigDecimal newBalance;
    }
} 