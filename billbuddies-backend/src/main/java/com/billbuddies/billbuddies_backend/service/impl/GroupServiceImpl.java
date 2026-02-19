package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.CreateGroupRequestDto;
import com.billbuddies.billbuddies_backend.dto.GroupResponseDto;
import com.billbuddies.billbuddies_backend.entity.*;
import com.billbuddies.billbuddies_backend.exception.BadRequestException;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.*;
import com.billbuddies.billbuddies_backend.service.GroupService;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupServiceImpl implements GroupService {

    private final GroupInfoRepository groupInfoRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;
    private final MemberTransactionRepository memberTransactionRepository;
    private final StatementRepository statementRepository;
    private final OriginalExpenseRepository originalExpenseRepository;

    // 🔹 NEW
    private final GroupPoolRepository groupPoolRepository;
    private final PoolTransactionRepository poolTransactionRepository;

    @Override
    @Transactional
    public List<GroupResponseDto> getAllGroups() {
        log.info("Fetching all groups");

        return groupInfoRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public GroupResponseDto createGroup(@Nonnull CreateGroupRequestDto request) {

        log.info("Creating group with name={}", request.getGroupName());

        validateRequest(request);

        // 1️⃣ Create group
        GroupInfo group = groupInfoRepository.save(
                GroupInfo.builder()
                        .groupName(request.getGroupName().trim())
                        .description(request.getGroupDescription())
                        .build()
        );

        // 2️⃣ 🔹 CREATE GROUP POOL (NEW)
        GroupPool pool = groupPoolRepository.save(
                GroupPool.builder()
                        .group(group)
                        .balance(BigDecimal.ZERO)
                        .build()
        );

        log.info("Group pool created for groupId={}, poolId={}",
                group.getGroupId(), pool.getPoolId());

        // 3️⃣ Create / attach members
        for (String memberName : request.getMemberList()) {

            Member member = memberRepository
                    .findByMemberNameIgnoreCase(memberName.trim())
                    .orElseGet(() -> {
                        log.info("Creating new member={}", memberName);
                        return memberRepository.save(
                                Member.builder()
                                        .memberName(memberName.trim())
                                        .build()
                        );
                    });

            GroupMember groupMember = GroupMember.builder()
                    .id(new GroupMemberId(group.getGroupId(), member.getMemberId()))
                    .group(group)
                    .member(member)
                    .build();

            groupMemberRepository.save(groupMember);
        }

        log.info("Group created successfully with id={}", group.getGroupId());

        return toDto(group);
    }

    private void validateRequest(CreateGroupRequestDto request) {

        if (request.getGroupName() == null || request.getGroupName().isBlank()) {
            throw new BadRequestException("Group name must not be empty");
        }

        if (request.getMemberList() == null || request.getMemberList().isEmpty()) {
            throw new BadRequestException("Group must have at least one member");
        }
    }

    private GroupResponseDto toDto(GroupInfo group) {
        return GroupResponseDto.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupDescription(group.getDescription())
                .createdAt(group.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void deleteGroup(Long groupId) {

        log.info("Deleting groupId={} and all related data", groupId);

        GroupInfo group = groupInfoRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found: " + groupId));

        // 1️⃣ Delete member ledger
        memberTransactionRepository.deleteByGroup_GroupId(groupId);

        // 2️⃣ Delete statements
        statementRepository.deleteByGroup_GroupId(groupId);

        // 3️⃣ Delete expenses
        originalExpenseRepository.deleteByGroup_GroupId(groupId);

        // 4️⃣ Delete pool transactions (NEW)
//        poolTransactionRepository.deleteByGroup_GroupId(groupId);

        // 5️⃣ Delete group pool (NEW)
        groupPoolRepository.findByGroup_GroupId(groupId)
                .ifPresent(groupPoolRepository::delete);

        // 6️⃣ Delete group members
        groupMemberRepository.deleteByGroup_GroupId(groupId);

        // 7️⃣ Delete group itself
        groupInfoRepository.delete(group);

        log.info("GroupId={} deleted successfully", groupId);
    }
}
