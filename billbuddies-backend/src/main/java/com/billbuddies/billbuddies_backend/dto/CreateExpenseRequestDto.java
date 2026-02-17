package com.billbuddies.billbuddies_backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateExpenseRequestDto {

    private BigDecimal amount;
    private String description;
    private LocalDate expenseDate;
    private String paidByName;
    private String paidToName;
}
