package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.SettlementPreviewResponseDto;
import com.billbuddies.billbuddies_backend.dto.StatementResponseDto;
import com.billbuddies.billbuddies_backend.service.SettlementService;
import com.billbuddies.billbuddies_backend.service.StatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SettlementController {

    private final SettlementService settlementService;
    private final StatementService statementService;

    @PostMapping("/groups/{groupId}/settle")
    @ResponseStatus(HttpStatus.OK)
    public void settleGroup(@PathVariable Long groupId) {
        settlementService.settleGroup(groupId);
    }

    @GetMapping("/groups/{groupId}/statement")
    public List<StatementResponseDto> getStatement(
            @PathVariable Long groupId
    ) {
        return statementService.getGroupStatement(groupId);
    }
    @GetMapping("/groups/{groupId}/settle")
    public SettlementPreviewResponseDto previewSettlement(
            @PathVariable Long groupId
    ) {
        return settlementService.previewSettlement(groupId);
    }
}