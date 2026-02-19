package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.ExecuteSettlementRequestDto;
import com.billbuddies.billbuddies_backend.entity.*;
import com.billbuddies.billbuddies_backend.entity.enums.TransactionDirection;
import com.billbuddies.billbuddies_backend.entity.enums.TransactionReason;
import com.billbuddies.billbuddies_backend.exception.BadRequestException;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.*;
import com.billbuddies.billbuddies_backend.service.SettlementExecutionService;
import com.billbuddies.billbuddies_backend.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementExecutionServiceImpl implements SettlementExecutionService {

    private final GroupInfoRepository groupInfoRepository;
    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final OriginalExpenseRepository originalExpenseRepository;
    private final MemberTransactionRepository memberTransactionRepository;
    private final SettlementService settlementService;

    @Override
    @Transactional
    public void executeSettlement(
            Long groupId,
            ExecuteSettlementRequestDto request
    ) {

        log.info("Executing settlement for groupId={}", groupId);

        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Settlement amount must be greater than zero");
        }

        GroupInfo group = groupInfoRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found with id: " + groupId));

        Member fromMember = memberRepository.findById(request.getFromMemberId())
                .orElseThrow(() ->
                        new BadRequestException("From member not found"));

        Member toMember = memberRepository.findById(request.getToMemberId())
                .orElseThrow(() ->
                        new BadRequestException("To member not found"));

        validateMemberInGroup(groupId, fromMember);
        validateMemberInGroup(groupId, toMember);

        // 1️⃣ Original expense (audit trail)
        OriginalExpense expense = originalExpenseRepository.save(
                OriginalExpense.builder()
                        .group(group)
                        .paidByName(fromMember.getMemberName())
                        .paidToName(toMember.getMemberName())
                        .amount(request.getAmount())
                        .expenseDate(LocalDate.now())
                        .description("Settlement")
                        .build()
        );

        // 2️⃣ Ledger entries
        memberTransactionRepository.save(
                MemberTransaction.builder()
                        .group(group)
                        .member(fromMember)
                        .originalExpense(expense)
                        .direction(TransactionDirection.CREDIT)
                        .amount(request.getAmount())
                        .reason(TransactionReason.SETTLED)
                        .counterpartyName(toMember.getMemberName())
                        .build()
        );

        memberTransactionRepository.save(
                MemberTransaction.builder()
                        .group(group)
                        .member(toMember)
                        .originalExpense(expense)
                        .direction(TransactionDirection.DEBIT)
                        .amount(request.getAmount())
                        .reason(TransactionReason.RECEIVED)
                        .counterpartyName(fromMember.getMemberName())
                        .build()
        );

        // 3️⃣ Recompute statement snapshot
        settlementService.settleGroup(groupId);

        log.info("Settlement executed successfully for groupId={}", groupId);
    }

    private void validateMemberInGroup(Long groupId, Member member) {
        if (!groupMemberRepository.existsById(
                new GroupMemberId(groupId, member.getMemberId()))) {
            throw new BadRequestException("Member is not part of the group");
        }
    }
}
