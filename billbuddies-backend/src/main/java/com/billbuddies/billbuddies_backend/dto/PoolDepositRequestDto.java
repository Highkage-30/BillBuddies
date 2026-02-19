package com.billbuddies.billbuddies_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PoolDepositRequestDto {

    private String memberName;
    private BigDecimal amount;
    private String description;
    private LocalDate expenseDate;   // ✅ NEW
}