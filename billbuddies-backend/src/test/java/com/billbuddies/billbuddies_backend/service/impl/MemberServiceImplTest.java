package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.MemberResponseDto;
import com.billbuddies.billbuddies_backend.entity.Member;
import com.billbuddies.billbuddies_backend.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;


import java.util.List;

@ExtendWith(SpringExtension.class)
public class MemberServiceImplTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;
    @Test
    void getAllMembers_shouldReturnMembersExcludingCCP(){
        ReflectionTestUtils.setField(
                memberService,
                "CCP_NAME",
                "BillBuddy"
        );
        Member member1 = new Member(1L, "Arnav", null);
        Member member2 = new Member(69L, "Kaushik", null);
        Mockito.when(
                memberRepository
                        .findByMemberNameIgnoreCaseNotOrderByMemberNameAsc("BillBuddy")
        ).thenReturn(List.of(member1, member2));

        // WHEN: service method is called
        List<MemberResponseDto> result = memberService.getAllMembers();

        // THEN: verify result
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMemberName()).isEqualTo("Alice");
        assertThat(result.get(0).getMemberId()).isEqualTo(1L);
        assertThat(result.get(1).getMemberName()).isEqualTo("Bob");
        assertThat(result.get(1).getMemberId()).isEqualTo(2L);
    }
}
