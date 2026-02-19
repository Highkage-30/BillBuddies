package com.billbuddies.billbuddies_backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OriginalExpenseResponseDto {

    private Long originalExpenseId;
    private String paidByName;
    private String paidToName;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String description;
    private LocalDateTime createdAt;
}
