package com.billbuddies.billbuddies_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ExpenseResponseDto {
    private Long originalExpenseId;
    private Long groupId;
    private String paidBy;
    private String paidTo;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String description;
}
