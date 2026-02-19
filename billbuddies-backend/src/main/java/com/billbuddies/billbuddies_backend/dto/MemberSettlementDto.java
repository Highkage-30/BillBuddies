package com.billbuddies.billbuddies_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class MemberSettlementDto {
    private Long fromMemberId;
    private String fromMemberName;
    private BigDecimal totalAmount;
    private List<SettlementBreakdownDto> breakdowns;
}
