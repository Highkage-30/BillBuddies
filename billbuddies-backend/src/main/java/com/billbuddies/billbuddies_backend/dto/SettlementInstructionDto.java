package com.billbuddies.billbuddies_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SettlementInstructionDto {

    private String fromName;
    private String toName;
    private BigDecimal amount;
}
