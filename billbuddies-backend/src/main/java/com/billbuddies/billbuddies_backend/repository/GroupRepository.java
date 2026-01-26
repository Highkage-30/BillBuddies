package com.billbuddies.billbuddies_backend.repository;

import com.billbuddies.billbuddies_backend.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
