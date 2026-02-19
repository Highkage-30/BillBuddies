package com.billbuddies.billbuddies_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberResponseDto {

    private Long memberId;
    private String memberName;
}
