package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.GroupStatementResponseDto;
import com.billbuddies.billbuddies_backend.service.StatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/statement")
@RequiredArgsConstructor
@Slf4j
public class StatementController {

    private final StatementService statementService;

    @GetMapping
    public GroupStatementResponseDto getGroupStatement(
            @PathVariable Long groupId
    ) {
        log.info("GET /api/v1/groups/{}/statement called", groupId);
        return statementService.getGroupStatement(groupId);
    }
}
