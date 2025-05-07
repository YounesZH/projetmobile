package com.smartfinance.repository;

import com.smartfinance.model.Account;
import com.smartfinance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);
    List<Account> findByUserAndIsActiveTrue(User user);
    Optional<Account> findByIdAndUser(Long id, User user);
    boolean existsByUserAndName(User user, String name);
} 