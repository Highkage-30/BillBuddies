package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.AddGroupMembersRequestDto;
import com.billbuddies.billbuddies_backend.dto.GroupMemberResponseDto;
import com.billbuddies.billbuddies_backend.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/members")
@RequiredArgsConstructor
@Slf4j
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    @GetMapping
    public List<GroupMemberResponseDto> getGroupMembers(
            @PathVariable Long groupId
    ) {
        log.info("GET /api/v1/groups/{}/members called", groupId);
        return groupMemberService.getGroupMembers(groupId);
    }
    @PostMapping
    public void getGroupMembers(
            @PathVariable Long groupId,@RequestBody AddGroupMembersRequestDto request
    ) {
        log.info("GET /api/v1/groups/{}/members called", groupId);
        groupMemberService.addMembers(groupId,request);
    }
}
