package com.web.app.controller;

import com.web.app.ControllerTestSupport;
import com.web.app.domain.member.Member;
import com.web.app.domain.member.MemberRole;
import com.web.app.dto.MemberDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends ControllerTestSupport {

    @DisplayName("JWT 토큰에 담긴 유저 정보를 조회한다.")
    @Test
    @WithMockUser
    void getUserInfo() throws Exception{
        // given
        Member member = new Member("test@gmail.com", "testName", "testPicture", MemberRole.USER);
        given(memberService.read(any(HttpServletRequest.class)))
                .willReturn(member);

        MemberDTO dto = new MemberDTO();
        dto.setEmail("test@gmail.com");
        dto.setName("testName");
        given(modelMapper.map(member, MemberDTO.class))
                .willReturn(dto);

        // when // then
        mockMvc.perform(get("/auth/userinfo")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.name").value("testName"));
    }
}