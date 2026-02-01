package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.StatementResponseDto;

import java.util.List;

public interface StatementService {

    List<StatementResponseDto> getGroupStatement(Long groupId);
}
