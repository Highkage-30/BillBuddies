package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.ExecuteSettlementRequestDto;
import com.billbuddies.billbuddies_backend.service.SettlementExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/settle")
@RequiredArgsConstructor
@Slf4j
public class SettlementExecutionController {

    private final SettlementExecutionService settlementExecutionService;

    @PostMapping("/execute")
    public void executeSettlement(
            @PathVariable Long groupId,
            @RequestBody ExecuteSettlementRequestDto request
    ) {
        log.info("POST /api/v1/groups/{}/settle/execute called", groupId);
        settlementExecutionService.executeSettlement(groupId, request);
    }
}
