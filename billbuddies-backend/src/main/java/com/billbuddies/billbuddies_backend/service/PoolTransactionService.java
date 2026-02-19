package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.GroupPoolTransactionsResponseDto;

public interface PoolTransactionService {
    GroupPoolTransactionsResponseDto getPoolTransactions(Long groupId);
}
