package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.MemberStatementResponseDto;
import com.billbuddies.billbuddies_backend.service.MemberStatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemberStatementController {
    private final MemberStatementService memberStatementService;
    @GetMapping("/members/{memberId}/statement")
    public MemberStatementResponseDto  getStatement(@PathVariable Long memberId) {
        return memberStatementService.getMemberStatement(memberId);
    }
}
