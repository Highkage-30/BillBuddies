package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.ExecuteSettlementRequestDto;

public interface SettlementExecutionService {

    void executeSettlement(Long groupId, ExecuteSettlementRequestDto request);
}
