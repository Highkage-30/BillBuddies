package com.billbuddies.billbuddies_backend.repository;

import com.billbuddies.billbuddies_backend.entity.Group;
import com.billbuddies.billbuddies_backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findAllByOrderByGroupNameAsc();
    boolean existsByGroupNameIgnoreCase(String groupName);

}
