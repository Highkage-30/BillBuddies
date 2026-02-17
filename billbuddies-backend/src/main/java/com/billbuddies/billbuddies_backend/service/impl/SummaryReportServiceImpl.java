package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.entity.*;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.*;
import com.billbuddies.billbuddies_backend.service.SummaryReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SummaryReportServiceImpl implements SummaryReportService {

    private final GroupInfoRepository groupInfoRepository;
    private final StatementRepository statementRepository;
    private final OriginalExpenseRepository originalExpenseRepository;
    private final GroupPoolRepository groupPoolRepository;
    private final PoolTransactionRepository poolTransactionRepository;

    @Override
    public byte[] generateReport(Long groupId) {

        GroupInfo group = groupInfoRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found"));

        List<Statement> statements =
                statementRepository.findByGroup_GroupId(groupId);

        List<OriginalExpense> expenses =
                originalExpenseRepository.findByGroup_GroupIdOrderByExpenseDateDesc(groupId);

        GroupPool pool = groupPoolRepository
                .findByGroup_GroupId(groupId)
                .orElse(null);

        List<PoolTransaction> poolTransactions =
                pool != null
                        ? poolTransactionRepository
                        .findByGroupPool_PoolIdOrderByCreatedAtDesc(pool.getPoolId())
                        : List.of();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            createOverviewSheet(workbook, group, expenses, pool);
            createMemberSummarySheet(workbook, statements);
            createExpensesSheet(workbook, expenses);
            createPoolSheet(workbook, poolTransactions);

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate summary report");
        }
    }

    // ============================================================
    // SHEET 1 — GROUP OVERVIEW
    // ============================================================

    private void createOverviewSheet(
            Workbook workbook,
            GroupInfo group,
            List<OriginalExpense> expenses,
            GroupPool pool
    ) {

        Sheet sheet = workbook.createSheet("Overview");

        BigDecimal totalExpenses = expenses.stream()
                .map(OriginalExpense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal poolBalance =
                pool != null ? pool.getBalance() : BigDecimal.ZERO;

        Object[][] data = {
                {"Group Name", group.getGroupName()},
                {"Description", group.getDescription()},
                {"Total Expenses", totalExpenses},
                {"BillBuddy Balance", poolBalance},
                {"Generated At", java.time.LocalDateTime.now()}
        };

        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(data[i][0].toString());
            row.createCell(1).setCellValue(data[i][1].toString());
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    // ============================================================
    // SHEET 2 — MEMBER SUMMARY
    // ============================================================

    private void createMemberSummarySheet(
            Workbook workbook,
            List<Statement> statements
    ) {

        Sheet sheet = workbook.createSheet("Member Summary");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Member");
        header.createCell(1).setCellValue("Credit");
        header.createCell(2).setCellValue("Debit");
        header.createCell(3).setCellValue("Balance");

        int rowNum = 1;

        for (Statement s : statements) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(
                    s.getMember().getMemberName());

            row.createCell(1).setCellValue(
                    s.getCredit().doubleValue());

            row.createCell(2).setCellValue(
                    s.getDebit().doubleValue());

            row.createCell(3).setCellValue(
                    s.getBalance().doubleValue());
        }

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ============================================================
    // SHEET 3 — ALL EXPENSES
    // ============================================================

    private void createExpensesSheet(
            Workbook workbook,
            List<OriginalExpense> expenses
    ) {

        Sheet sheet = workbook.createSheet("Expenses");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Date");
        header.createCell(1).setCellValue("Paid By");
        header.createCell(2).setCellValue("Paid To");
        header.createCell(3).setCellValue("Amount");
        header.createCell(4).setCellValue("Description");

        int rowNum = 1;

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (OriginalExpense expense : expenses) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(
                    expense.getExpenseDate().format(formatter));

            row.createCell(1).setCellValue(
                    expense.getPaidByName());

            row.createCell(2).setCellValue(
                    expense.getPaidToName());

            row.createCell(3).setCellValue(
                    expense.getAmount().doubleValue());

            row.createCell(4).setCellValue(
                    expense.getDescription());
        }

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ============================================================
    // SHEET 4 — BILLBUDDY TRANSACTIONS
    // ============================================================

    private void createPoolSheet(
            Workbook workbook,
            List<PoolTransaction> transactions
    ) {

        Sheet sheet = workbook.createSheet("BillBuddy");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Date");
        header.createCell(1).setCellValue("Type");
        header.createCell(2).setCellValue("Member");
        header.createCell(3).setCellValue("Amount");
        header.createCell(4).setCellValue("Description");

        int rowNum = 1;

        for (PoolTransaction tx : transactions) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(
                    tx.getExpenseDate() != null
                            ? tx.getExpenseDate().toString()
                            : tx.getCreatedAt().toString());

            row.createCell(1).setCellValue(
                    tx.getType().name());

            row.createCell(2).setCellValue(
                    tx.getMember() != null
                            ? tx.getMember().getMemberName()
                            : "—");

            row.createCell(3).setCellValue(
                    tx.getAmount().doubleValue());

            row.createCell(4).setCellValue(
                    tx.getDescription());
        }

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
