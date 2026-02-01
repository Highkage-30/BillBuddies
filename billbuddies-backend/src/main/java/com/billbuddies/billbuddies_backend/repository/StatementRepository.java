package com.billbuddies.billbuddies_backend.repository;

import com.billbuddies.billbuddies_backend.entity.Statement;
import com.billbuddies.billbuddies_backend.entity.StatementId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatementRepository
        extends JpaRepository<Statement, StatementId> {

    void deleteByGroupId(Long groupId);

    List<Statement> findByGroupIdOrderByMemberNameAsc(Long groupId);
    Optional<Statement> findByGroupIdAndMemberName(Long groupId, String memberName);
}
