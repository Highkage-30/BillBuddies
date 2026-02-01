package com.billbuddies.billbuddies_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SettlementPreviewItemDto {
    private String fromMemberName;
    private String toMemberName;
    private BigDecimal amount;
}
