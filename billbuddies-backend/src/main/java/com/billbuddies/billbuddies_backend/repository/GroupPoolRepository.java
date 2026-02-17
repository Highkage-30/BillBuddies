package com.billbuddies.billbuddies_backend.repository;

import com.billbuddies.billbuddies_backend.entity.GroupPool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupPoolRepository extends JpaRepository<GroupPool, Long> {
    Optional<GroupPool> findByGroup_GroupId(Long groupId);
}
