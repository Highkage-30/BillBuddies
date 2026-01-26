package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.AddMemberResponseDto;
import com.billbuddies.billbuddies_backend.dto.GroupMemberResponseDto;
import com.billbuddies.billbuddies_backend.entity.Group;
import com.billbuddies.billbuddies_backend.entity.GroupMember;
import com.billbuddies.billbuddies_backend.entity.GroupMemberId;
import com.billbuddies.billbuddies_backend.entity.Member;
import com.billbuddies.billbuddies_backend.exception.GroupNotFoundException;
import com.billbuddies.billbuddies_backend.exception.MemberAlreadyInGroupException;
import com.billbuddies.billbuddies_backend.repository.GroupMemberRepository;
import com.billbuddies.billbuddies_backend.repository.GroupRepository;
import com.billbuddies.billbuddies_backend.repository.MemberRepository;
import com.billbuddies.billbuddies_backend.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;

    /**
     * GET members of a group
     */
    @Transactional(readOnly = true)
    @Override
    public List<GroupMemberResponseDto> getMembersByGroupId(Long groupId) {

        log.info("Fetching members for groupId={}", groupId);

        if (!groupRepository.existsById(groupId)) {
            throw new GroupNotFoundException("Group not found for id: " + groupId);
        }

        return groupMemberRepository.findByGroup_GroupId(groupId)
                .stream()
                .map(gm -> new GroupMemberResponseDto(
                        gm.getMember().getMemberName(),
                        gm.getMember().getMemberId()
                ))
                .toList();
    }

    /**
     * POST add member to group (create or map)
     */
    @Transactional
    @Override
    public AddMemberResponseDto addMemberToGroup(Long groupId, String memberName) {

        log.info("Adding member '{}' to groupId={}", memberName, groupId);

        // 1️⃣ Validate group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new GroupNotFoundException("Group not found for id: " + groupId)
                );

        // 2️⃣ Normalize member name
        String normalizedName = memberName.trim();

        // 3️⃣ Find or create member (NO lambda misuse)
        Member member;
        boolean created;

        Optional<Member> existingMember =
                memberRepository.findByMemberNameIgnoreCase(normalizedName);

        if (existingMember.isPresent()) {
            member = existingMember.get();
            created = false;
        } else {
            member = new Member();
            member.setMemberName(normalizedName);
            member = memberRepository.save(member);
            created = true;
        }

        // 4️⃣ Prevent duplicate mapping
        if (groupMemberRepository
                .existsByGroup_GroupIdAndMember_MemberId(
                        groupId, member.getMemberId()
                )) {
            throw new MemberAlreadyInGroupException(
                    "Member already exists in group"
            );
        }

        // 5️⃣ Create group-member mapping
        GroupMember groupMember = new GroupMember();
        groupMember.setId(new GroupMemberId(groupId, member.getMemberId()));
        groupMember.setGroup(group);
        groupMember.setMember(member);
        groupMember.setJoinedAt(LocalDateTime.now());

        groupMemberRepository.save(groupMember);

        log.info("Member '{}' successfully mapped to groupId={}",
                member.getMemberName(), groupId);

        return new AddMemberResponseDto(
                member.getMemberId(),
                member.getMemberName(),
                created ? "CREATED_AND_MAPPED" : "EXISTING_AND_MAPPED"
        );
    }
}
