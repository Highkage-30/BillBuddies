package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.AddExpenseRequestDto;
import com.billbuddies.billbuddies_backend.dto.AddExpenseResponseDto;
import com.billbuddies.billbuddies_backend.dto.ExpenseResponseDto;
import com.billbuddies.billbuddies_backend.entity.*;
import com.billbuddies.billbuddies_backend.exception.GroupNotFoundException;
import com.billbuddies.billbuddies_backend.exception.MemberNotFoundException;
import com.billbuddies.billbuddies_backend.exception.MemberNotInGroupException;
import com.billbuddies.billbuddies_backend.repository.*;
import com.billbuddies.billbuddies_backend.service.OriginalExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OriginalExpenseServiceImpl implements OriginalExpenseService {

    private final OriginalExpenseRepository originalExpenseRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;
    private final SplitExpenseRepository splitExpenseRepository;
    private final FundsObligationRepository fundsObligationRepository;

    @Value("${centralCounterParty.name}")
    private String CCP_NAME;

    /* ===========================
       READ EXPENSES
       =========================== */

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponseDto> getAllExpenses(Long groupId) {

        if (!groupRepository.existsById(groupId)) {
            throw new GroupNotFoundException("Group not found for id: " + groupId);
        }

        return originalExpenseRepository
                .findByGroupIdOrderByExpenseDateDesc(groupId)
                .stream()
                .map(oe -> new ExpenseResponseDto(
                        oe.getOriginalExpenseId(),
                        oe.getGroupId(),
                        oe.getPaidByName(),
                        oe.getPaidToName(),
                        oe.getAmount(),
                        oe.getExpenseDate(),
                        oe.getDescription()
                ))
                .toList();
    }

    /* ===========================
       CREATE EXPENSE
       =========================== */

    @Override
    @Transactional
    public AddExpenseResponseDto createExpense(
            Long groupId,
            AddExpenseRequestDto dto
    ) {

        if (!groupRepository.existsById(groupId)) {
            throw new GroupNotFoundException("Group not found for id: " + groupId);
        }

        // Validate paid-by member
        Member paidByMember = memberRepository
                .findByMemberNameIgnoreCase(dto.getPaidByName())
                .orElseThrow(() ->
                        new MemberNotFoundException(
                                "Member not found: " + dto.getPaidByName()
                        )
                );

        if (!groupMemberRepository.existsByGroup_GroupIdAndMember_MemberId(
                groupId,
                paidByMember.getMemberId()
        )) {
            throw new MemberNotInGroupException("Paid-by member not in group");
        }

        /* 1️⃣ Save ORIGINAL_EXPENSE */
        OriginalExpense oe = originalExpenseRepository.save(
                OriginalExpense.builder()
                        .groupId(groupId)
                        .paidByName(dto.getPaidByName())
                        .paidToName(dto.getPaidToName())
                        .amount(dto.getAmount())
                        .expenseDate(dto.getExpenseDate())
                        .description(dto.getDescription())
                        .build()
        );

        /* 2️⃣ Generate SPLIT_EXPENSE (CCP-normalized cash flow) */
        generateSplitExpenseEntries(groupId, oe);

        /* 3️⃣ Generate FUNDS_OBLIGATION (value responsibility) */
        generateFundsObligation(oe, groupId);

        return new AddExpenseResponseDto(
                oe.getOriginalExpenseId(),
                groupId,
                "CREATED"
        );
    }

    /* =====================================================
       SPLIT EXPENSE LOGIC
       - Universal CCP clearing
       - Pure cash movement
       ===================================================== */

    private void generateSplitExpenseEntries(Long groupId, OriginalExpense oe) {

        String paidBy = oe.getPaidByName();
        String paidTo = oe.getPaidToName();

        // Member/BillBuddy → CCP
        if (!paidBy.equalsIgnoreCase(CCP_NAME)) {
            splitExpenseRepository.save(
                    SplitExpense.builder()
                            .groupId(groupId)
                            .fromName(paidBy)
                            .toName(CCP_NAME)
                            .amount(oe.getAmount())
                            .originalExpense(oe)
                            .build()
            );
        }

        // CCP → Member / External
        if (!paidTo.equalsIgnoreCase(CCP_NAME)) {
            splitExpenseRepository.save(
                    SplitExpense.builder()
                            .groupId(groupId)
                            .fromName(CCP_NAME)
                            .toName(paidTo)
                            .amount(oe.getAmount())
                            .originalExpense(oe)
                            .build()
            );
        }
    }

    /* =====================================================
       FUNDS OBLIGATION LOGIC (FINAL MODEL)
       ===================================================== */
    private void generateFundsObligation(OriginalExpense oe, Long groupId) {

        String paidTo = oe.getPaidToName();
        BigDecimal amount = oe.getAmount();

        boolean paidToIsMember =
                memberRepository.findByMemberNameIgnoreCase(paidTo).isPresent();

        boolean paidToIsCCP =
                paidTo.equalsIgnoreCase(CCP_NAME);

    /* =====================================================
       CASE 1: MEMBER → MEMBER
       - Not shared
       - Receiver bears FULL responsibility
       ===================================================== */
        if (paidToIsMember && !paidToIsCCP) {

            Member beneficiary =
                    memberRepository.findByMemberNameIgnoreCase(paidTo).get();

            fundsObligationRepository.save(
                    FundsObligation.builder()
                            .originalExpense(oe)
                            .groupId(groupId)
                            .member(beneficiary)
                            .shareAmount(amount)
                            .build()
            );
            return;
        }

    /* =====================================================
       CASE 2: MEMBER → EXTERNAL (SHARED EXPENSE)
       - Split among ALL real members (including payer)
       - BillBuddy excluded
       ===================================================== */
        if (!paidToIsMember && !paidToIsCCP) {

            List<GroupMember> members =
                    groupMemberRepository
                            .findByGroup_GroupIdOrderByMember_MemberNameAsc(groupId)
                            .stream()
                            .filter(gm ->
                                    !gm.getMember()
                                            .getMemberName()
                                            .equalsIgnoreCase(CCP_NAME)
                            )
                            .toList();

            if (members.isEmpty()) {
                throw new IllegalStateException(
                        "No members available for shared expense " + oe.getOriginalExpenseId()
                );
            }

            BigDecimal share =
                    amount.divide(
                            BigDecimal.valueOf(members.size()),
                            2,
                            RoundingMode.HALF_UP
                    );

            for (GroupMember gm : members) {
                fundsObligationRepository.save(
                        FundsObligation.builder()
                                .originalExpense(oe)
                                .groupId(groupId)
                                .member(gm.getMember())
                                .shareAmount(share)
                                .build()
                );
            }
            return;
        }

    /* =====================================================
       CASE 3: MEMBER → BILLBUDDY (PREFUNDING)
       - No value consumed
       - No obligation created
       ===================================================== */
    }

}
