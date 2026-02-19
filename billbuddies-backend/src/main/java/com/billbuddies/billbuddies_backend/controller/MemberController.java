package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.MemberResponseDto;
import com.billbuddies.billbuddies_backend.dto.MemberStatementResponseDto;
import com.billbuddies.billbuddies_backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public List<MemberResponseDto> getAllMembers() {
        log.info("GET /api/v1/members called");
        return memberService.getAllMembers();
    }
    @GetMapping("/{memberId}/statement")
    public MemberStatementResponseDto getStatement(@PathVariable Long memberId) {
        log.info("GET /api/v1/members/{memberId}/statement called");
        return memberService.getMemberStatement(memberId);
    }

}
