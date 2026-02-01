package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.AddExpenseRequestDto;
import com.billbuddies.billbuddies_backend.dto.AddExpenseResponseDto;
import com.billbuddies.billbuddies_backend.dto.ExpenseResponseDto;
import com.billbuddies.billbuddies_backend.service.OriginalExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OriginalExpenseController {
    private final OriginalExpenseService originalExpenseService;
    @GetMapping("/groups/{groupId}/expenses")
    public List<ExpenseResponseDto> getAllExpenses(@PathVariable Long groupId) {
        return originalExpenseService.getAllExpenses(groupId);
    }
    @PostMapping("/groups/{groupId}/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public AddExpenseResponseDto createExpense(@PathVariable Long groupId, @RequestBody @Valid AddExpenseRequestDto  addExpenseRequestDto) {
        log.info("Creating expense {}", addExpenseRequestDto);
        return originalExpenseService.createExpense(groupId, addExpenseRequestDto);
    }
}
