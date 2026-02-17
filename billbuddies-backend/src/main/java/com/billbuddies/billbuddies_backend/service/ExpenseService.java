package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.CreateExpenseRequestDto;
import com.billbuddies.billbuddies_backend.dto.CreateExpenseResponseDto;
import com.billbuddies.billbuddies_backend.dto.OriginalExpenseResponseDto;

import java.util.List;

public interface ExpenseService {

    CreateExpenseResponseDto createExpense(Long groupId, CreateExpenseRequestDto request);

    List<OriginalExpenseResponseDto> getGroupExpenses(Long groupId);

    void deleteExpense(Long groupId, Long originalExpenseId);

}
