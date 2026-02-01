package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.AddMemberResponseDto;
import com.billbuddies.billbuddies_backend.dto.GroupMemberResponseDto;
import com.billbuddies.billbuddies_backend.entity.Group;
import com.billbuddies.billbuddies_backend.entity.GroupMember;
import com.billbuddies.billbuddies_backend.entity.GroupMemberId;
import com.billbuddies.billbuddies_backend.entity.Member;
import com.billbuddies.billbuddies_backend.exception.*;
import com.billbuddies.billbuddies_backend.repository.GroupMemberRepository;
import com.billbuddies.billbuddies_backend.repository.GroupRepository;
import com.billbuddies.billbuddies_backend.repository.MemberRepository;
import com.billbuddies.billbuddies_backend.service.GroupMemberService;
import com.billbuddies.billbuddies_backend.util.NameNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${centralCounterParty.name}")
    private String CCP_NAME;

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

        return groupMemberRepository.findByGroup_GroupIdOrderByMember_MemberNameAsc(groupId)
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
        String normalizedName =
                NameNormalizer.capitalizeFirstLetter(memberName);

        if (normalizedName.equalsIgnoreCase(CCP_NAME)) {
            log.warn("Attempt to manually add CCP '{}' to groupId={}",
                    CCP_NAME, groupId);
            throw new CcpCannotBeAddedException(
                    "Central Counter Party cannot be added manually"
            );
        }
        // 1️⃣ Validate group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new GroupNotFoundException("Group not found for id: " + groupId)
                );
        boolean created;

        Optional<Member> existingMember =
                memberRepository.findByMemberNameIgnoreCase(normalizedName);

        Member member;
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

    @Override
    @Transactional
    public void removeMemberFromGroup(Long groupId, Long memberId) {
        log.info("Removing member '{}' from groupId={}", memberId, groupId);
        if(!groupRepository.existsById(groupId)) {
            throw new GroupNotFoundException("Group not found for id: " + groupId);
        }
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(
                        "Member not found for id: " + memberId
                ));
        if (member.getMemberName().equalsIgnoreCase(CCP_NAME)) {
            log.warn("Attempt to remove CCP '{}' from groupId={}",
                    CCP_NAME, groupId);
            throw new CcpCannotBeRemovedException(
                    "Central Counter Party cannot be removed from the group"
            );
        }
        GroupMember groupMember=groupMemberRepository.findByGroup_GroupIdAndMember_MemberId(groupId,memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found in group for id: " + memberId));

        groupMemberRepository.delete(groupMember);
        log.info("MemberId={} removed from groupId={}", memberId, groupId);
    }
}
