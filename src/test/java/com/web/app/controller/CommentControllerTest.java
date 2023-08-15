package com.web.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.app.domain.comment.Comment;
import com.web.app.dto.CommentRequestDTO;
import com.web.app.dto.CommentResponseDTO;
import com.web.app.fixture.CommentFixtureFactory;
import com.web.app.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;


    @DisplayName("로그인한 회원은 게시글에 댓글을 달 수 있다.")
    @Test
    @WithMockUser
    void postComment() throws Exception {
        // given
        CommentRequestDTO requestDTO = CommentFixtureFactory.createRequestDTO();
        CommentResponseDTO responseDTO = CommentFixtureFactory.createResponseDTO();

        given(commentService.register(any(CommentRequestDTO.class))).willReturn(responseDTO);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/comment")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(responseDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(responseDTO.getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberEmail").value(responseDTO.getMemberEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberName").value(responseDTO.getMemberName()));

    }

    @DisplayName("로그인한 회원은 본인이 작성한 게시글의 댓글을 수정할 수 있다.")
    @Test
    @WithMockUser
    void putComment() throws Exception {
        // given
        CommentRequestDTO requestDTO = CommentFixtureFactory.createRequestDTO();
        doNothing().when(commentService).update(any(CommentRequestDTO.class), any(HttpServletRequest.class));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.put("/auth/comment")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Updated Comment!"));
    }
    
    @DisplayName("로그인한 회원은 본인이 작성한 게시글의 댓글을 삭제할 수 있다.")
    @Test
    @WithMockUser
    void deleteComment() throws Exception{
        // given
        doNothing().when(commentService).remove(anyLong(), any(HttpServletRequest.class));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/auth/comment/%d", 1L))
                        .with(csrf()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Deleted Comment!"));
    }


}