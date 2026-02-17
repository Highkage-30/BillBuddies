package com.billbuddies.billbuddies_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PoolTransactionDto {
    private Long transactionId;
    private Long memberId;
    private String memberName;
    private String type;
    private BigDecimal amount;
    private String paidToName;   // ✅ NEW
    private String description;
    private LocalDate expenseDate;
    private LocalDateTime createdAt;
}
