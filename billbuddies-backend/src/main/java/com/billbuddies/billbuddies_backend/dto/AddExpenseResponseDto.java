package com.billbuddies.billbuddies_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddExpenseResponseDto {
    private Long originalExpenseId;
    private Long groupId;
    private String status;
}
