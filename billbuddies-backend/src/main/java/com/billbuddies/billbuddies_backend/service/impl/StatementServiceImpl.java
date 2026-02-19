package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.GroupStatementResponseDto;
import com.billbuddies.billbuddies_backend.dto.MemberStatementDto;
import com.billbuddies.billbuddies_backend.entity.Statement;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.GroupInfoRepository;
import com.billbuddies.billbuddies_backend.repository.StatementRepository;
import com.billbuddies.billbuddies_backend.service.StatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatementServiceImpl implements StatementService {

    private final GroupInfoRepository groupInfoRepository;
    private final StatementRepository statementRepository;
    @Override
    public GroupStatementResponseDto getGroupStatement(Long groupId) {

        log.info("Fetching statement for groupId={}", groupId);

        if (!groupInfoRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }
        String groupName= groupInfoRepository.findById(groupId).get().getGroupName();
        log.info("Fetching statement for groupName={}", groupName);
        List<Statement> statements =
                statementRepository.findByGroup_GroupId(groupId);

        if (statements.isEmpty()) {
            return GroupStatementResponseDto.builder()
                    .groupId(groupId)
                    .groupName(groupName)
                    .generatedAt(null)
                    .members(List.of())
                    .build();
        }

        LocalDateTime generatedAt = statements.get(0).getGeneratedAt();

        List<MemberStatementDto> members = statements.stream()
                .map(s -> MemberStatementDto.builder()
                        .memberId(s.getMember().getMemberId())
                        .memberName(s.getMember().getMemberName())
                        .credit(s.getCredit())
                        .debit(s.getDebit())
                        .balance(s.getBalance())
                        .build())
                .toList();

        return GroupStatementResponseDto.builder()
                .groupId(groupId)
                .groupName(groupName)
                .generatedAt(generatedAt)
                .members(members)
                .build();
    }
}
