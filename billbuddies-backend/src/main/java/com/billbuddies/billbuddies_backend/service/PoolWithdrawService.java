package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.PoolWithdrawRequestDto;

public interface PoolWithdrawService {

    void withdraw(Long groupId, PoolWithdrawRequestDto request);
}
