package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.MemberGroupStatementDto;
import com.billbuddies.billbuddies_backend.dto.MemberResponseDto;
import com.billbuddies.billbuddies_backend.dto.MemberStatementResponseDto;
import com.billbuddies.billbuddies_backend.entity.Member;
import com.billbuddies.billbuddies_backend.entity.Statement;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.MemberRepository;
import com.billbuddies.billbuddies_backend.repository.StatementRepository;
import com.billbuddies.billbuddies_backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final StatementRepository statementRepository;
    @Override
    public List<MemberResponseDto> getAllMembers() {
        log.info("Fetching all members");

        return memberRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }
    @Override
    public MemberStatementResponseDto getMemberStatement(Long memberId) {

        log.info("Fetching member statement for memberId={}", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found with id: " + memberId));

        List<Statement> statements =
                statementRepository.findByMember_MemberId(memberId);

        List<MemberGroupStatementDto> groupStatements = statements.stream()
                .map(s -> MemberGroupStatementDto.builder()
                        .groupId(s.getGroup().getGroupId())
                        .groupName(s.getGroup().getGroupName())
                        .credit(s.getCredit())
                        .debit(s.getDebit())
                        .balance(s.getBalance())
                        .build())
                .toList();

        return MemberStatementResponseDto.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .groups(groupStatements)
                .build();
    }
    private MemberResponseDto toDto(Member member) {
        return MemberResponseDto.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .build();
    }
}
