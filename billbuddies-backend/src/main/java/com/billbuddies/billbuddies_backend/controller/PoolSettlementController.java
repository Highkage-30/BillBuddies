package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.service.PoolSettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class PoolSettlementController {

    private final PoolSettlementService poolSettlementService;

    @PostMapping("/{groupId}/pool/settle")
    public void settlePool(@PathVariable Long groupId) {
        poolSettlementService.settle(groupId);
    }
}
