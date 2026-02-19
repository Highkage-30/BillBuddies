package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.service.SummaryReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class SummaryReportController {

    private final SummaryReportService summaryReportService;

    @GetMapping("/{groupId}/summary/download")
    public ResponseEntity<byte[]> downloadSummary(
            @PathVariable Long groupId
    ) {

        byte[] file = summaryReportService.generateReport(groupId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=group-summary.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }
}
