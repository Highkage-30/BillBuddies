package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.GroupPoolResponseDto;
import com.billbuddies.billbuddies_backend.dto.GroupPoolTransactionsResponseDto;
import com.billbuddies.billbuddies_backend.dto.PoolWithdrawRequestDto;
import com.billbuddies.billbuddies_backend.service.PoolService;
import com.billbuddies.billbuddies_backend.service.PoolTransactionService;
import com.billbuddies.billbuddies_backend.service.PoolWithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class PoolController {

    private final PoolService poolService;
    private final PoolTransactionService poolTransactionService;
    private final PoolWithdrawService poolWithdrawService;

    @GetMapping("/{groupId}/pool")
    public GroupPoolResponseDto getGroupPool(
            @PathVariable Long groupId
    ) {
        return poolService.getGroupPool(groupId);
    }
    @GetMapping("/{groupId}/pool/transactions")
    public GroupPoolTransactionsResponseDto getPoolTransactions(
            @PathVariable Long groupId
    ) {
        return poolTransactionService.getPoolTransactions(groupId);
    }
    @PostMapping("/{groupId}/pool/withdraw")
    public void withdrawFromPool(
            @PathVariable Long groupId,
            @RequestBody PoolWithdrawRequestDto request
    ) {
        poolWithdrawService.withdraw(groupId, request);
    }
}
