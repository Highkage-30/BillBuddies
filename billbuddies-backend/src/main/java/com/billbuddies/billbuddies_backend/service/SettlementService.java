package com.billbuddies.billbuddies_backend.service;


import com.billbuddies.billbuddies_backend.dto.SettlementPreviewResponseDto;

public interface SettlementService {
    void settleGroup(Long groupId);

    SettlementPreviewResponseDto previewSettlement(Long groupId);

}
