package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.dto.BoardRequestDTO;
import com.web.app.dto.BoardResponseDTO;
import com.web.app.repository.BoardRepository;
import com.web.app.util.JWTUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Log4j2
public class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @AfterEach
    void tearDown() {
        boardRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자가 입력한 데이터로 게시글을 작성한다.")
    public void testRegister() {
        // given
        String title = "board title";
        String content = "board content";
        String writer = "방승일";
        String email = "test@gmail.com";
        BoardRequestDTO boardRequestDTO = createBoardDTO(email, title, content, writer);

        // when
        Long id = boardService.register(boardRequestDTO);

        // then
        assertThat(id).isNotNull();
    }


    @Test
    @DisplayName("게시글 Id로 해당 게시글을 조회한다.")
    public void testRead() {
        // given
        String title = "board title";
        String content = "board content";
        String writer = "방승일";
        String email = "test@gmail.com";
        Board board = createBoard(title, content, writer, email);

        Long id = boardRepository.save(board).getId();

        // when
        BoardResponseDTO read = boardService.read(id);

        // then
        assertThat(read.getId()).isEqualTo(id);
        assertThat(read.getTitle()).isEqualTo(title);
        assertThat(read.getContent()).isEqualTo(content);
        assertThat(read.getWriter()).isEqualTo(writer);
        assertThat(read.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("게시글 수정은 작성자만 가능합니다.")
    public void testModify() {
        // given
        String title = "board title";
        String content = "board content";
        String writer = "방승일";
        String email = "test@gmail.com";
        Board board = createBoard(title, content, writer, email);
        Long id = boardRepository.save(board).getId();

        String updated_title = "updated title";
        String updated_content = "updated content";
        BoardRequestDTO newBoardRequestDTO = createBoardDTO(email, updated_title, updated_content, writer);

        MockHttpServletRequest request = getRequestWithJWT(writer, email);

        // when
        Board newBoard = boardService.modify(request, id, newBoardRequestDTO);

        // then
        assertThat(newBoard.getTitle()).isEqualTo(updated_title);
        assertThat(newBoard.getContent()).isEqualTo(updated_content);
        assertThat(newBoard.getEmail()).isEqualTo(email);
    }


    @Test
    @DisplayName("게시글 삭제는 작성자만 가능합니다.")
    public void testDelete() {
        // given
        String title = "board title";
        String content = "board content";
        String writer = "방승일";
        String email = "test@gmail.com";
        Board board = createBoard(title, content, writer, email);
        Long id = boardRepository.save(board).getId();

        MockHttpServletRequest request = getRequestWithJWT(writer, email);

        // when
        boardService.remove(request, id);

        // then
        assertThatThrownBy(() -> boardService.read(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No value present");
    }

    @Test
    @DisplayName("회원 본인이 작성한 모든 게시글을 조회합니다.")
    public void testReadAll() {
        // given
        String writer = "방승일";
        String email = "test@gmail.com";
        Board board1 = createBoard("title1", "content1", writer, email);
        Board board2 = createBoard("title2", "content2", writer, email);
        Board board3 = createBoard("title3", "content3", writer, email);

        boardRepository.saveAll(List.of(board1, board2, board3));

        // when
        List<Board> boards = boardService.readAll(email);

        // then
        assertThat(boards).hasSize(3);
        assertThat(boards.get(0).getEmail()).isEqualTo(email);
    }

    private static Board createBoard(String title, String content, String writer, String email) {
        return Board.builder()
                .title(title)
                .content(content)
                .writer(writer)
                .email(email)
                .build();
    }

    private static BoardRequestDTO createBoardDTO(String email, String title, String content, String writer) {
        return BoardRequestDTO.builder()
                .title(title)
                .content(content)
                .writer(writer)
                .email(email)
                .build();
    }

    private MockHttpServletRequest getRequestWithJWT(String writer, String email) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + jwtUtil.generateToken(Map.of("email", email, "name", writer), 1));
        return request;
    }




}

