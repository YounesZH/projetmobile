package com.smartfinance.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "budgets")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    private String description;

    @Column(name = "amount_limit", nullable = false)
    private BigDecimal amountLimit;

    @Column(name = "spent_amount", nullable = false)
    private BigDecimal spentAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private String currency;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "roll_over")
    private boolean rollOver = false;

    @Column(name = "notification_threshold")
    private Integer notificationThreshold = 80; // Percentage

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public BigDecimal getRemainingAmount() {
        return amountLimit.subtract(spentAmount);
    }

    public int getProgressPercentage() {
        if (amountLimit.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return spentAmount.multiply(new BigDecimal("100"))
                .divide(amountLimit, 2, BigDecimal.ROUND_HALF_UP)
                .intValue();
    }
} 