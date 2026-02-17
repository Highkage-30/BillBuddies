package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.PoolDepositRequestDto;
import com.billbuddies.billbuddies_backend.service.PoolDepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class PoolDepositController {

    private final PoolDepositService poolDepositService;

    @PostMapping("/{groupId}/pool/deposit")
    public void depositToPool(
            @PathVariable Long groupId,
            @RequestBody PoolDepositRequestDto request
    ) {
        poolDepositService.deposit(groupId, request);
    }
}
