package com.billbuddies.billbuddies_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddMemberRequestDto {
    @NotBlank(message = "Member name cannot be empty")
    private String memberName;
}
