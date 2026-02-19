package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.GroupStatementResponseDto;

public interface StatementService {

    GroupStatementResponseDto getGroupStatement(Long groupId);
}
