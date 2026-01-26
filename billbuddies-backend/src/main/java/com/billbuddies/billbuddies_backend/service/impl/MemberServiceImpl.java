package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.MemberResponseDto;
import com.billbuddies.billbuddies_backend.repository.MemberRepository;
import com.billbuddies.billbuddies_backend.service.MemberService;
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
    @Transactional(readOnly = true)
    @Override
    public List<MemberResponseDto> getAllMembers() {

        log.debug("Fetching all members");
        return memberRepository.findAllByOrderByMemberNameAsc()
                .stream()
                .map(m -> new MemberResponseDto(
                        m.getMemberName(),
                        m.getMemberId()
                ))
                .toList();
    }
}
