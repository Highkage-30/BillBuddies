package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.SettlementPreviewItemDto;
import com.billbuddies.billbuddies_backend.dto.SettlementPreviewResponseDto;
import com.billbuddies.billbuddies_backend.entity.SplitExpense;
import com.billbuddies.billbuddies_backend.entity.Statement;
import com.billbuddies.billbuddies_backend.repository.FundsObligationRepository;
import com.billbuddies.billbuddies_backend.repository.GroupMemberRepository;
import com.billbuddies.billbuddies_backend.repository.SplitExpenseRepository;
import com.billbuddies.billbuddies_backend.repository.StatementRepository;
import com.billbuddies.billbuddies_backend.service.SettlementService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class SettlementServiceImpl implements SettlementService {

    private final SplitExpenseRepository splitExpenseRepository;
    private final FundsObligationRepository fundsObligationRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final StatementRepository statementRepository;

    @Value("${centralCounterParty.name}")
    private String CCP_NAME;

    @Override
    @Transactional
    public void settleGroup(Long groupId) {

        statementRepository.deleteByGroupId(groupId);

        Map<String, BigDecimal> paid = new HashMap<>();
        Map<String, BigDecimal> received = new HashMap<>();

        // init members + CCP
        groupMemberRepository
                .findByGroup_GroupIdOrderByMember_MemberNameAsc(groupId)
                .forEach(gm -> {
                    paid.put(gm.getMember().getMemberName(), BigDecimal.ZERO);
                    received.put(gm.getMember().getMemberName(), BigDecimal.ZERO);
                });

        // SPLIT_EXPENSE → PAID
        splitExpenseRepository.findByGroupId(groupId)
                .forEach(se ->
                        paid.merge(se.getFromName(), se.getAmount(), BigDecimal::add)
                );

        // FUNDS_OBLIGATION → RECEIVED VALUE
        fundsObligationRepository.findByGroupId(groupId)
                .forEach(fo ->
                        received.merge(
                                fo.getMember().getMemberName(),
                                fo.getShareAmount(),
                                BigDecimal::add
                        )
                );

        // BillBuddy received = money coming into CCP
        BigDecimal ccpIn =
                splitExpenseRepository.findByGroupId(groupId).stream()
                        .filter(se -> se.getToName().equalsIgnoreCase(CCP_NAME))
                        .map(SplitExpense::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ccpOut =
                splitExpenseRepository.findByGroupId(groupId).stream()
                        .filter(se -> se.getFromName().equalsIgnoreCase(CCP_NAME))
                        .map(SplitExpense::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        received.put(CCP_NAME, ccpIn);
        paid.put(CCP_NAME, ccpOut);

        // Persist statement snapshot
        for (String member : paid.keySet()) {

            BigDecimal credit = paid.getOrDefault(member, BigDecimal.ZERO);
            BigDecimal debit = received.getOrDefault(member, BigDecimal.ZERO);
            BigDecimal net = credit.subtract(debit);

            statementRepository.save(
                    Statement.builder()
                            .groupId(groupId)
                            .memberName(member)
                            .credit(credit)
                            .debit(debit)
                            .balance(net)
                            .settlementDate(LocalDateTime.now())
                            .build()
            );
        }
    }
    @Override
    @Transactional(readOnly = true)
    public SettlementPreviewResponseDto previewSettlement(Long groupId) {

        List<Statement> statements =
                statementRepository.findByGroupIdOrderByMemberNameAsc(groupId);

        List<BalanceHolder> creditors = new ArrayList<>();
        List<BalanceHolder> debtors = new ArrayList<>();

        /* 1️⃣ Split balances */
        for (Statement s : statements) {
            BigDecimal balance = s.getBalance();

            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(
                        new BalanceHolder(
                                s.getMemberName(),
                                balance
                        )
                );
            } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(
                        new BalanceHolder(
                                s.getMemberName(),
                                balance.abs()
                        )
                );
            }
        }

        List<SettlementPreviewItemDto> settlements = new ArrayList<>();

        /* 2️⃣ Two-pointer settlement matching */
        int i = 0;
        int j = 0;

        while (i < debtors.size() && j < creditors.size()) {

            BalanceHolder debtor = debtors.get(i);
            BalanceHolder creditor = creditors.get(j);

            BigDecimal amount =
                    debtor.amount.min(creditor.amount);

            settlements.add(
                    SettlementPreviewItemDto.builder()
                            .fromMemberName(debtor.memberName)
                            .toMemberName(creditor.memberName)
                            .amount(amount)
                            .build()
            );

            debtor.amount = debtor.amount.subtract(amount);
            creditor.amount = creditor.amount.subtract(amount);

            if (debtor.amount.compareTo(BigDecimal.ZERO) == 0) {
                i++;
            }
            if (creditor.amount.compareTo(BigDecimal.ZERO) == 0) {
                j++;
            }
        }

        return SettlementPreviewResponseDto.builder()
                .groupId(groupId)
                .generatedAt(LocalDateTime.now())
                .settlements(settlements)
                .build();
    }

    /* ===============================
       INTERNAL MUTABLE HOLDER
       =============================== */
    @Data
    @AllArgsConstructor
    private static class BalanceHolder {
        private String memberName;
        private BigDecimal amount;
    }


}
