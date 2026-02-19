package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.SettlementPreviewResponseDto;
import com.billbuddies.billbuddies_backend.dto.SettlementRowDto;
import com.billbuddies.billbuddies_backend.entity.Statement;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.GroupInfoRepository;
import com.billbuddies.billbuddies_backend.repository.StatementRepository;
import com.billbuddies.billbuddies_backend.service.SettlementPreviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementPreviewServiceImpl implements SettlementPreviewService {

    private final GroupInfoRepository groupInfoRepository;
    private final StatementRepository statementRepository;

    @Override
    public SettlementPreviewResponseDto previewSettlement(Long groupId) {

        log.info("Generating settlement preview for groupId={}", groupId);

        if (!groupInfoRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }
        String groupName= groupInfoRepository.findById(groupId).get().getGroupName();

        List<Statement> statements =
                statementRepository.findByGroup_GroupId(groupId);

        if (statements.isEmpty()) {
            return SettlementPreviewResponseDto.builder()
                    .groupId(groupId)
                    .groupName(groupName)
                    .generatedAt(LocalDateTime.now())
                    .settlements(List.of())
                    .build();
        }

        // Separate debtors and creditors
        List<Statement> creditors = new ArrayList<>();
        List<Statement> debtors = new ArrayList<>();

        for (Statement s : statements) {
            if (s.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(s);
            } else if (s.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(s);
            }
        }

        // Sort for better UX (largest first)
        creditors.sort(Comparator.comparing(Statement::getBalance).reversed());
        debtors.sort(
                Comparator.comparing(
                        (Statement s) -> s.getBalance().abs()
                ).reversed()
        );
        List<SettlementRowDto> rows = new ArrayList<>();

        int i = 0, j = 0;

        while (i < debtors.size() && j < creditors.size()) {

            Statement debtor = debtors.get(i);
            Statement creditor = creditors.get(j);

            BigDecimal debtorAmount = debtor.getBalance().abs();
            BigDecimal creditorAmount = creditor.getBalance();

            BigDecimal settleAmount = debtorAmount.min(creditorAmount);

            rows.add(SettlementRowDto.builder()
                    .fromMemberId(debtor.getMember().getMemberId())
                    .fromMemberName(debtor.getMember().getMemberName())
                    .toMemberId(creditor.getMember().getMemberId())
                    .toMemberName(creditor.getMember().getMemberName())
                    .amount(settleAmount)
                    .build()
            );

            // Update balances in-memory
            debtor.setBalance(debtor.getBalance().add(settleAmount));      // toward zero
            creditor.setBalance(creditor.getBalance().subtract(settleAmount));

            if (debtor.getBalance().compareTo(BigDecimal.ZERO) == 0) i++;
            if (creditor.getBalance().compareTo(BigDecimal.ZERO) == 0) j++;
        }

        return SettlementPreviewResponseDto.builder()
                .groupId(groupId)
                .groupName(groupName)
                .generatedAt(LocalDateTime.now())
                .settlements(rows)
                .build();
    }
}
