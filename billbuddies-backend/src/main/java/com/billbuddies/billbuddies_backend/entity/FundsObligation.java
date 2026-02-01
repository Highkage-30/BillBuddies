package com.billbuddies.billbuddies_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "funds_obligation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_fo_expense_member",
                        columnNames = {"original_expense_id", "member_id"}
                )
        },
        indexes = {
                @Index(name = "idx_fo_group", columnList = "group_id"),
                @Index(name = "idx_fo_member", columnList = "member_id"),
                @Index(name = "idx_fo_original", columnList = "original_expense_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundsObligation {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "funds_obligation_seq_gen"
    )
    @SequenceGenerator(
            name = "funds_obligation_seq_gen",
            sequenceName = "funds_obligation_seq",
            allocationSize = 1
    )
    @Column(name = "funds_obligation_id")
    private Long fundsObligationId;

    /**
     * Expense for which this obligation exists
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_expense_id", nullable = false)
    private OriginalExpense originalExpense;

    /**
     * Group scope (denormalized for fast settlement)
     */
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    /**
     * Member who bears this cost
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /**
     * Amount this member owes for the expense
     */
    @Column(name = "share_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal shareAmount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
