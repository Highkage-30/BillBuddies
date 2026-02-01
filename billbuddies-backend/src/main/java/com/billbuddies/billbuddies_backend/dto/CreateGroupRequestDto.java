package com.billbuddies.billbuddies_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateGroupRequestDto {
    @NotBlank(message = "Group name cannot be empty")
    private String groupName;
    private String groupDescription;
    private List<String> memberList;
}
