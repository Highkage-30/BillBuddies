package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.PoolWithdrawRequestDto;
import com.billbuddies.billbuddies_backend.entity.*;
import com.billbuddies.billbuddies_backend.entity.enums.PoolTransactionType;
import com.billbuddies.billbuddies_backend.entity.enums.TransactionDirection;
import com.billbuddies.billbuddies_backend.entity.enums.TransactionReason;
import com.billbuddies.billbuddies_backend.exception.BadRequestException;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.*;
import com.billbuddies.billbuddies_backend.service.PoolWithdrawService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoolWithdrawServiceImpl implements PoolWithdrawService {

    private final GroupInfoRepository groupInfoRepository;
    private final GroupPoolRepository groupPoolRepository;
    private final PoolTransactionRepository poolTransactionRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberTransactionRepository memberTransactionRepository;
    private final OriginalExpenseRepository originalExpenseRepository;

    @Override
    @Transactional
    public void withdraw(Long groupId, PoolWithdrawRequestDto request) {

        log.info("BillBuddy withdraw initiated: groupId={}, amount={}, paidTo={}",
                groupId, request.getAmount(), request.getPaidToName());

        // ===============================
        // 1️⃣ VALIDATIONS
        // ===============================

        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Withdraw amount must be positive");
        }

        if (request.getExpenseDate() == null) {
            throw new BadRequestException("Expense date is required");
        }

        if (request.getPaidToName() == null ||
                request.getPaidToName().isBlank()) {
            throw new BadRequestException("Paid To name is required");
        }

        GroupInfo group = groupInfoRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found: " + groupId));

        GroupPool pool = groupPoolRepository
                .findByGroup_GroupId(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("BillBuddy pool not found for groupId: " + groupId));

        if (pool.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient BillBuddy balance");
        }

        // ===============================
        // 2️⃣ CREATE ORIGINAL EXPENSE
        // ===============================

        OriginalExpense expense = originalExpenseRepository.save(
                OriginalExpense.builder()
                        .group(group)
                        .paidByName("BillBuddy")
                        .paidToName(request.getPaidToName())
                        .amount(request.getAmount())
                        .expenseDate(request.getExpenseDate())
                        .description("BillBuddy Expense"
                                + (request.getDescription() != null
                                ? " - " + request.getDescription()
                                : ""))
                        .build()
        );

        // ===============================
        // 3️⃣ AUTO SPLIT AMONG MEMBERS
        // ===============================

        List<GroupMember> members =
                groupMemberRepository.findByGroup_GroupId(groupId);

        if (members.isEmpty()) {
            throw new BadRequestException("No members found in group");
        }

        BigDecimal splitAmount = request.getAmount()
                .divide(BigDecimal.valueOf(members.size()), 2, RoundingMode.HALF_UP);

        BigDecimal totalDebited = BigDecimal.ZERO;

        for (int i = 0; i < members.size(); i++) {

            GroupMember gm = members.get(i);
            BigDecimal debitAmount = splitAmount;

            // rounding correction on last member
            if (i == members.size() - 1) {
                debitAmount = request.getAmount().subtract(totalDebited);
            }

            memberTransactionRepository.save(
                    MemberTransaction.builder()
                            .group(group)
                            .member(gm.getMember())
                            .originalExpense(expense)
                            .direction(TransactionDirection.DEBIT)
                            .amount(debitAmount)
                            .reason(TransactionReason.BILLBUDDY_EXPENSE)
                            .counterpartyName("BillBuddy")
                            .build()
            );

            totalDebited = totalDebited.add(debitAmount);
        }

        // ===============================
        // 4️⃣ SAVE POOL TRANSACTION
        // ===============================

        poolTransactionRepository.save(
                PoolTransaction.builder()
                        .groupPool(pool)
                        .type(PoolTransactionType.WITHDRAW)
                        .amount(request.getAmount())
                        .paidToName(request.getPaidToName())
                        .description(request.getDescription())
                        .expenseDate(request.getExpenseDate())
                        .build()
        );

        // ===============================
        // 5️⃣ UPDATE POOL BALANCE
        // ===============================

        pool.setBalance(pool.getBalance().subtract(request.getAmount()));

        log.info("BillBuddy withdraw successful. New balance={}",
                pool.getBalance());
    }
}
