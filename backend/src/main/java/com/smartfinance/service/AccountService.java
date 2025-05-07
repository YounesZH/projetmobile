package com.smartfinance.service;

import com.smartfinance.model.Account;
import com.smartfinance.model.User;
import com.smartfinance.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserService userService;

    public List<Account> getUserAccounts(String userEmail) {
        User user = userService.findByEmail(userEmail);
        return accountRepository.findByUserAndIsActiveTrue(user);
    }

    @Transactional
    public Account createAccount(String userEmail, Account account) {
        User user = userService.findByEmail(userEmail);
        
        if (accountRepository.existsByUserAndName(user, account.getName())) {
            throw new RuntimeException("Account with this name already exists");
        }

        account.setUser(user);
        account.setCurrentBalance(account.getInitialBalance());
        return accountRepository.save(account);
    }

    @Transactional
    public Account updateAccount(String userEmail, Long accountId, Account updatedAccount) {
        User user = userService.findByEmail(userEmail);
        Account existingAccount = accountRepository.findByIdAndUser(accountId, user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        existingAccount.setName(updatedAccount.getName());
        existingAccount.setDescription(updatedAccount.getDescription());
        existingAccount.setType(updatedAccount.getType());
        existingAccount.setCurrency(updatedAccount.getCurrency());
        
        return accountRepository.save(existingAccount);
    }

    @Transactional
    public void deleteAccount(String userEmail, Long accountId) {
        User user = userService.findByEmail(userEmail);
        Account account = accountRepository.findByIdAndUser(accountId, user)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        account.setActive(false);
        accountRepository.save(account);
    }

    @Transactional
    public Account updateBalance(String userEmail, Long accountId, BigDecimal newBalance) {
        User user = userService.findByEmail(userEmail);
        Account account = accountRepository.findByIdAndUser(accountId, user)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        account.setCurrentBalance(newBalance);
        return accountRepository.save(account);
    }

    public Account getAccount(String userEmail, Long accountId) {
        User user = userService.findByEmail(userEmail);
        return accountRepository.findByIdAndUser(accountId, user)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
} 