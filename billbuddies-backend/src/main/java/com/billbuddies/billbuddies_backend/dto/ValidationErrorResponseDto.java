package com.billbuddies.billbuddies_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ValidationErrorResponseDto {
    private int status;
    private Map<String, String> errors;
}
