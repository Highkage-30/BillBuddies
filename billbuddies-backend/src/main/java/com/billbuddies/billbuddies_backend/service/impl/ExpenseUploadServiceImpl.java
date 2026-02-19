package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.CreateExpenseRequestDto;
import com.billbuddies.billbuddies_backend.dto.ExpenseUploadRowDto;
import com.billbuddies.billbuddies_backend.dto.PoolDepositRequestDto;
import com.billbuddies.billbuddies_backend.dto.PoolWithdrawRequestDto;
import com.billbuddies.billbuddies_backend.exception.BadRequestException;
import com.billbuddies.billbuddies_backend.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseUploadServiceImpl implements ExpenseUploadService {

    private final ExpenseService expenseService;
    private final SettlementService settlementService;
    private final PoolDepositService poolDepositService;
    private final PoolWithdrawService poolWithdrawService;

    @Override
    @Transactional
    public void upload(Long groupId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new BadRequestException("Invalid file name");
        }

        List<ExpenseUploadRowDto> rows =
                filename.toLowerCase().endsWith(".csv")
                        ? parseCsv(file)
                        : parseExcel(file);

        if (rows.isEmpty()) {
            throw new BadRequestException("No expense rows found in file");
        }

        // 🔑 Convert & reuse existing API
        for (ExpenseUploadRowDto row : rows) {
                if ("BillBuddy".equalsIgnoreCase(row.getPaidByName()) &&
                        "BillBuddy".equalsIgnoreCase(row.getPaidToName())) {
                    throw new BadRequestException("BillBuddy cannot pay itself");
                }
                if("BillBuddy".equalsIgnoreCase(row.getPaidToName())) {
                    poolDepositService.deposit(groupId,
                            PoolDepositRequestDto.builder()
                                    .memberName(row.getPaidByName())
                                    .expenseDate(row.getExpenseDate())
                                    .amount(row.getAmount())
                                    .description(row.getDescription())
                                    .build()
                            );
                }
                if("BillBuddy".equalsIgnoreCase(row.getPaidByName())) {
                    poolWithdrawService.withdraw(groupId,
                            PoolWithdrawRequestDto.builder()
                                    .paidToName(row.getPaidToName())
                                    .expenseDate(row.getExpenseDate())
                                    .amount(row.getAmount())
                                    .description(row.getDescription())
                                    .build()
                            );
                }
                else{
                    expenseService.createExpense(groupId,
                            CreateExpenseRequestDto.builder()
                                    .amount(row.getAmount())
                                    .description(row.getDescription())
                                    .paidByName(row.getPaidByName())
                                    .paidToName(row.getPaidToName())
                                    .expenseDate(row.getExpenseDate())
                                    .build()
                            );
                }
        }

        settlementService.settleGroup(groupId);
    }

    // ================= CSV =================

    private List<ExpenseUploadRowDto> parseCsv(MultipartFile file) {
        try (
                Reader reader = new InputStreamReader(file.getInputStream());
                CSVParser parser = CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim()
                        .parse(reader)
        ) {

            validateHeaders(parser.getHeaderMap().keySet());

            List<ExpenseUploadRowDto> rows = new ArrayList<>();
            int rowNumber = 2;

            for (CSVRecord record : parser) {
                try {
                    rows.add(
                            ExpenseUploadRowDto.builder()
                                    .paidByName(record.get("paidByName"))
                                    .paidToName(record.get("paidToName"))
                                    .amount(new BigDecimal(record.get("amount")))
                                    .expenseDate(LocalDate.parse(record.get("expenseDate")))
                                    .description(
                                            record.isMapped("description")
                                                    ? record.get("description")
                                                    : null
                                    )
                                    .build()
                    );
                } catch (Exception e) {
                    throw new BadRequestException(
                            "Invalid data at row " + rowNumber + ": " + e.getMessage()
                    );
                }
                rowNumber++;
            }
            return rows;

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Invalid CSV format");
        }
    }

    // ================= EXCEL =================

    private List<ExpenseUploadRowDto> parseExcel(MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new BadRequestException("Excel file has no header row");
            }

            Map<String, Integer> headerIndex = new HashMap<>();
            for (Cell cell : headerRow) {
                headerIndex.put(
                        cell.getStringCellValue().trim().toLowerCase(),
                        cell.getColumnIndex()
                );
            }

            validateHeaders(headerIndex.keySet());

            List<ExpenseUploadRowDto> rows = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                int excelRow = i + 1;

                try {
                    rows.add(
                            ExpenseUploadRowDto.builder()
                                    .paidByName(getString(row, headerIndex.get("paidbyname")))
                                    .paidToName(getString(row, headerIndex.get("paidtoname")))
                                    .amount(getDecimal(row, headerIndex.get("amount")))
                                    .expenseDate(getDate(row, headerIndex.get("expensedate")))
                                    .description(
                                            headerIndex.containsKey("description")
                                                    ? getString(row, headerIndex.get("description"))
                                                    : null
                                    )
                                    .build()
                    );
                } catch (Exception e) {
                    throw new BadRequestException(
                            "Invalid data at row " + excelRow + ": " + e.getMessage()
                    );
                }
            }

            return rows;

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Invalid Excel format");
        }
    }

    // ================= HELPERS =================

    private void validateHeaders(Set<String> headers) {
        List<String> required = List.of(
                "paidbyname", "paidtoname", "amount", "expensedate"
        );
        for (String col : required) {
            if (!headers.contains(col)) {
                throw new BadRequestException("Missing required column: " + col);
            }
        }
    }

    private String getString(Row row, int index) {
        return row.getCell(index).getStringCellValue().trim();
    }

    private BigDecimal getDecimal(Row row, int index) {
        return BigDecimal.valueOf(row.getCell(index).getNumericCellValue());
    }

    private LocalDate getDate(Row row, int index) {
        return row.getCell(index)
                .getLocalDateTimeCellValue()
                .toLocalDate();
    }
}
