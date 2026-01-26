package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.CreateGroupRequestDto;
import com.billbuddies.billbuddies_backend.dto.CreateGroupResponseDto;
import com.billbuddies.billbuddies_backend.dto.GroupResponseDto;
import com.billbuddies.billbuddies_backend.entity.Group;
import com.billbuddies.billbuddies_backend.entity.GroupMember;
import com.billbuddies.billbuddies_backend.entity.GroupMemberId;
import com.billbuddies.billbuddies_backend.entity.Member;
import com.billbuddies.billbuddies_backend.exception.GroupAlreadyExistsException;
import com.billbuddies.billbuddies_backend.repository.GroupMemberRepository;
import com.billbuddies.billbuddies_backend.repository.GroupRepository;
import com.billbuddies.billbuddies_backend.repository.MemberRepository;
import com.billbuddies.billbuddies_backend.service.GroupService;
import com.billbuddies.billbuddies_backend.util.NameNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    @Value("${centralCounterParty.name}")
    private String CCP_NAME;
    @Override
    @Transactional(readOnly = true)
    public List<GroupResponseDto> getAllGroups() {
        log.info("getAllGroups()");
        return groupRepository.findAllByOrderByGroupNameAsc()
                .stream()
                .map(g->new GroupResponseDto(
                        g.getGroupId(),
                        g.getGroupName(),
                        g.getGroupDescription(),
                        g.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    @Override
    public CreateGroupResponseDto createGroup(CreateGroupRequestDto request) {

        String normalizedGroupName =
                NameNormalizer.capitalizeFirstLetter(request.getGroupName());

        log.info("Creating group '{}'", normalizedGroupName);

        if (groupRepository.existsByGroupNameIgnoreCase(normalizedGroupName)) {
            log.warn("Group '{}' already exists", normalizedGroupName);
            throw new GroupAlreadyExistsException(
                    "Group already exists with name: " + normalizedGroupName
            );
        }

        Group group = new Group();
        group.setGroupName(normalizedGroupName);
        group.setGroupDescription(request.getGroupDescription());
        group.setCreatedAt(LocalDateTime.now());

        group = groupRepository.save(group);

        Set<String> allMembers = new HashSet<>();

        // Always add CCP
        allMembers.add(CCP_NAME);

        if (request.getMemberList() != null) {
            request.getMemberList()
                    .stream()
                    .map(NameNormalizer::capitalizeFirstLetter)
                    .filter(name -> !name.isEmpty())
                    .forEach(allMembers::add);
        }

        for (String memberName : allMembers) {

            Member member = memberRepository
                    .findByMemberNameIgnoreCase(memberName)
                    .orElseGet(() -> {
                        log.info("Creating new member '{}'", memberName);
                        Member m = new Member();
                        m.setMemberName(memberName);
                        return memberRepository.save(m);
                    });

            GroupMember groupMember = new GroupMember(
                    new GroupMemberId(group.getGroupId(), member.getMemberId()),
                    group,
                    member,
                    LocalDateTime.now()
            );

            groupMemberRepository.save(groupMember);

            log.debug("Mapped member '{}' to group '{}'",
                    member.getMemberName(), group.getGroupName());
        }

        log.info("Group '{}' created successfully", group.getGroupName());

        return new CreateGroupResponseDto(
                group.getGroupId(),
                group.getGroupName(),
                "CREATED"
        );
    }
}
