package com.billbuddies.billbuddies_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MemberStatementDto {

    private Long memberId;
    private String memberName;
    private BigDecimal credit;
    private BigDecimal debit;
    private BigDecimal balance;
}
