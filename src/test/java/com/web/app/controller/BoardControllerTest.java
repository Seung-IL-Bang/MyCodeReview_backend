package com.web.app.controller;

import com.web.app.ControllerTestSupport;
import com.web.app.dto.BoardListResponseDTO;
import com.web.app.dto.BoardResponseDTO;
import com.web.app.dto.PageRequestDTO;
import com.web.app.dto.PageResponseDTO;
import com.web.app.fixture.BoardFixtureFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class BoardControllerTest extends ControllerTestSupport {


    @DisplayName("로그인 또는 비로그인 유저가 Board Id 에 해당하는 게시글을 조회합니다.")
    @Test
    @WithMockUser
    void getBoard() throws Exception {
        //given
        BoardResponseDTO boardResponseDTO = BoardFixtureFactory.createResponseDTO();
        given(getEmailFromJWT.execute(any(HttpServletRequest.class))).willReturn(boardResponseDTO.getEmail());
        given(boardService.read(anyLong(), anyString())).willReturn(boardResponseDTO);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/board/%d", boardResponseDTO.getId()))
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
                .andExpect(jsonPath("$.reviewList").isArray())
                .andExpect(jsonPath("$.commentList").isArray());
    }

    @DisplayName("유저의 이메일을 통해 해당 유저가 작성한 모든 게시글들을 조회한다.")
    @Test
    @WithMockUser
    void getBoardList() throws Exception {
        //given
//        Board board1 = BoardFixtureFactory.create(1L);
//        Board board2 = BoardFixtureFactory.create(2L);
//        Board board3 = BoardFixtureFactory.create(3L);
//
//        List<Board> boardList = List.of(board1, board2, board3);
//
//        given(getBoardListFromEmailOfJWT.execute(any(HttpServletRequest.class)))
//                .willReturn(boardList);
//
//        // when // then
//        mockMvc.perform(MockMvcRequestBuilders.get("/auth/board/list"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(boardList)));
    }

    @DisplayName("로그인 여부와 상관없이 페이징 처리된 최신 게시글 목록을 조회할 수 있다.")
    @Test
    @WithMockUser // Why ??? /auth 가 없는데도 불구하고 테스트시 302 로그인 페이지로 리다이렉팅된다.
    void getPublicBoardList() throws Exception{
        //given
        BoardListResponseDTO boardResponseDTO1 = BoardFixtureFactory.createListResponseDTO(1L);
        BoardListResponseDTO boardResponseDTO2 = BoardFixtureFactory.createListResponseDTO(2L);
        BoardListResponseDTO boardResponseDTO3 = BoardFixtureFactory.createListResponseDTO(3L);


        List<BoardListResponseDTO> dtoList = List.of(boardResponseDTO1, boardResponseDTO2, boardResponseDTO3);

        PageResponseDTO<BoardListResponseDTO> result = PageResponseDTO.<BoardListResponseDTO>builder()
                .pageRequestDTO(new PageRequestDTO())
                .dtoList(dtoList)
                .total(dtoList.size())
                .build();


        given(boardService.readPublicAllWithPagingAndSearch(any(PageRequestDTO.class)))
                .willReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/board/list")
                        .with(csrf())
                        .with(digest())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(8))
                .andExpect(jsonPath("$.dtoList").isArray())
                .andExpect(jsonPath("$.total").value(dtoList.size()));
    }

}