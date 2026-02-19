package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.GroupPoolResponseDto;
import com.billbuddies.billbuddies_backend.dto.GroupPoolTransactionsResponseDto;

public interface PoolService {
    GroupPoolResponseDto getGroupPool(Long groupId);
}
