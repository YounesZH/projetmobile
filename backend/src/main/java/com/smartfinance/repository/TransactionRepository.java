package com.smartfinance.repository;

import com.smartfinance.model.Account;
import com.smartfinance.model.Transaction;
import com.smartfinance.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUser(User user, Pageable pageable);
    Page<Transaction> findByUserAndAccount(User user, Account account, Pageable pageable);
    List<Transaction> findByUserAndIsRecurringTrue(User user);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = ?1 AND t.transactionDate BETWEEN ?2 AND ?3")
    Page<Transaction> findByUserAndDateRange(User user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = ?1 AND t.category = ?2 AND t.transactionDate BETWEEN ?3 AND ?4")
    Page<Transaction> findByUserAndCategoryAndDateRange(User user, String category, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = ?1 AND t.type = ?2 AND t.transactionDate BETWEEN ?3 AND ?4")
    BigDecimal getTotalAmountByTypeAndDateRange(User user, Transaction.TransactionType type, LocalDateTime startDate, LocalDateTime endDate);
} 