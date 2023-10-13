package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface BoardService {

    Long register(BoardRequestDTO BoardRequestDTO);

    Board modify(HttpServletRequest request, Long id, BoardRequestDTO BoardRequestDTO);

    void remove(HttpServletRequest request, Long id);

    BoardResponseDTO read(Long id, HttpServletRequest request);

    PageResponseDTO<BoardResponseDTO> readPublicAllWithPagingAndSearch(PageRequestDTO pageRequestDTO);

    PageResponseWithCategoryDTO<BoardListResponseDTO> readAllWithPagingAndSearch(HttpServletRequest request, PageRequestDTO pageRequestDTO);

    PageResponseDTO<BoardResponseDTO> readByEmailLikeBoardsWithPaging(HttpServletRequest request, PageRequestDTO pageRequestDTO);
}
