package com.billbuddies.billbuddies_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MemberGroupStatementDto {
    private Long groupId;
    private String groupName;
    private BigDecimal credit;
    private BigDecimal debit;
    private BigDecimal balance;
}