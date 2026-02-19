package com.billbuddies.billbuddies_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class GroupPoolResponseDto {
    private Long poolId;
    private Long groupId;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}
