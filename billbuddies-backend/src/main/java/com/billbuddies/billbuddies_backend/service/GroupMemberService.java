package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.AddMemberResponseDto;
import com.billbuddies.billbuddies_backend.dto.GroupMemberResponseDto;

import java.util.List;

public interface GroupMemberService {

    List<GroupMemberResponseDto> getMembersByGroupId(Long groupId);

    AddMemberResponseDto addMemberToGroup(Long groupId, String memberName);

}
