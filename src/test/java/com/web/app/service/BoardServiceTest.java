package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.dto.BoardDTO;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Log4j2
public class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Test
    @DisplayName("Register Board")
    public void testRegister() {

        BoardDTO boardDTO = BoardDTO.builder()
                .title("board title")
                .content("board content")
                .writer("방승일")
                .email("test@gmail.com")
                .build();

        Long id = boardService.register(boardDTO);

        log.info(id);
    }


    @Test
    @DisplayName("Read Board")
    public void testRead() {

        Long id = 1L;

        Board board = boardService.read(id);

        log.info("board: " + board);
    }

    @Test
    @DisplayName("Modify Board")
    public void testModify() {
        BoardDTO boardDTO = BoardDTO.builder()
                .id(1L)
                .title("updated title")
                .content("updated content")
                .email("test@gmail.com")
                .build();

        Board board = boardService.modify(boardDTO);

        log.info("Updated Board : " + board);
    }

    @Test
    @DisplayName("Delete Board")
    public void testDelete() {
        Long id = 2L;

        boardService.remove(id);
    }

    @Test
    @DisplayName("readAll Board")
    public void testReadAll() {
        String email = "seungilbang@khu.ac.kr";

        List<Board> boards = boardService.readAll(email);

        boards.forEach(log::info);
    }



}

