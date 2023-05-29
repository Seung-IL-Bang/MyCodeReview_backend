package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.dto.BoardDTO;

import java.util.List;

public interface BoardService {

    public Long register(BoardDTO BoardDTO);

    public Board read(Long id);

    public List<Board> readAll(String email);

    public Board modify(BoardDTO BoardDTO);

    public void remove(Long id);

}
