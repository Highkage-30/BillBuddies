package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.CreateGroupRequestDto;
import com.billbuddies.billbuddies_backend.dto.GroupResponseDto;

import java.util.List;

public interface GroupService {

    GroupResponseDto createGroup(CreateGroupRequestDto request);
    List<GroupResponseDto> getAllGroups();
    void deleteGroup(Long groupId);

}
