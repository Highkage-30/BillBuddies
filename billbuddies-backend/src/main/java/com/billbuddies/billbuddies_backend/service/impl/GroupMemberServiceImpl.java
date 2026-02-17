package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.AddGroupMembersRequestDto;
import com.billbuddies.billbuddies_backend.dto.GroupMemberResponseDto;
import com.billbuddies.billbuddies_backend.entity.GroupInfo;
import com.billbuddies.billbuddies_backend.entity.GroupMember;
import com.billbuddies.billbuddies_backend.entity.GroupMemberId;
import com.billbuddies.billbuddies_backend.entity.Member;
import com.billbuddies.billbuddies_backend.exception.BadRequestException;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.GroupInfoRepository;
import com.billbuddies.billbuddies_backend.repository.GroupMemberRepository;
import com.billbuddies.billbuddies_backend.repository.MemberRepository;
import com.billbuddies.billbuddies_backend.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupMemberServiceImpl implements GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;
    private final GroupInfoRepository groupInfoRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GroupMemberResponseDto> getGroupMembers(Long groupId) {

        log.info("Fetching members for groupId={}", groupId);

        // Validate group exists
        if (!groupInfoRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }

        return groupMemberRepository.findByGroup_GroupId(groupId)
                .stream()
                .map(this::toDto)
                .toList();
    }
    @Override
    @Transactional
    public void addMembers(Long groupId, AddGroupMembersRequestDto request) {

        if (request.getMemberNames() == null || request.getMemberNames().isEmpty()) {
            throw new BadRequestException("Member list cannot be empty");
        }

        GroupInfo group = groupInfoRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found with id: " + groupId));

        // Remove duplicates from payload (case-insensitive)
        Set<String> normalizedNames = new HashSet<>();
        for (String name : request.getMemberNames()) {
            if (name == null || name.isBlank()) {
                throw new BadRequestException("Member name cannot be blank");
            }
            normalizedNames.add(name.trim().toLowerCase());
        }

        for (String normalizedName : normalizedNames) {

            // 1️⃣ Find or create member
            Member member = memberRepository
                    .findByMemberNameIgnoreCase(normalizedName)
                    .orElseGet(() -> {
                        log.info("Creating new member: {}", normalizedName);
                        return memberRepository.save(
                                Member.builder()
                                        .memberName(capitalize(normalizedName))
                                        .build()
                        );
                    });

            // 2️⃣ Add to group if not already present
            GroupMemberId id =
                    new GroupMemberId(groupId, member.getMemberId());

            if (groupMemberRepository.existsById(id)) {
                log.info("Member {} already in group {}, skipping",
                        member.getMemberName(), groupId);
                continue;
            }

            groupMemberRepository.save(
                    GroupMember.builder()
                            .id(id)
                            .group(group)
                            .member(member)
                            .joinedAt(LocalDateTime.now())
                            .build()
            );
        }

        log.info("Members added successfully to groupId={}", groupId);
    }

    private String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private GroupMemberResponseDto toDto(GroupMember groupMember) {
        return GroupMemberResponseDto.builder()
                .memberId(groupMember.getMember().getMemberId())
                .memberName(groupMember.getMember().getMemberName())
                .build();
    }
}
