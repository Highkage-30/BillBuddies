package com.billbuddies.billbuddies_backend.repository;

import com.billbuddies.billbuddies_backend.entity.OriginalExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OriginalExpenseRepository
        extends JpaRepository<OriginalExpense, Long> {

    List<OriginalExpense> findByGroup_GroupIdOrderByExpenseDateDesc(Long groupId);
    void deleteByGroup_GroupId(Long groupId);
}
