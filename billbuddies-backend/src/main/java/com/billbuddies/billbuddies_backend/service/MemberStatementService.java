package com.billbuddies.billbuddies_backend.service;

import com.billbuddies.billbuddies_backend.dto.MemberStatementResponseDto;

public interface MemberStatementService {

    MemberStatementResponseDto getMemberStatement(Long memberId);
}
