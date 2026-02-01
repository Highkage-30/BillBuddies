package com.billbuddies.billbuddies_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class StatementResponseDto {
    private String memberName;
    private BigDecimal credit;
    private BigDecimal debit;
    private BigDecimal balance;
}
