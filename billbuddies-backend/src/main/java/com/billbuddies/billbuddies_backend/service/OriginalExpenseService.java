package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.AddExpenseRequestDto;
import com.billbuddies.billbuddies_backend.dto.AddExpenseResponseDto;
import com.billbuddies.billbuddies_backend.dto.ExpenseResponseDto;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface OriginalExpenseService {
    List<ExpenseResponseDto> getAllExpenses(Long groupId);
    AddExpenseResponseDto createExpense(Long groupId, AddExpenseRequestDto addExpenseRequestDto);
}
