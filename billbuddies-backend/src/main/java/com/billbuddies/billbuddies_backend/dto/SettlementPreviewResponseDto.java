package com.billbuddies.billbuddies_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SettlementPreviewResponseDto {

    private Long groupId;
    private String groupName;
    private LocalDateTime generatedAt;
    private List<SettlementRowDto> settlements;
}
