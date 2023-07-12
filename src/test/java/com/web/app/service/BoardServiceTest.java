package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.dto.BoardRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Log4j2
public class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Test
    @DisplayName("Register Board")
    public void testRegister() {

        BoardRequestDTO boardRequestDTO = BoardRequestDTO.builder()
                .title("board title")
                .content("board content")
                .writer("방승일")
                .email("test@gmail.com")
                .build();

        Long id = boardService.register(boardRequestDTO);

        log.info(id);
    }


    @Test
    @DisplayName("Read Board")
    public void testRead() {

        Long id = 1L;

        BoardRequestDTO read = boardService.read(id);

        log.info("board: " + read);
    }

    @Test
    @DisplayName("Modify Board")
    public void testModify(HttpServletRequest request) {
        BoardRequestDTO boardRequestDTO = BoardRequestDTO.builder()
                .id(1L)
                .title("updated title")
                .content("updated content")
                .email("test@gmail.com")
                .build();

        Board board = boardService.modify(request, 1L, boardRequestDTO);

        log.info("Updated Board : " + board);
    }

    @Test
    @DisplayName("Delete Board")
    public void testDelete(HttpServletRequest request) {
        Long id = 2L;

        boardService.remove(request, id);
    }

    @Test
    @DisplayName("readAll Board")
    public void testReadAll() {
        String email = "seungilbang@khu.ac.kr";

        List<Board> boards = boardService.readAll(email);

        boards.forEach(log::info);
    }



}

