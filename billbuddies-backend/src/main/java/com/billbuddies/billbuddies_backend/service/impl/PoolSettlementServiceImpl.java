package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.entity.*;
import com.billbuddies.billbuddies_backend.entity.enums.PoolTransactionType;
import com.billbuddies.billbuddies_backend.entity.enums.TransactionDirection;
import com.billbuddies.billbuddies_backend.entity.enums.TransactionReason;
import com.billbuddies.billbuddies_backend.exception.BadRequestException;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.*;
import com.billbuddies.billbuddies_backend.service.PoolSettlementService;
import com.billbuddies.billbuddies_backend.service.SettlementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoolSettlementServiceImpl implements PoolSettlementService {

    private final GroupInfoRepository groupInfoRepository;
    private final GroupPoolRepository groupPoolRepository;
    private final StatementRepository statementRepository;
    private final OriginalExpenseRepository originalExpenseRepository;
    private final MemberTransactionRepository memberTransactionRepository;
    private final PoolTransactionRepository poolTransactionRepository;
    private final SettlementService settlementService;

    @Override
    @Transactional
    public void settle(Long groupId) {

        log.info("Starting BillBuddy pool settlement for groupId={}", groupId);

        GroupInfo group = groupInfoRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        GroupPool pool = groupPoolRepository.findByGroup_GroupId(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("BillBuddy pool not found"));

        BigDecimal poolBalance = pool.getBalance();

        if (poolBalance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("No BillBuddy balance to distribute");
        }

        // 🔥 Get members with positive balance
        List<Statement> statements =
                statementRepository.findByGroup_GroupId(groupId);

        List<Statement> positiveMembers = statements.stream()
                .filter(s -> s.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(Statement::getBalance).reversed())
                .toList();

        List<Statement> negativeMembers = statements.stream()
                .filter(s -> s.getBalance().compareTo(BigDecimal.ZERO) < 0)
                .toList();
        if (!negativeMembers.isEmpty()) {
            throw new BadRequestException("Cannot distribute BillBuddy balance while some members still owe money");
        }
        if (positiveMembers.isEmpty()) {
            throw new BadRequestException("No members eligible for BillBuddy settlement");
        }

        for (Statement statement : positiveMembers) {

            if (poolBalance.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal memberBalance = statement.getBalance();

            BigDecimal payout = memberBalance.min(poolBalance);

            // 1️⃣ Create original expense
            OriginalExpense expense = originalExpenseRepository.save(
                    OriginalExpense.builder()
                            .group(group)
                            .paidByName("BillBuddy")
                            .paidToName(statement.getMember().getMemberName())
                            .amount(payout)
                            .expenseDate(LocalDate.now())
                            .description("BillBuddy Settlement")
                            .build()
            );

            // 2️⃣ Member ledger entry (CREDIT)
            memberTransactionRepository.save(
                    MemberTransaction.builder()
                            .group(group)
                            .member(statement.getMember())
                            .originalExpense(expense)
                            .direction(TransactionDirection.DEBIT)
                            .amount(payout)
                            .reason(TransactionReason.BILLBUDDY_SETTLEMENT)
                            .counterpartyName("BillBuddy")
                            .build()
            );

            // 3️⃣ Pool transaction entry
            poolTransactionRepository.save(
                    PoolTransaction.builder()
                            .groupPool(pool)
                            .member(statement.getMember())
                            .type(PoolTransactionType.WITHDRAW)
                            .amount(payout)
                            .description("Settlement to " + statement.getMember().getMemberName())
                            .expenseDate(LocalDate.now())
                            .build()
            );

            poolBalance = poolBalance.subtract(payout);
        }

        // 4️⃣ Update pool balance
        pool.setBalance(poolBalance);

        // 5️⃣ Recalculate statements
        settlementService.settleGroup(groupId);

        log.info("BillBuddy settlement completed. Remaining pool balance={}", poolBalance);
    }
}
