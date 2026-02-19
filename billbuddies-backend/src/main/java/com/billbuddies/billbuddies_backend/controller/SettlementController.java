package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/settle")
@RequiredArgsConstructor
@Slf4j
public class SettlementController {

    private final SettlementService settlementService;

    @PostMapping
    public void settleGroup(@PathVariable Long groupId) {
        log.info("POST /api/v1/groups/{}/settle called", groupId);
        settlementService.settleGroup(groupId);
    }
}
