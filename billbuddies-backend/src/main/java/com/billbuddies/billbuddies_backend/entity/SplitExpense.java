package com.billbuddies.billbuddies_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "split_expense",
        indexes = {
                @Index(name = "idx_se_group", columnList = "group_id"),
                @Index(name = "idx_se_from", columnList = "from_name"),
                @Index(name = "idx_se_to", columnList = "to_name"),
                @Index(name = "idx_se_original", columnList = "original_expense_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SplitExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "split_expense_seq_gen")
    @SequenceGenerator(
            name = "split_expense_seq_gen",
            sequenceName = "split_expense_seq",
            allocationSize = 1
    )
    @Column(name = "split_expense_id")
    private Long splitExpenseId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "from_name", nullable = false)
    private String fromName;

    @Column(name = "to_name", nullable = false)
    private String toName;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_expense_id", nullable = false)
    private OriginalExpense originalExpense;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
