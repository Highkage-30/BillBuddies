package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.MemberResponseDto;
import com.billbuddies.billbuddies_backend.dto.MemberStatementResponseDto;

import java.util.List;

public interface MemberService {

    List<MemberResponseDto> getAllMembers();
    MemberStatementResponseDto getMemberStatement(Long memberId);
}
