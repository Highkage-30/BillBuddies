package com.billbuddies.billbuddies_backend.repository;

import com.billbuddies.billbuddies_backend.entity.PoolTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PoolTransactionRepository
        extends JpaRepository<PoolTransaction, Long> {

    List<PoolTransaction>
    findByGroupPool_PoolIdOrderByCreatedAtDesc(Long poolId);
}
