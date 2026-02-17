package com.billbuddies.billbuddies_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateExpenseResponseDto {

    private Long originalExpenseId;
    private String status;
}
