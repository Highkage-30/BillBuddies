package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.*;
import com.billbuddies.billbuddies_backend.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/expenses")
@RequiredArgsConstructor
@Slf4j
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public CreateExpenseResponseDto createExpense(
            @PathVariable Long groupId,
            @RequestBody CreateExpenseRequestDto request
    ) {
        log.info("POST /api/v1/groups/{}/expenses called", groupId);
        return expenseService.createExpense(groupId, request);
    }

    @GetMapping
    public List<OriginalExpenseResponseDto> getGroupExpenses(
            @PathVariable Long groupId
    ) {
        log.info("GET /api/v1/groups/{}/expenses called", groupId);
        return expenseService.getGroupExpenses(groupId);
    }
    @DeleteMapping("/{expenseId}")
    public void deleteExpense(
            @PathVariable Long groupId,
            @PathVariable Long expenseId
    ) {
        log.info("DELETE /api/v1/groups/{}/expenses/{}", groupId, expenseId);
        expenseService.deleteExpense(groupId, expenseId);
    }
}
