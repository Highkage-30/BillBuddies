package com.billbuddies.billbuddies_backend.repository;

import com.billbuddies.billbuddies_backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberNameIgnoreCase(String memberName);
    List<Member> findByMemberNameIgnoreCaseNotOrderByMemberNameAsc(String memberName);

}
