package com.billbuddies.billbuddies_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MemberStatementResponseDto {
    private Long memberId;
    private String memberName;
    private List<MemberGroupStatementDto> groups;
}