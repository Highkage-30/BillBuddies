package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.PoolDepositRequestDto;

public interface PoolDepositService {
    void deposit(Long groupId, PoolDepositRequestDto request);
}
