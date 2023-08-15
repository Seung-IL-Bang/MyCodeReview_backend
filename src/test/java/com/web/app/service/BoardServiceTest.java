package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.likes.Likes;
import com.web.app.domain.member.Member;
import com.web.app.dto.BoardRequestDTO;
import com.web.app.dto.BoardResponseDTO;
import com.web.app.dto.PageRequestDTO;
import com.web.app.dto.PageResponseDTO;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.fixture.MemberFixtureFactory;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.CommentRepository;
import com.web.app.repository.LikesRepository;
import com.web.app.repository.MemberRepository;
import com.web.app.util.JWTUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
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

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentRepository commentRepository;

    @AfterEach
    void tearDown() {
        commentRepository.deleteAllInBatch(); // 외래키 참조 제약 위배 방지를 위한 cleaning
        likesRepository.deleteAllInBatch(); // 외래키 참조 제약 위배 방지를 위한 cleaning
        boardRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자가 입력한 데이터로 게시글을 작성한다.")
    public void testRegister() {
        // given
        BoardRequestDTO requestDTO = BoardFixtureFactory.createRequestDTO();

        // when
        Long id = boardService.register(requestDTO);

        // then
        assertThat(id).isNotNull();
    }


    @Test
    @DisplayName("게시글 Id로 해당 게시글을 조회한다.")
    public void testRead() {
        // given
        Board board = BoardFixtureFactory.create();
        Member member = MemberFixtureFactory.create();

        Long id = boardRepository.save(board).getId();

        // when
        BoardResponseDTO read = boardService.read(id, member.getEmail());

        // then
        assertThat(read.getId()).isEqualTo(id);
        assertThat(read.getTitle()).isEqualTo(board.getTitle());
        assertThat(read.getContent()).isEqualTo(board.getContent());
        assertThat(read.getWriter()).isEqualTo(board.getWriter());
        assertThat(read.getEmail()).isEqualTo(board.getEmail());
    }

    @Test
    @DisplayName("게시글 수정은 작성자만 가능합니다.")
    public void testModify() {
        // given
        Board board = BoardFixtureFactory.create(1L);
        Long id = boardRepository.save(board).getId();

        BoardRequestDTO updateRequestDTO = BoardFixtureFactory.createRequestDTO();

        MockHttpServletRequest request = getRequestWithJWT(board.getWriter(), board.getEmail());

        // when
        Board newBoard = boardService.modify(request, id, updateRequestDTO);

        // then
        assertThat(newBoard.getTitle()).isEqualTo(updateRequestDTO.getTitle());
        assertThat(newBoard.getContent()).isEqualTo(updateRequestDTO.getContent());
        assertThat(newBoard.getEmail()).isEqualTo(board.getEmail());
        assertThat(newBoard.getWriter()).isEqualTo(board.getWriter());
    }


    @Test
    @DisplayName("게시글 삭제는 작성자만 가능합니다.")
    public void testDelete() {
        // given
        Board board = BoardFixtureFactory.create();
        Member member = MemberFixtureFactory.create();
        Long id = boardRepository.save(board).getId();

        MockHttpServletRequest request = getRequestWithJWT(board.getWriter(), board.getEmail());

        // when
        boardService.remove(request, id);

        // then
        assertThatThrownBy(() -> boardService.read(id, member.getEmail()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 회원은 존재하지 않습니다.");
    }

    @Test
    @DisplayName("회원 본인이 작성한 모든 게시글을 조회합니다.")
    public void testReadAll() {
        // given
        Board board1 = BoardFixtureFactory.create();
        Board board2 = BoardFixtureFactory.create();
        Board board3 = BoardFixtureFactory.create();

        boardRepository.saveAll(List.of(board1, board2, board3));

        // when
        List<Board> boards = boardService.readAll(board1.getEmail());

        // then
        assertThat(boards).hasSize(3);
        assertThat(boards.get(0).getEmail()).isEqualTo(board1.getEmail());
    }

    @DisplayName("권한 없이도 누구나 페이징 처리된 최신 게시글 목록을 조회 한다.")
    @Test
    void readPublicAllWithPaging() {
        //given
        Board board1 = BoardFixtureFactory.create();
        Board board2 = BoardFixtureFactory.create();
        Board board3 = BoardFixtureFactory.create();

        List<Board> boards = boardRepository.saveAll(List.of(board1, board2, board3));

        PageRequestDTO pageRequestDTO = new PageRequestDTO();

        // when
        PageResponseDTO<BoardResponseDTO> responseDTO = boardService.readPublicAllWithPaging(pageRequestDTO);

        // then
        assertThat(responseDTO.getDtoList()).hasSize(3)
                .extracting("id", "title", "content")
                .containsExactlyInAnyOrder(
                        tuple(boards.get(0).getId(), board1.getTitle(), board1.getContent()),
                        tuple(boards.get(1).getId(), board2.getTitle(), board2.getContent()),
                        tuple(boards.get(2).getId(), board3.getTitle(), board3.getContent())
                );
        assertThat(responseDTO.getPage()).isEqualTo(1);
        assertThat(responseDTO.getSize()).isEqualTo(8);
    }

    @DisplayName("로그인한 유저의 좋아요 목록에 해당하는 게시글 목록을 페이징 처리하여 조회할 수 있다.")
    @Test
    void readByEmailLikeBoardsWithPaging() {
        // given
        Member member = MemberFixtureFactory.create();
        memberRepository.save(member);
        Board board1 = BoardFixtureFactory.create();
        Board board2 = BoardFixtureFactory.create();
        Board board3 = BoardFixtureFactory.create();
        List<Board> boards = boardRepository.saveAll(List.of(board1, board2, board3));

        Likes likes1 = Likes.builder().board(boards.get(0)).member(member).build();
        Likes likes2 = Likes.builder().board(boards.get(1)).member(member).build();
        Likes likes3 = Likes.builder().board(boards.get(2)).member(member).build();
        likesRepository.saveAll(List.of(likes1, likes2, likes3));

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();

        // when
        PageResponseDTO<BoardResponseDTO> pageResponseDTO = boardService.readByEmailLikeBoardsWithPaging(member.getEmail(), pageRequestDTO);

        // then
        assertThat(pageResponseDTO.getDtoList()).hasSize(3)
                .extracting("id")
                .containsExactlyInAnyOrder(boards.get(0).getId(), boards.get(1).getId(), boards.get(2).getId());
        assertThat(pageResponseDTO.getPage()).isEqualTo(1);
        assertThat(pageResponseDTO.getSize()).isEqualTo(8);
    }


    private MockHttpServletRequest getRequestWithJWT(String writer, String email) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + jwtUtil.generateToken(Map.of("email", email, "name", writer), 1));
        return request;
    }


}

