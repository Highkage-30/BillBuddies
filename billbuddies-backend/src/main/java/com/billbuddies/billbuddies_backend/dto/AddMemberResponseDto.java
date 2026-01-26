package com.billbuddies.billbuddies_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AddMemberResponseDto {
    private Long memberId;
    private String memberName;
    private String status;
}
