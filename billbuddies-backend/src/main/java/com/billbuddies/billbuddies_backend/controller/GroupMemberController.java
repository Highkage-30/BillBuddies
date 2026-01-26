package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.*;
import com.billbuddies.billbuddies_backend.service.GroupMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@CrossOrigin(
        origins = "http://192.168.1.5:3000",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
)
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    @GetMapping("/groups/{groupId}/members")
    public List<GroupMemberResponseDto> getMembersByGroupId(
            @PathVariable Long groupId
    ) {
        return groupMemberService.getMembersByGroupId(groupId);
    }

    @PostMapping("/groups/{groupId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public AddMemberResponseDto addMemberToGroup(
            @PathVariable Long groupId,
            @RequestBody @Valid AddMemberRequestDto request
    ) {
        return groupMemberService.addMemberToGroup(groupId, request.getMemberName());
    }

    @DeleteMapping("/groups/{groupId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Void deleteMemberFromGroup(@PathVariable Long groupId, @PathVariable Long memberId) {
        groupMemberService.removeMemberFromGroup(groupId, memberId);
        return null;
    }
}
