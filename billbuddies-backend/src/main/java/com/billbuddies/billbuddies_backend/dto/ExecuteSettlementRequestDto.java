package com.billbuddies.billbuddies_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExecuteSettlementRequestDto {

    private Long fromMemberId;
    private Long toMemberId;
    private BigDecimal amount;
}
