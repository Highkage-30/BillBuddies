package com.billbuddies.billbuddies_backend.repository;

import com.billbuddies.billbuddies_backend.entity.FundsObligation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FundsObligationRepository extends JpaRepository<FundsObligation, Long> {

    List<FundsObligation> findByGroupId(Long groupId);
}
