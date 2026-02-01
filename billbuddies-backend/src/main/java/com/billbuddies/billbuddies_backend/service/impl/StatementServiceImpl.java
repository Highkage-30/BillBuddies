package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.StatementResponseDto;
import com.billbuddies.billbuddies_backend.repository.StatementRepository;
import com.billbuddies.billbuddies_backend.service.StatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatementServiceImpl implements StatementService {

    private final StatementRepository statementRepository;

    @Override
    public List<StatementResponseDto> getGroupStatement(Long groupId) {

        return statementRepository
                .findByGroupIdOrderByMemberNameAsc(groupId)
                .stream()
                .map(s -> new StatementResponseDto(
                        s.getMemberName(),
                        s.getCredit(),
                        s.getDebit(),
                        s.getBalance()
                ))
                .toList();
    }
}
