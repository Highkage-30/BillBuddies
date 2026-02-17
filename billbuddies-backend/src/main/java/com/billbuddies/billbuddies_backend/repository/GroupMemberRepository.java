package com.billbuddies.billbuddies_backend.repository;

import com.billbuddies.billbuddies_backend.entity.GroupMember;
import com.billbuddies.billbuddies_backend.entity.GroupMemberId;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberRepository
        extends JpaRepository<GroupMember, GroupMemberId> {

    List<GroupMember> findByGroup_GroupId(Long groupId);
    boolean existsById(@Nonnull GroupMemberId id);
    void deleteByGroup_GroupId(Long groupId);
}
