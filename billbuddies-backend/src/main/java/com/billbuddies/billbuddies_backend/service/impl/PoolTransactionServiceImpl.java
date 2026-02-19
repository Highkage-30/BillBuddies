package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.GroupPoolTransactionsResponseDto;
import com.billbuddies.billbuddies_backend.dto.PoolTransactionDto;
import com.billbuddies.billbuddies_backend.entity.GroupInfo;
import com.billbuddies.billbuddies_backend.entity.GroupPool;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.GroupInfoRepository;
import com.billbuddies.billbuddies_backend.repository.GroupPoolRepository;
import com.billbuddies.billbuddies_backend.repository.PoolTransactionRepository;
import com.billbuddies.billbuddies_backend.service.PoolTransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoolTransactionServiceImpl implements PoolTransactionService {

    private final GroupInfoRepository groupInfoRepository;
    private final GroupPoolRepository groupPoolRepository;
    private final PoolTransactionRepository poolTransactionRepository;

    @Override
    @Transactional
    public GroupPoolTransactionsResponseDto getPoolTransactions(Long groupId) {

        log.info("Fetching pool transactions for groupId={}", groupId);

        GroupInfo group = groupInfoRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found: " + groupId));

        // 🔥 Auto-create pool if missing
        GroupPool pool = groupPoolRepository
                .findByGroup_GroupId(groupId)
                .orElseGet(() -> {
                    log.info("Pool not found. Auto-creating pool for groupId={}", groupId);
                    return groupPoolRepository.save(
                            GroupPool.builder()
                                    .group(group)
                                    .balance(BigDecimal.ZERO)
                                    .build()
                    );
                });

        List<PoolTransactionDto> transactions =
                poolTransactionRepository
                        .findByGroupPool_PoolIdOrderByCreatedAtDesc(pool.getPoolId())
                        .stream()
                        .map(tx -> PoolTransactionDto.builder()
                                .transactionId(tx.getPoolTransactionId())
                                .memberId(
                                        tx.getMember() != null
                                                ? tx.getMember().getMemberId()
                                                : 0
                                )
                                .memberName(
                                        tx.getMember() != null
                                                ? tx.getMember().getMemberName()
                                                : "BillBuddy"
                                )
                                .type(tx.getType().name())
                                .amount(tx.getAmount())
                                .description(tx.getDescription())
                                .paidToName(tx.getPaidToName())
                                .expenseDate(tx.getExpenseDate())  // 🔥 added
                                .createdAt(tx.getCreatedAt())
                                .build()
                        )
                        .toList();

        return GroupPoolTransactionsResponseDto.builder()
                .groupId(groupId)
                .poolId(pool.getPoolId())
                .transactions(transactions)
                .build();
    }
}
