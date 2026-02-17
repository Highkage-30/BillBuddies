package com.billbuddies.billbuddies_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "original_expense",
        indexes = {
                @Index(name = "idx_expense_group", columnList = "group_id"),
                @Index(name = "idx_expense_paid_by", columnList = "paid_by_name"),
                @Index(name = "idx_expense_paid_to", columnList = "paid_to_name"),
                @Index(name = "idx_expense_group_date", columnList = "group_id, expense_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OriginalExpense {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "original_expense_seq_gen"
    )
    @SequenceGenerator(
            name = "original_expense_seq_gen",
            sequenceName = "original_expense_seq",
            allocationSize = 1
    )
    @Column(name = "original_expense_id")
    private Long originalExpenseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupInfo group;

    @Column(name = "paid_by_name", nullable = false)
    private String paidByName;

    @Column(name = "paid_to_name", nullable = false)
    private String paidToName;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
