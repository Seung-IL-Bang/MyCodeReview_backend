package com.web.app.controller;

import com.web.app.ControllerTestSupport;
import com.web.app.dto.LikeRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class LikesControllerTest extends ControllerTestSupport {

    @DisplayName("로그인한 유저는 좋아요를 누를 수 있다.")
    @Test
    @WithMockUser
    public void postLikeByUser() throws Exception {
        // given
        Long boardId = 1L;
        LikeRequestDTO likeRequestDTO = new LikeRequestDTO(boardId, "test@gmail.com");

        willDoNothing().given(likesUseCase).executePost(any(LikeRequestDTO.class));

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
    
    @DisplayName("로그인한 유저는 좋아요를 취소할 수 있다.")
    @Test
    @WithMockUser
    void cancelLikeByUser() throws Exception {
        // given
        Long boardId = 1L;
        LikeRequestDTO likeRequestDTO = new LikeRequestDTO(boardId, "test@gmail.com");

        willDoNothing().given(likesUseCase).executeDelete(any(LikeRequestDTO.class));
        
        // when // then
        mockMvc.perform(MockMvcRequestBuilders.delete("/auth/like")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(likeRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(String.format(
                        "Deleted %d board's Like", boardId
                )));

    }



}