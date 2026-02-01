package com.billbuddies.billbuddies_backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AddExpenseRequestDto {
    @NotBlank(message = "PaidBy is required ")
    private String paidByName;

    @NotBlank(message = "PaidTo is required")
    private String paidToName;

    @Positive(message = "Amount must be positive")
    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;

    @PastOrPresent(message = "Date should be past or present,cannot be future")
    private LocalDate expenseDate;

    @Size(max = 100,message = "Description cannot be more than 100 character")
    private String description;
}
