package com.billbuddies.billbuddies_backend.repository;

import com.billbuddies.billbuddies_backend.entity.GroupMember;
import com.billbuddies.billbuddies_backend.entity.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository
        extends JpaRepository<GroupMember, GroupMemberId> {

    List<GroupMember> findByGroup_GroupIdOrderByMember_MemberNameAsc(Long groupId);
    boolean existsByGroup_GroupIdAndMember_MemberId(Long groupId, Long memberId);

    Optional<GroupMember> findByGroup_GroupIdAndMember_MemberId(Long groupId, Long memberId);
}
