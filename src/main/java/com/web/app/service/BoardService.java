package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.dto.BoardDTO;
import com.web.app.dto.PageRequestDTO;
import com.web.app.dto.PageResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface BoardService {

    public Long register(BoardDTO BoardDTO);

    public BoardDTO read(Long id);

    public List<Board> readAll(String email);

    public PageResponseDTO<BoardDTO> readAllWithPaging(String email, PageRequestDTO pageRequestDTO);

    public PageResponseDTO<BoardDTO> readAllWithPagingAndSearch(String email, PageRequestDTO pageRequestDTO);

    public Board modify(HttpServletRequest request, Long id, BoardDTO BoardDTO);

    public void remove(HttpServletRequest request, Long id);

}
