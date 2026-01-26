package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.MemberResponseDto;
import com.billbuddies.billbuddies_backend.entity.Member;
import com.billbuddies.billbuddies_backend.repository.MemberRepository;
import com.billbuddies.billbuddies_backend.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    @Transactional
    @Override
    public List<MemberResponseDto> getAllMembers() {

        log.info("Fetching all members");
        return memberRepository.findAll()
                .stream()
                .map(m -> new MemberResponseDto(
                        m.getMemberName(),
                        m.getMemberId()
                ))
                .toList();
    }
}
