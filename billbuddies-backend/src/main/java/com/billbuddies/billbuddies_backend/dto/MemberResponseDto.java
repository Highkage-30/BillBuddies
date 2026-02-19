package com.billbuddies.billbuddies_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponseDto {

    private Long memberId;
    private String memberName;
}
