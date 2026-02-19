package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.service.ExpenseUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/expenses")
@RequiredArgsConstructor
@Slf4j
public class ExpenseUploadController {

    private final ExpenseUploadService expenseUploadService;

    @PostMapping("/upload")
    public void uploadExpenses(
            @PathVariable Long groupId,
            @RequestParam("file") MultipartFile file
    ) {
        log.info("Uploading expense file for groupId={}", groupId);
        expenseUploadService.upload(groupId, file);
    }
}
