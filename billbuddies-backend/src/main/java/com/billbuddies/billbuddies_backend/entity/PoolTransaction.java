package com.billbuddies.billbuddies_backend.entity;

import com.billbuddies.billbuddies_backend.entity.enums.PoolTransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "pool_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoolTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pool_tx_seq_gen")
    @SequenceGenerator(
            name = "pool_tx_seq_gen",
            sequenceName = "pool_transaction_seq",
            allocationSize = 1
    )
    @Column(name = "pool_transaction_id")
    private Long poolTransactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pool_id", nullable = false)
    private GroupPool groupPool;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // only for DEPOSIT

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PoolTransactionType type;

    @Column(name = "paid_to_name")
    private String paidToName;   // ✅ NEW

    private String description;

    private LocalDate expenseDate;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
