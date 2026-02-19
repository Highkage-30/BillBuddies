package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.SettlementPreviewResponseDto;
import com.billbuddies.billbuddies_backend.service.SettlementPreviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/settle")
@RequiredArgsConstructor
@Slf4j
public class SettlementPreviewController {

    private final SettlementPreviewService settlementPreviewService;

    @GetMapping
    public SettlementPreviewResponseDto previewSettlement(
            @PathVariable Long groupId
    ) {
        log.info("GET /api/v1/groups/{}/settle called", groupId);
        return settlementPreviewService.previewSettlement(groupId);
    }
}
