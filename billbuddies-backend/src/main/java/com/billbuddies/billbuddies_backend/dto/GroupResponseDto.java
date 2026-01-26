package com.billbuddies.billbuddies_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GroupResponseDto {
    private Long groupId;
    private String groupName;
    private String groupDescription;
    private LocalDateTime createdAt;
}
