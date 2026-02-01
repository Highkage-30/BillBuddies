package com.billbuddies.billbuddies_backend.repository;

import com.billbuddies.billbuddies_backend.entity.OriginalExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OriginalExpenseRepository extends JpaRepository<OriginalExpense, Long> {
    List<OriginalExpense> findByGroupIdOrderByExpenseDateDesc(Long groupId) ;
}
