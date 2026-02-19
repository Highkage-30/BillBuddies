package com.billbuddies.billbuddies_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SettlementBreakdownDto {
    private Long toMemberId;
    private String toMemberName;
    private BigDecimal amount;
}
