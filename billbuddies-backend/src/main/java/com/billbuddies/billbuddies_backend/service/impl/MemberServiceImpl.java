package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.MemberResponseDto;
import com.billbuddies.billbuddies_backend.repository.MemberRepository;
import com.billbuddies.billbuddies_backend.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    @Value("${centralCounterParty.name}")
    private String CCP_NAME;
    @Transactional(readOnly = true)
    @Override
    public List<MemberResponseDto> getAllMembers() {
        log.info("Fetching all members");
        return memberRepository.findByMemberNameIgnoreCaseNotOrderByMemberNameAsc(CCP_NAME)
                .stream()
                .map(m -> new MemberResponseDto(
                        m.getMemberName(),
                        m.getMemberId()
                ))
                .toList();
    }
}
