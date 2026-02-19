package com.billbuddies.billbuddies_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ExpenseUploadRowDto {
    private String paidByName;
    private String paidToName;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String description;
}
