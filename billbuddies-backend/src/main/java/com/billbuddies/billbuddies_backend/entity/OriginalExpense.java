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
                @Index(name = "idx_oe_group", columnList = "group_id"),
                @Index(name = "idx_oe_paid_by", columnList = "paid_by_name"),
                @Index(name = "idx_oe_paid_to", columnList = "paid_to_name"),
                @Index(name = "idx_oe_group_date", columnList = "group_id, expense_date")
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

    /**
     * Group in which expense occurred
     */
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    /**
     * Member who actually paid the money
     * MUST be a member of the group (validated in service layer)
     */
    @Column(name = "paid_by_name", nullable = false)
    private String paidByName;

    /**
     * Can be anyone:
     * - member
     * - BillBuddy
     * - external entity
     * No FK by design.
     */
    @Column(name = "paid_to_name", nullable = false)
    private String paidToName;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "expense_date")
    private LocalDate expenseDate;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
