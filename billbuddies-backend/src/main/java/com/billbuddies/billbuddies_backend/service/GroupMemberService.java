package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.AddGroupMembersRequestDto;
import com.billbuddies.billbuddies_backend.dto.GroupMemberResponseDto;

import java.util.List;

public interface GroupMemberService {

    List<GroupMemberResponseDto> getGroupMembers(Long groupId);
    void addMembers(Long groupId, AddGroupMembersRequestDto request);

}
