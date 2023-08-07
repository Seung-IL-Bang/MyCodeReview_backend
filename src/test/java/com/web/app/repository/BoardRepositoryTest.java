package com.web.app.repository;

import com.web.app.domain.board.Board;
import com.web.app.dto.PageRequestDTO;
import com.web.app.fixture.BoardFixtureFactory;
import org.assertj.core.groups.Tuple;
import org.hibernate.annotations.BatchSize;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @DisplayName("메인 페이지에서 권한 없이도 누구나 페이징 처리된 최신 게시글 목록을 조회 할 수 있다.")
    @Test
    void findPublicPagingList() {
        //given
        Board board1 = BoardFixtureFactory.create(); // 페이징 미포함 예정
        Board board2 = BoardFixtureFactory.create(); // 페이징 미포함 예정
        Board board3 = BoardFixtureFactory.create();
        Board board4 = BoardFixtureFactory.create();
        Board board5 = BoardFixtureFactory.create();
        Board board6 = BoardFixtureFactory.create();
        Board board7 = BoardFixtureFactory.create();
        Board board8 = BoardFixtureFactory.create();
        Board board9 = BoardFixtureFactory.create();
        Board board10 = BoardFixtureFactory.create();

        List<Board> boards = boardRepository.saveAll(List.of(board1, board2, board3, board4, board5, board6, board7, board8, board9, board10));

        PageRequestDTO pageRequestDTO = new PageRequestDTO(); // 1 page & 8 size

        Pageable pageable = pageRequestDTO.getPageable("createdAt"); // ORDER BY createdAt DESC

        // when
        Page<Board> all = boardRepository.findAll(pageable);

        // then
        List<Board> pagingBoards = all.getContent();
        assertThat(pagingBoards).hasSize(8)
                .extracting("id", "title", "content")
                .containsExactlyInAnyOrder(
                        tuple(boards.get(9).getId(), board10.getTitle(), board10.getContent()),
                        tuple(boards.get(8).getId(), board9.getTitle(), board9.getContent()),
                        tuple(boards.get(7).getId(), board8.getTitle(), board8.getContent()),
                        tuple(boards.get(6).getId(), board7.getTitle(), board7.getContent()),
                        tuple(boards.get(5).getId(), board6.getTitle(), board6.getContent()),
                        tuple(boards.get(4).getId(), board5.getTitle(), board5.getContent()),
                        tuple(boards.get(3).getId(), board4.getTitle(), board4.getContent()),
                        tuple(boards.get(2).getId(), board3.getTitle(), board3.getContent())
                ).doesNotContain(
                        tuple(boards.get(1).getId(), board2.getTitle(), board2.getContent()),
                        tuple(boards.get(0).getId(), board1.getTitle(), board1.getContent())
                );
    }


}