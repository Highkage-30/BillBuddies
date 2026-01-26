package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.MemberResponseDto;
import com.billbuddies.billbuddies_backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@CrossOrigin(
        origins = "http://192.168.1.5:3000",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
)
public class MemberController {
    private final MemberService memberService;
    @GetMapping("/members")
    public List<MemberResponseDto> getAllMembers() {
        return memberService.getAllMembers();
    }
}
