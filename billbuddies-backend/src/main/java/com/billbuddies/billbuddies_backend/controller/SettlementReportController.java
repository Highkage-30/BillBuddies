package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.service.SettlementReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class SettlementReportController {

    private final SettlementReportService settlementReportService;

    @GetMapping("/{groupId}/settlement/report")
    public ResponseEntity<ByteArrayResource> downloadSettlementReport(
            @PathVariable Long groupId
    ) {

        byte[] fileData = settlementReportService.generateSettlementReport(groupId);

        ByteArrayResource resource = new ByteArrayResource(fileData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Settlement_Report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(fileData.length)
                .body(resource);
    }
}
