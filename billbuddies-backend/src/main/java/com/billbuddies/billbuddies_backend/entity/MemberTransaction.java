package com.billbuddies.billbuddies_backend.entity;

import com.billbuddies.billbuddies_backend.entity.enums.TransactionDirection;
import com.billbuddies.billbuddies_backend.entity.enums.TransactionReason;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "member_transaction",
        indexes = {
                @Index(name = "idx_txn_group", columnList = "group_id"),
                @Index(name = "idx_txn_member", columnList = "member_id"),
                @Index(name = "idx_txn_expense", columnList = "original_expense_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberTransaction {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "member_transaction_seq_gen"
    )
    @SequenceGenerator(
            name = "member_transaction_seq_gen",
            sequenceName = "member_transaction_seq",
            allocationSize = 1
    )
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupInfo group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_expense_id", nullable = false)
    private OriginalExpense originalExpense;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    private TransactionDirection direction;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private TransactionReason reason;

    @Column(name = "counterparty_name")
    private String counterpartyName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
