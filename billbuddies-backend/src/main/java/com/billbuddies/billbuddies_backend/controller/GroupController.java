package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.CreateGroupRequestDto;
import com.billbuddies.billbuddies_backend.dto.CreateGroupResponseDto;
import com.billbuddies.billbuddies_backend.dto.GroupResponseDto;
import com.billbuddies.billbuddies_backend.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class GroupController {
    private final GroupService groupService;
    @GetMapping("/groups")
    List<GroupResponseDto> getAllGroups() {
        return groupService.getAllGroups();
    }
    @PostMapping("/groups")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateGroupResponseDto createGroup(@RequestBody @Valid CreateGroupRequestDto createGroupRequestDto) {
        log.info("Creating group {}", createGroupRequestDto.getGroupName());
        return groupService.createGroup(createGroupRequestDto);
    }
}

