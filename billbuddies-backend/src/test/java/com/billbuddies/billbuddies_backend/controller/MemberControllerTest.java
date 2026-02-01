package com.billbuddies.billbuddies_backend.controller;

import com.billbuddies.billbuddies_backend.dto.MemberResponseDto;
import com.billbuddies.billbuddies_backend.service.MemberService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    void getAllMembers_shouldReturn200AndMemberList() throws Exception {
        List<MemberResponseDto> mockMembers = List.of(
                new MemberResponseDto("Arnav",1L),
                new MemberResponseDto("Kaushik",69L)
        );
        Mockito.when(memberService.getAllMembers()).thenReturn(mockMembers);
        mockMvc.perform(get("/api/v1/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].memberName").value("Arnav"))
                .andExpect(jsonPath("$[0].memberId").value(1L))
                .andExpect(jsonPath("$[1].memberName").value("Kaushik"))
                .andExpect(jsonPath("$[1].memberId").value(69L));

    }
}
