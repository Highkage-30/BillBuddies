package com.billbuddies.billbuddies_backend.repository;

import com.billbuddies.billbuddies_backend.entity.MemberTransaction;
import com.billbuddies.billbuddies_backend.entity.enums.TransactionReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberTransactionRepository
        extends JpaRepository<MemberTransaction, Long> {
    List<MemberTransaction> findByGroup_GroupId(Long groupId);
    void deleteByOriginalExpense_OriginalExpenseId(Long originalExpenseId);

    void deleteByGroup_GroupId(Long groupId);
}
