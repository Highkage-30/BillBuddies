package com.billbuddies.billbuddies_backend.repository;

import com.billbuddies.billbuddies_backend.entity.GroupMember;
import com.billbuddies.billbuddies_backend.entity.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberRepository
        extends JpaRepository<GroupMember, GroupMemberId> {

    List<GroupMember> findByGroup_GroupId(Long groupId);
    boolean existsByGroup_GroupIdAndMember_MemberId(Long groupId, Long memberId);

}
