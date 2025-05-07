package com.smartfinance.service;

import com.smartfinance.model.Account;
import com.smartfinance.model.Transaction;
import com.smartfinance.model.User;
import com.smartfinance.repository.AccountRepository;
import com.smartfinance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    public Page<Transaction> getUserTransactions(String userEmail, Pageable pageable) {
        User user = userService.findByEmail(userEmail);
        return transactionRepository.findByUser(user, pageable);
    }

    public Page<Transaction> getAccountTransactions(String userEmail, Long accountId, Pageable pageable) {
        User user = userService.findByEmail(userEmail);
        Account account = accountService.getAccount(userEmail, accountId);
        return transactionRepository.findByUserAndAccount(user, account, pageable);
    }

    @Transactional
    public Transaction createTransaction(String userEmail, Transaction transaction) {
        User user = userService.findByEmail(userEmail);
        Account account = accountService.getAccount(userEmail, transaction.getAccount().getId());
        
        transaction.setUser(user);
        transaction.setAccount(account);
        
        // Update account balance based on transaction type
        BigDecimal currentBalance = account.getCurrentBalance();
        switch (transaction.getType()) {
            case INCOME:
                account.setCurrentBalance(currentBalance.add(transaction.getAmount()));
                break;
            case EXPENSE:
                account.setCurrentBalance(currentBalance.subtract(transaction.getAmount()));
                break;
            case TRANSFER:
                // Handle transfer logic here
                break;
        }
        
        accountService.updateBalance(userEmail, account.getId(), account.getCurrentBalance());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction updateTransaction(String userEmail, Long transactionId, Transaction updatedTransaction) {
        User user = userService.findByEmail(userEmail);
        Transaction existingTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!existingTransaction.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized access to transaction");
        }

        // Update transaction details
        existingTransaction.setAmount(updatedTransaction.getAmount());
        existingTransaction.setCategory(updatedTransaction.getCategory());
        existingTransaction.setDescription(updatedTransaction.getDescription());
        existingTransaction.setTransactionDate(updatedTransaction.getTransactionDate());
        existingTransaction.setTags(updatedTransaction.getTags());

        return transactionRepository.save(existingTransaction);
    }

    @Transactional
    public void deleteTransaction(String userEmail, Long transactionId) {
        User user = userService.findByEmail(userEmail);
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized access to transaction");
        }

        // Reverse the transaction effect on account balance
        Account account = transaction.getAccount();
        BigDecimal currentBalance = account.getCurrentBalance();
        switch (transaction.getType()) {
            case INCOME:
                account.setCurrentBalance(currentBalance.subtract(transaction.getAmount()));
                break;
            case EXPENSE:
                account.setCurrentBalance(currentBalance.add(transaction.getAmount()));
                break;
            case TRANSFER:
                // Handle transfer reversal logic here
                break;
        }

        accountService.updateBalance(userEmail, account.getId(), account.getCurrentBalance());
        transactionRepository.delete(transaction);
    }

    public Page<Transaction> getTransactionsByDateRange(
            String userEmail,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        User user = userService.findByEmail(userEmail);
        return transactionRepository.findByUserAndDateRange(user, startDate, endDate, pageable);
    }

    public Page<Transaction> getTransactionsByCategory(
            String userEmail,
            String category,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        User user = userService.findByEmail(userEmail);
        return transactionRepository.findByUserAndCategoryAndDateRange(user, category, startDate, endDate, pageable);
    }

    public BigDecimal getTotalAmountByType(
            String userEmail,
            Transaction.TransactionType type,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        User user = userService.findByEmail(userEmail);
        return transactionRepository.getTotalAmountByTypeAndDateRange(user, type, startDate, endDate);
    }
} 