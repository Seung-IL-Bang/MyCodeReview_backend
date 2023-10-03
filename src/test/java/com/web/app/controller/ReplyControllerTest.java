package com.web.app.controller;

import com.web.app.ControllerTestSupport;
import com.web.app.dto.ReplyRequestDTO;
import com.web.app.dto.ReplyResponseDTO;
import com.web.app.fixture.ReplyFixtureFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class ReplyControllerTest extends ControllerTestSupport {

    @DisplayName("로그인한 유저는 특정 댓글에 답글 작성을 요청할 수 있다.")
    @Test
    @WithMockUser
    void postReply() throws Exception {
        // given
        ReplyRequestDTO replyRequestDTO = ReplyFixtureFactory.createRequestDTO();
        ReplyResponseDTO replyResponseDTO = ReplyFixtureFactory.createResponseDTO();

        given(replyService.register(any(ReplyRequestDTO.class))).willReturn(replyResponseDTO);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/reply")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(replyRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(replyResponseDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(replyResponseDTO.getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberEmail").value(replyResponseDTO.getMemberEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberName").value(replyResponseDTO.getMemberName()));
    }

    @DisplayName("로그인한 유저는 본인이 작성한 답글에 대해 수정 요청을 할 수 있다.")
    @Test
    @WithMockUser
    void putReply() throws Exception {
        // given
        ReplyRequestDTO replyRequestDTO = ReplyFixtureFactory.createRequestDTO();

        willDoNothing().given(replyService).update(any(ReplyRequestDTO.class), any(HttpServletRequest.class));


        // when // then
        mockMvc.perform(MockMvcRequestBuilders.put("/auth/reply")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(replyRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("답글이 수정되었습니다."));
    }

    @DisplayName("로그인한 유저는 본인이 작성한 답글에 대해 삭제 요청을 할 수 있다.")
    @Test
    @WithMockUser
    void deleteReply() throws Exception {
        // given
        willDoNothing().given(replyService).remove(anyLong(), any(HttpServletRequest.class));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/auth/reply/%d", 1L))
                        .with(csrf()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("답글이 삭제되었습니다."));

    }
}