package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.PoolDepositRequestDto;
import com.billbuddies.billbuddies_backend.entity.*;
import com.billbuddies.billbuddies_backend.entity.enums.PoolTransactionType;
import com.billbuddies.billbuddies_backend.entity.enums.TransactionDirection;
import com.billbuddies.billbuddies_backend.entity.enums.TransactionReason;
import com.billbuddies.billbuddies_backend.exception.BadRequestException;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.*;
import com.billbuddies.billbuddies_backend.service.PoolDepositService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
@RequiredArgsConstructor
@Slf4j
public class PoolDepositServiceImpl implements PoolDepositService {

    private final GroupInfoRepository groupInfoRepository;
    private final GroupPoolRepository groupPoolRepository;
    private final PoolTransactionRepository poolTransactionRepository;
    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberTransactionRepository memberTransactionRepository;
    private final OriginalExpenseRepository originalExpenseRepository;

    @Override
    @Transactional
    public void deposit(Long groupId, PoolDepositRequestDto request) {

        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Deposit amount must be positive");
        }

        if (request.getExpenseDate() == null) {
            throw new BadRequestException("Expense date is required");
        }

        GroupInfo group = groupInfoRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found: " + groupId));

        // 🔥 AUTO-CREATE POOL IF MISSING
        GroupPool pool = groupPoolRepository
                .findByGroup_GroupId(groupId)
                .orElseGet(() -> groupPoolRepository.save(
                        GroupPool.builder()
                                .group(group)
                                .balance(BigDecimal.ZERO)
                                .build()
                ));

        Member member = memberRepository
                .findByMemberNameIgnoreCase(request.getMemberName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found"));

        if (!groupMemberRepository.existsById(
                new GroupMemberId(groupId, member.getMemberId()))) {
            throw new BadRequestException("Member not part of this group");
        }

        // ===============================
        // 1️⃣ CREATE ORIGINAL EXPENSE
        // ===============================
        OriginalExpense expense = originalExpenseRepository.save(
                OriginalExpense.builder()
                        .group(group)
                        .paidByName(member.getMemberName())
                        .paidToName("BillBuddy")
                        .amount(request.getAmount())
                        .expenseDate(request.getExpenseDate())
                        .description("BillBuddy Deposit" +
                                (request.getDescription() != null ?
                                        " - " + request.getDescription() : ""))
                        .build()
        );

        // ===============================
        // 2️⃣ MEMBER LEDGER ENTRY
        // ===============================
        memberTransactionRepository.save(
                MemberTransaction.builder()
                        .group(group)
                        .member(member)
                        .originalExpense(expense)
                        .direction(TransactionDirection.CREDIT)
                        .amount(request.getAmount())
                        .reason(TransactionReason.BILLBUDDY_DEPOSIT)  // ✅ FIXED ENUM
                        .counterpartyName("BillBuddy")
                        .build()
        );

        // ===============================
        // 3️⃣ POOL TRANSACTION ENTRY
        // ===============================
        poolTransactionRepository.save(
                PoolTransaction.builder()
                        .groupPool(pool)
                        .member(member)
                        .type(PoolTransactionType.DEPOSIT)
                        .paidToName("BillBuddy")
                        .amount(request.getAmount())
                        .description(request.getDescription())
                        .expenseDate(request.getExpenseDate())
                        .build()
        );

        // ===============================
        // 4️⃣ UPDATE POOL BALANCE
        // ===============================
        pool.setBalance(pool.getBalance().add(request.getAmount()));
        groupPoolRepository.save(pool); // 🔥 important

        log.info("BillBuddy deposit successful. New balance={}", pool.getBalance());
    }
}
