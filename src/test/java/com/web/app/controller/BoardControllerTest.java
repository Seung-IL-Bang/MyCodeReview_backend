package com.web.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.app.domain.board.Board;
import com.web.app.dto.BoardResponseDTO;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.mediator.GetBoardListFromEmailOfJWT;
import com.web.app.mediator.GetEmailFromJWT;
import com.web.app.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BoardService boardService;

    @MockBean
    private GetBoardListFromEmailOfJWT getBoardListFromEmailOfJWT;

    @MockBean
    private GetEmailFromJWT getEmailFromJWT;


    @DisplayName("Board Id 에 해당하는 게시글을 조회합니다.")
    @Test
    @WithMockUser
    void getBoard() throws Exception {
        //given
        Long boardId = any(Long.class);

        BoardResponseDTO boardResponseDTO = BoardFixtureFactory.createResponseDTO();

        given(boardService.read(boardId)).willReturn(boardResponseDTO);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/auth/board/%d", boardId))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(boardResponseDTO.getId()))
                .andExpect(jsonPath("$.title").value(boardResponseDTO.getTitle()))
                .andExpect(jsonPath("$.content").value(boardResponseDTO.getContent()))
                .andExpect(jsonPath("$.tagList").isArray())
                .andExpect(jsonPath("$.link").value(boardResponseDTO.getLink()))
                .andExpect(jsonPath("$.difficulty").value(boardResponseDTO.getDifficulty()))
                .andExpect(jsonPath("$.writer").value(boardResponseDTO.getWriter()))
                .andExpect(jsonPath("$.email").value(boardResponseDTO.getEmail()))
                .andExpect(jsonPath("$.reviewList").isArray());
    }

    @DisplayName("유저의 이메일을 통해 해당 유저가 작성한 모든 게시글들을 조회한다.")
    @Test
    @WithMockUser
    void getBoardList() throws Exception {
        //given
        Board board1 = BoardFixtureFactory.create(1L);
        Board board2 = BoardFixtureFactory.create(2L);
        Board board3 = BoardFixtureFactory.create(3L);

        List<Board> boardList = List.of(board1, board2, board3);

        given(getBoardListFromEmailOfJWT.execute(any(HttpServletRequest.class)))
                .willReturn(boardList);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/board/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(boardList)));
    }

}