package com.billbuddies.billbuddies_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupPoolTransactionsResponseDto {
    private Long groupId;
    private Long poolId;
    private List<PoolTransactionDto> transactions;
}
