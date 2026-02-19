package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.entity.*;
import com.billbuddies.billbuddies_backend.entity.enums.TransactionDirection;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.*;
import com.billbuddies.billbuddies_backend.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementServiceImpl implements SettlementService {

    private final GroupInfoRepository groupInfoRepository;
    private final MemberTransactionRepository memberTransactionRepository;
    private final StatementRepository statementRepository;

    @Override
    @Transactional
    public void settleGroup(Long groupId) {

        log.info("Running settlement for groupId={}", groupId);

        GroupInfo group = groupInfoRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found with id: " + groupId));

        // 1️⃣ Fetch ledger
        List<MemberTransaction> transactions =
                memberTransactionRepository.findByGroup_GroupId(groupId);

        if (transactions.isEmpty()) {
            log.info("No transactions found for groupId={}, clearing statements", groupId);
            statementRepository.deleteByGroup_GroupId(groupId);
            return;
        }

        // 2️⃣ Aggregate credit / debit per member
        Map<Long, BigDecimal> creditMap = new HashMap<>();
        Map<Long, BigDecimal> debitMap = new HashMap<>();

        for (MemberTransaction tx : transactions) {

            Long memberId = tx.getMember().getMemberId();
            BigDecimal amount = tx.getAmount();

            if (tx.getDirection() == TransactionDirection.CREDIT) {
                creditMap.put(
                        memberId,
                        creditMap.getOrDefault(memberId, BigDecimal.ZERO).add(amount)
                );
            } else {
                debitMap.put(
                        memberId,
                        debitMap.getOrDefault(memberId, BigDecimal.ZERO).add(amount)
                );
            }
        }

        // 3️⃣ Overwrite statement table
        statementRepository.deleteByGroup_GroupId(groupId);

        LocalDateTime now = LocalDateTime.now();

        for (Long memberId : unionKeys(creditMap, debitMap)) {

            BigDecimal credit = creditMap.getOrDefault(memberId, BigDecimal.ZERO);
            BigDecimal debit  = debitMap.getOrDefault(memberId, BigDecimal.ZERO);
            BigDecimal balance = credit.subtract(debit);

            Member member = transactions.stream()
                    .map(MemberTransaction::getMember)
                    .filter(m -> m.getMemberId().equals(memberId))
                    .findFirst()
                    .orElseThrow(); // logically impossible

            Statement statement = Statement.builder()
                    .id(new StatementId(groupId, memberId))
                    .group(group)
                    .member(member)
                    .credit(credit)
                    .debit(debit)
                    .balance(balance)
                    .generatedAt(now)
                    .build();

            statementRepository.save(statement);
        }

        log.info("Settlement completed for groupId={}", groupId);
    }

    private Set<Long> unionKeys(
            Map<Long, BigDecimal> creditMap,
            Map<Long, BigDecimal> debitMap
    ) {
        Set<Long> keys = new HashSet<>(creditMap.keySet());
        keys.addAll(debitMap.keySet());
        return keys;
    }

}
