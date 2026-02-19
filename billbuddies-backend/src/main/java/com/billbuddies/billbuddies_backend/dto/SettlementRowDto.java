package com.billbuddies.billbuddies_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SettlementRowDto {

    private Long fromMemberId;
    private String fromMemberName;

    private Long toMemberId;
    private String toMemberName;

    private BigDecimal amount;
}
