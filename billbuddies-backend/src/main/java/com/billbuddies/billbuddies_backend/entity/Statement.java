package com.billbuddies.billbuddies_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "statement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statement {

    @EmbeddedId
    private StatementId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupId")
    @JoinColumn(name = "group_id", nullable = false)
    private GroupInfo group;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "credit", precision = 15, scale = 2)
    private BigDecimal credit;

    @Column(name = "debit", precision = 15, scale = 2)
    private BigDecimal debit;

    @Column(name = "balance", precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
}
