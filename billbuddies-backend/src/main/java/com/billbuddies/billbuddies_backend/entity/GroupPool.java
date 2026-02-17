package com.billbuddies.billbuddies_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "group_pool",
        uniqueConstraints = @UniqueConstraint(columnNames = "group_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupPool {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_pool_seq")
    @SequenceGenerator(
            name = "group_pool_seq",
            sequenceName = "group_pool_seq",
            allocationSize = 1
    )
    @Column(name = "pool_id")
    private Long poolId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupInfo group;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (balance == null) balance = BigDecimal.ZERO;
        createdAt = LocalDateTime.now();
    }
}
