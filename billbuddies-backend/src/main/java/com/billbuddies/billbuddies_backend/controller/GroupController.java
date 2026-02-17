package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.CreateGroupRequestDto;
import com.billbuddies.billbuddies_backend.dto.GroupResponseDto;
import com.billbuddies.billbuddies_backend.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Slf4j
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public List<GroupResponseDto> getAllGroups() {
        log.info("GET /api/v1/groups called");
        return groupService.getAllGroups();
    }
    @PostMapping
    public GroupResponseDto createGroup(
            @RequestBody CreateGroupRequestDto request
    ) {
        log.info("POST /api/v1/groups called");
        return groupService.createGroup(request);
    }
    @DeleteMapping("/{groupId}")
    public void deleteGroup(@PathVariable Long groupId) {
        log.info("DELETE /api/v1/groups/{}", groupId);
        groupService.deleteGroup(groupId);
    }
}
