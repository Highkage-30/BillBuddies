package com.billbuddies.billbuddies_backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface ExpenseUploadService {
    void upload(Long groupId, MultipartFile file);
}
