package com.billbuddies.billbuddies_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateGroupResponseDto {
    private Long groupId;
    private String groupName;
    private String status; // CREATED
}