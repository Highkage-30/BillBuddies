package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.AddGroupMembersRequestDto;
import com.billbuddies.billbuddies_backend.dto.GroupMemberResponseDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupMemberService {

    List<GroupMemberResponseDto> getGroupMembers(Long groupId);
    void addMembers(Long groupId, AddGroupMembersRequestDto request);
    void uploadGroupMembers(@PathVariable Long groupId, @RequestParam("file") MultipartFile file);
}
