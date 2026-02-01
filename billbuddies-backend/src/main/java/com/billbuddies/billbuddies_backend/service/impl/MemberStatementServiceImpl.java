package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.MemberGroupStatementDto;
import com.billbuddies.billbuddies_backend.dto.MemberStatementResponseDto;
import com.billbuddies.billbuddies_backend.entity.Group;
import com.billbuddies.billbuddies_backend.entity.GroupMember;
import com.billbuddies.billbuddies_backend.entity.Member;
import com.billbuddies.billbuddies_backend.entity.Statement;
import com.billbuddies.billbuddies_backend.exception.MemberNotFoundException;
import com.billbuddies.billbuddies_backend.repository.GroupMemberRepository;
import com.billbuddies.billbuddies_backend.repository.MemberRepository;
import com.billbuddies.billbuddies_backend.repository.StatementRepository;
import com.billbuddies.billbuddies_backend.service.MemberStatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberStatementServiceImpl implements MemberStatementService {

    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final StatementRepository statementRepository;

    @Override
    public MemberStatementResponseDto getMemberStatement(Long memberId) {

        /* 1️⃣ Validate member exists */
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new MemberNotFoundException(
                                "Member not found for id: " + memberId
                        )
                );

        /* 2️⃣ Fetch all group memberships */
        List<GroupMember> memberships =
                groupMemberRepository.findByMember_MemberId(memberId);

        List<MemberGroupStatementDto> groupStatements = new ArrayList<>();

        /* 3️⃣ Build group-wise statement view */
        for (GroupMember gm : memberships) {

            Group group = gm.getGroup();

            Optional<Statement> stmtOpt =
                    statementRepository.findByGroupIdAndMemberName(
                            group.getGroupId(),
                            member.getMemberName()
                    );

            BigDecimal credit = BigDecimal.ZERO;
            BigDecimal debit = BigDecimal.ZERO;
            BigDecimal balance = BigDecimal.ZERO;

            if (stmtOpt.isPresent()) {
                Statement s = stmtOpt.get();
                credit = s.getCredit();
                debit = s.getDebit();
                balance = s.getBalance();
            }

            groupStatements.add(
                    MemberGroupStatementDto.builder()
                            .groupId(group.getGroupId())
                            .groupName(group.getGroupName())
                            .credit(credit)
                            .debit(debit)
                            .balance(balance)
                            .build()
            );
        }

        /* 4️⃣ Final response */
        return MemberStatementResponseDto.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .groups(groupStatements)
                .build();
    }
}
