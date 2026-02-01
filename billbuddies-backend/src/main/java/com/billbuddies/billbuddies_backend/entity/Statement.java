package com.billbuddies.billbuddies_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "statement",
        indexes = {
                @Index(name = "idx_stmt_group", columnList = "group_id"),
                @Index(name = "idx_stmt_member", columnList = "member_name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(StatementId.class)
public class Statement {

    @Id
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Id
    @Column(name = "member_name", nullable = false)
    private String memberName;

    @Column(name = "credit", precision = 12, scale = 2)
    private BigDecimal credit;

    @Column(name = "debit", precision = 12, scale = 2)
    private BigDecimal debit;

    @Column(name = "balance", precision = 12, scale = 2)
    private BigDecimal balance;

    @Column(name = "settlement_date")
    private LocalDateTime settlementDate;
}
