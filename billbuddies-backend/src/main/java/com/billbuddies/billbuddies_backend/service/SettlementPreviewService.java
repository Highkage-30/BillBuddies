package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.SettlementPreviewResponseDto;

public interface SettlementPreviewService {

    SettlementPreviewResponseDto previewSettlement(Long groupId);
}
