package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface BoardService {

    public Long register(BoardRequestDTO BoardRequestDTO);

    public BoardResponseDTO read(Long id);

    public List<Board> readAll(String email);

    public PageResponseDTO<BoardRequestDTO> readAllWithPaging(String email, PageRequestDTO pageRequestDTO);

    public PageResponseWithCategoryDTO<BoardResponseDTO> readAllWithPagingAndSearch(String email, PageRequestDTO pageRequestDTO);

    public Board modify(HttpServletRequest request, Long id, BoardRequestDTO BoardRequestDTO);

    public void remove(HttpServletRequest request, Long id);
}
