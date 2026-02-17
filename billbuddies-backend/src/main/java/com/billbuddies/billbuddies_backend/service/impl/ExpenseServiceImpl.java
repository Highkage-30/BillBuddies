package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.CreateExpenseRequestDto;
import com.billbuddies.billbuddies_backend.dto.CreateExpenseResponseDto;
import com.billbuddies.billbuddies_backend.dto.OriginalExpenseResponseDto;
import com.billbuddies.billbuddies_backend.entity.GroupInfo;
import com.billbuddies.billbuddies_backend.entity.GroupMember;
import com.billbuddies.billbuddies_backend.entity.GroupMemberId;
import com.billbuddies.billbuddies_backend.entity.Member;
import com.billbuddies.billbuddies_backend.entity.OriginalExpense;
import com.billbuddies.billbuddies_backend.entity.MemberTransaction;
import com.billbuddies.billbuddies_backend.entity.enums.TransactionDirection;
import com.billbuddies.billbuddies_backend.entity.enums.TransactionReason;
import com.billbuddies.billbuddies_backend.exception.BadRequestException;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.GroupInfoRepository;
import com.billbuddies.billbuddies_backend.repository.GroupMemberRepository;
import com.billbuddies.billbuddies_backend.repository.MemberRepository;
import com.billbuddies.billbuddies_backend.repository.MemberTransactionRepository;
import com.billbuddies.billbuddies_backend.repository.OriginalExpenseRepository;
import com.billbuddies.billbuddies_backend.service.ExpenseService;
import com.billbuddies.billbuddies_backend.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {

    private final GroupInfoRepository groupInfoRepository;
    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final OriginalExpenseRepository originalExpenseRepository;
    private final MemberTransactionRepository memberTransactionRepository;
    private final SettlementService settlementService;

    // =====================================================
    // POST /api/v1/groups/{groupId}/expenses
    // =====================================================
    @Override
    @Transactional
    public CreateExpenseResponseDto createExpense(
            Long groupId,
            CreateExpenseRequestDto request
    ) {

        log.info("Creating expense for groupId={} ,groupDescription= {}", groupId,request.getDescription());
        validateRequest(request);

        GroupInfo group = groupInfoRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found with id: " + groupId));

        Member paidBy = memberRepository
                .findByMemberNameIgnoreCase(request.getPaidByName())
                .orElseThrow(() ->
                        new BadRequestException("PaidBy member does not exist:"+request.getPaidByName()));

        validateMemberInGroup(groupId, paidBy);

        // 1️⃣ Save original expense (always)
        OriginalExpense expense = originalExpenseRepository.save(
                OriginalExpense.builder()
                        .group(group)
                        .paidByName(request.getPaidByName())
                        .paidToName(request.getPaidToName())
                        .amount(request.getAmount())
                        .expenseDate(request.getExpenseDate())
                        .description(request.getDescription())
                        .build()
        );

        // 2️⃣ Detect paidTo type
        Member paidToMember = memberRepository
                .findByMemberNameIgnoreCase(request.getPaidToName())
                .filter(m -> isMemberInGroup(groupId, m))
                .orElse(null);

        if (paidToMember != null) {
            // ===============================
            // CASE 1: MEMBER → MEMBER
            // ===============================
            log.info("Processing member-to-member expense");

            saveTransaction(
                    group,
                    paidBy,
                    expense,
                    TransactionDirection.CREDIT,
                    request.getAmount(),
                    TransactionReason.PAID,
                    paidToMember.getMemberName()
            );

            saveTransaction(
                    group,
                    paidToMember,
                    expense,
                    TransactionDirection.DEBIT,
                    request.getAmount(),
                    TransactionReason.RECEIVED,
                    paidBy.getMemberName()
            );

        } else {
            // ===============================
            // CASE 2: MEMBER → THIRD PARTY
            // ===============================
            log.info("Processing member-to-third-party expense");

            List<GroupMember> groupMembers =
                    groupMemberRepository.findByGroup_GroupId(groupId);

            BigDecimal splitAmount = request.getAmount()
                    .divide(
                            BigDecimal.valueOf(groupMembers.size()),
                            2,
                            RoundingMode.HALF_UP
                    );

            // CREDIT: full amount to payer
            saveTransaction(
                    group,
                    paidBy,
                    expense,
                    TransactionDirection.CREDIT,
                    request.getAmount(),
                    TransactionReason.PAID,
                    request.getPaidToName()
            );

            // DEBIT: split with rounding correction
            BigDecimal totalDebited = BigDecimal.ZERO;

            for (int i = 0; i < groupMembers.size(); i++) {

                GroupMember gm = groupMembers.get(i);
                BigDecimal debitAmount = splitAmount;

                // Adjust last member for rounding difference
                if (i == groupMembers.size() - 1) {
                    debitAmount = request.getAmount().subtract(totalDebited);
                }

                saveTransaction(
                        group,
                        gm.getMember(),
                        expense,
                        TransactionDirection.DEBIT,
                        debitAmount,
                        TransactionReason.CONSUMED,
                        request.getPaidToName()
                );

                totalDebited = totalDebited.add(debitAmount);
            }
        }

        log.info("Expense created successfully. originalExpenseId={}",
                expense.getOriginalExpenseId());

        return CreateExpenseResponseDto.builder()
                .originalExpenseId(expense.getOriginalExpenseId())
                .status("CREATED")
                .build();
    }

    // =====================================================
    // GET /api/v1/groups/{groupId}/expenses
    // =====================================================
    @Override
    public List<OriginalExpenseResponseDto> getGroupExpenses(Long groupId) {

        if (!groupInfoRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }

        return originalExpenseRepository
                .findByGroup_GroupIdOrderByExpenseDateDesc(groupId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =====================================================
    // VALIDATIONS
    // =====================================================
    private void validateRequest(CreateExpenseRequestDto request) {

        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }

        if (request.getPaidByName() == null ||
                request.getPaidByName().isBlank()) {
            throw new BadRequestException("PaidBy name is required");
        }

        if (request.getPaidToName() == null ||
                request.getPaidToName().isBlank()) {
            throw new BadRequestException("PaidTo name is required");
        }

        if (request.getPaidByName()
                .equalsIgnoreCase(request.getPaidToName())) {
            throw new BadRequestException("PaidBy and PaidTo cannot be the same member");
        }

        if (request.getExpenseDate() == null) {
            throw new BadRequestException("Expense date is required");
        }
    }

    private void validateMemberInGroup(Long groupId, Member member) {
        if (!isMemberInGroup(groupId, member)) {
            throw new BadRequestException("Member is not part of the group");
        }
    }

    private boolean isMemberInGroup(Long groupId, Member member) {
        return groupMemberRepository.existsById(
                new GroupMemberId(groupId, member.getMemberId()));
    }

    // =====================================================
    // HELPERS
    // =====================================================
    private void saveTransaction(
            GroupInfo group,
            Member member,
            OriginalExpense expense,
            TransactionDirection direction,
            BigDecimal amount,
            TransactionReason reason,
            String counterparty
    ) {
        memberTransactionRepository.save(
                MemberTransaction.builder()
                        .group(group)
                        .member(member)
                        .originalExpense(expense)
                        .direction(direction)
                        .amount(amount)
                        .reason(reason)
                        .counterpartyName(counterparty)
                        .build()
        );
    }

    private OriginalExpenseResponseDto toDto(OriginalExpense expense) {
        return OriginalExpenseResponseDto.builder()
                .originalExpenseId(expense.getOriginalExpenseId())
                .paidByName(expense.getPaidByName())
                .paidToName(expense.getPaidToName())
                .amount(expense.getAmount())
                .expenseDate(expense.getExpenseDate())
                .description(expense.getDescription())
                .createdAt(expense.getCreatedAt())
                .build();
    }
    @Override
    @Transactional
    public void deleteExpense(Long groupId, Long expenseId) {

        log.info("Deleting expenseId={} from groupId={}", expenseId, groupId);

        OriginalExpense expense = originalExpenseRepository.findById(expenseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Expense not found: " + expenseId));

        if (!expense.getGroup().getGroupId().equals(groupId)) {
            throw new BadRequestException("Expense does not belong to the given group");
        }
        if ("BillBuddy".equalsIgnoreCase(expense.getPaidByName())) {
            throw new BadRequestException("BillBuddy linked expenses cannot be deleted");
        }

        // 1️⃣ Remove derived ledger entries
        memberTransactionRepository
                .deleteByOriginalExpense_OriginalExpenseId(expenseId);

        // 2️⃣ Remove the source expense
        originalExpenseRepository.delete(expense);

        // 3️⃣ Recompute statement snapshot
        settlementService.settleGroup(groupId);

        log.info("ExpenseId={} deleted and balances recomputed", expenseId);
    }
}
