package com.web.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.app.dto.LikeRequestDTO;
import com.web.app.service.LikesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = LikesController.class)
class LikesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private LikesService likesService;

    @DisplayName("로그인한 유저는 좋아요를 누를 수 있다.")
    @Test
    @WithMockUser
    public void postLikeByUser() throws Exception {
        // given
        Long boardId = 1L;
        LikeRequestDTO likeRequestDTO = new LikeRequestDTO(boardId, "test@gmail.com");

        willDoNothing().given(likesService).postLike(any(LikeRequestDTO.class));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/like")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(likeRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(String.format(
                        "Liked %d board", boardId
                )));
    }



}