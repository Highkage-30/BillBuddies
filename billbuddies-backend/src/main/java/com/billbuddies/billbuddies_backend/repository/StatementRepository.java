package com.billbuddies.billbuddies_backend.repository;

import com.billbuddies.billbuddies_backend.entity.Statement;
import com.billbuddies.billbuddies_backend.entity.StatementId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatementRepository
        extends JpaRepository<Statement, StatementId> {

    void deleteByGroup_GroupId(Long groupId);
    List<Statement> findByGroup_GroupId(Long groupId);
    List<Statement> findByMember_MemberId(Long memberId);

}
