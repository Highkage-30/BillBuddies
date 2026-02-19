package com.billbuddies.billbuddies_backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddGroupMembersRequestDto {
    List<String> memberNames;
}
