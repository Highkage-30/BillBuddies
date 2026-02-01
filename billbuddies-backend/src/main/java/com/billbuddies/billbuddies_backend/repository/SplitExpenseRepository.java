package com.billbuddies.billbuddies_backend.repository;

import com.billbuddies.billbuddies_backend.entity.SplitExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SplitExpenseRepository extends JpaRepository<SplitExpense, Long> {
    List<SplitExpense> findByGroupId(Long groupId);
}
