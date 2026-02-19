package com.billbuddies.billbuddies_backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponseDto {

    private Long groupId;
    private String groupName;
    private String groupDescription;
    private LocalDateTime createdAt;
}
