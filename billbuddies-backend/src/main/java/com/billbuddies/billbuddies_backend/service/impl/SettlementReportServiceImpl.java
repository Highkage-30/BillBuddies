package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.entity.*;
import com.billbuddies.billbuddies_backend.entity.enums.TransactionDirection;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.GroupInfoRepository;
import com.billbuddies.billbuddies_backend.repository.GroupPoolRepository;
import com.billbuddies.billbuddies_backend.repository.MemberTransactionRepository;
import com.billbuddies.billbuddies_backend.service.SettlementReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementReportServiceImpl implements SettlementReportService {

    private final GroupInfoRepository groupInfoRepository;
    private final MemberTransactionRepository memberTransactionRepository;
    private final GroupPoolRepository groupPoolRepository;

    @Override
    public byte[] generateSettlementReport(Long groupId) {

        GroupInfo group = groupInfoRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        // 🔥 1️⃣ Build balances from MemberTransaction (truth source)
        List<MemberTransaction> ledger =
                memberTransactionRepository.findByGroup_GroupId(groupId);

        Map<String, BigDecimal> balances = new HashMap<>();

        for (MemberTransaction tx : ledger) {

            String member = tx.getMember().getMemberName();
            balances.putIfAbsent(member, BigDecimal.ZERO);

            if (tx.getDirection() == TransactionDirection.CREDIT) {
                balances.put(member, balances.get(member).add(tx.getAmount()));
            } else {
                balances.put(member, balances.get(member).subtract(tx.getAmount()));
            }
        }

        // 🔥 Working copy for simulation
        Map<String, BigDecimal> workingBalances = new HashMap<>(balances);

        List<SettlementRow> memberRows = new ArrayList<>();
        List<SettlementRow> poolRows = new ArrayList<>();

        // ==========================================================
        // 2️⃣ SIMULATE MEMBER → MEMBER
        // ==========================================================

        List<Map.Entry<String, BigDecimal>> creditors = workingBalances.entrySet()
                .stream()
                .filter(e -> e.getValue().compareTo(BigDecimal.ZERO) > 0)
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .toList();

        List<Map.Entry<String, BigDecimal>> debtors = workingBalances.entrySet()
                .stream()
                .filter(e -> e.getValue().compareTo(BigDecimal.ZERO) < 0)
                .sorted(Comparator.comparing(e -> e.getValue().abs()))
                .toList();

        int i = 0, j = 0;

        while (i < debtors.size() && j < creditors.size()) {

            String debtor = debtors.get(i).getKey();
            String creditor = creditors.get(j).getKey();

            BigDecimal debtorAmount = workingBalances.get(debtor).abs();
            BigDecimal creditorAmount = workingBalances.get(creditor);

            BigDecimal settleAmount = debtorAmount.min(creditorAmount);

            memberRows.add(new SettlementRow(debtor, creditor, settleAmount));

            workingBalances.put(debtor,
                    workingBalances.get(debtor).add(settleAmount));

            workingBalances.put(creditor,
                    workingBalances.get(creditor).subtract(settleAmount));

            if (workingBalances.get(debtor).compareTo(BigDecimal.ZERO) == 0) i++;
            if (workingBalances.get(creditor).compareTo(BigDecimal.ZERO) == 0) j++;
        }

        // ==========================================================
        // 3️⃣ SIMULATE BILLBUDDY → MEMBER
        // ==========================================================

        GroupPool pool = groupPoolRepository
                .findByGroup_GroupId(groupId)
                .orElse(null);

        BigDecimal poolBalance =
                pool != null ? pool.getBalance() : BigDecimal.ZERO;

        if (poolBalance.compareTo(BigDecimal.ZERO) > 0) {

            for (Map.Entry<String, BigDecimal> entry : workingBalances.entrySet()) {

                if (poolBalance.compareTo(BigDecimal.ZERO) <= 0) break;

                if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {

                    BigDecimal payout = entry.getValue().min(poolBalance);

                    poolRows.add(
                            new SettlementRow("BillBuddy",
                                    entry.getKey(),
                                    payout)
                    );

                    workingBalances.put(entry.getKey(),
                            entry.getValue().subtract(payout));

                    poolBalance = poolBalance.subtract(payout);
                }
            }
        }
        log.info("Member rows:{}\nPool rows:{}", memberRows, poolRows);

        // ==========================================================
        // 4️⃣ GENERATE EXCEL
        // ==========================================================

        try (Workbook workbook = new XSSFWorkbook()) {

            createMemberSheet(workbook, memberRows);
            createPoolSheet(workbook, poolRows);
//            createFinalSheet(workbook, workingBalances);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating report");
        }
    }

    // ==========================================================
    // SHEET BUILDERS
    // ==========================================================

    private void createMemberSheet(Workbook wb, List<SettlementRow> rows) {

        Sheet sheet = wb.createSheet("Member Settlements");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("From");
        header.createCell(1).setCellValue("To");
        header.createCell(2).setCellValue("Amount");

        int rowNum = 1;

        for (SettlementRow row : rows) {
            Row r = sheet.createRow(rowNum++);
            r.createCell(0).setCellValue(row.from);
            r.createCell(1).setCellValue(row.to);
            r.createCell(2).setCellValue(row.amount.doubleValue());
        }
    }

    private void createPoolSheet(Workbook wb, List<SettlementRow> rows) {

        Sheet sheet = wb.createSheet("BillBuddy Settlements");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("From");
        header.createCell(1).setCellValue("To");
        header.createCell(2).setCellValue("Amount");

        int rowNum = 1;

        for (SettlementRow row : rows) {
            Row r = sheet.createRow(rowNum++);
            r.createCell(0).setCellValue(row.from);
            r.createCell(1).setCellValue(row.to);
            r.createCell(2).setCellValue(row.amount.doubleValue());
        }
    }

    private void createFinalSheet(Workbook wb, Map<String, BigDecimal> balances) {

        Sheet sheet = wb.createSheet("Final Net");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Member");
        header.createCell(1).setCellValue("Final Balance");

        int rowNum = 1;

        for (Map.Entry<String, BigDecimal> entry : balances.entrySet()) {
            Row r = sheet.createRow(rowNum++);
            r.createCell(0).setCellValue(entry.getKey());
            r.createCell(1).setCellValue(entry.getValue().doubleValue());
        }
    }

    // ==========================================================
    // HELPER CLASS
    // ==========================================================

    private static class SettlementRow {
        String from;
        String to;
        BigDecimal amount;

        SettlementRow(String from, String to, BigDecimal amount) {
            this.from = from;
            this.to = to;
            this.amount = amount;
        }
    }
}
