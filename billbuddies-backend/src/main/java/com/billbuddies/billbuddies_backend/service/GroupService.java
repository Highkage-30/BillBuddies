package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.CreateGroupRequestDto;
import com.billbuddies.billbuddies_backend.dto.CreateGroupResponseDto;
import com.billbuddies.billbuddies_backend.dto.GroupResponseDto;

import java.util.List;

public interface GroupService {
    List<GroupResponseDto> getAllGroups();
    CreateGroupResponseDto createGroup(CreateGroupRequestDto request);
}
