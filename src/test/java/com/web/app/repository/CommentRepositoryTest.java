package com.web.app.repository;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.fixture.CommentFixtureFactory;
import com.web.app.fixture.MemberFixtureFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @AfterEach
    void tearDown() {
        commentRepository.deleteAllInBatch();
        boardRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("특정 게시글에 댓글을 달 수 있다.")
    @Test
    public void saveComment() {
        // given
        Member savedMember = memberRepository.save(MemberFixtureFactory.create());
        Board savedBoard = boardRepository.save(BoardFixtureFactory.create());
        Comment comment = CommentFixtureFactory.of(savedBoard, savedMember);

        // when
        Comment saved = commentRepository.save(comment);

        // then
        assertThat(saved).isNotNull();
    }

    @DisplayName("특정 게시글의 댓글 목록을 조회할 수 있다.")
    @Test
    public void findAllByBoardIs() {
        // given
        Member savedMember = memberRepository.save(MemberFixtureFactory.create());
        Board savedBoard = boardRepository.save(BoardFixtureFactory.create());
        Comment comment1 = CommentFixtureFactory.of(savedBoard, savedMember, 1L);
        Comment comment2 = CommentFixtureFactory.of(savedBoard, savedMember, 2L);
        Comment comment3 = CommentFixtureFactory.of(savedBoard, savedMember, 3L);

        List<Comment> saveAll = commentRepository.saveAll(List.of(comment1, comment2, comment3));

        // when
        List<Comment> comments = commentRepository.findAllByBoardIsOrderByCreatedAtAsc(savedBoard);

        // then
        assertThat(comments).hasSize(3)
                .extracting("id")
                .containsExactlyInAnyOrder(saveAll.get(0).getId(), saveAll.get(1).getId(), saveAll.get(2).getId());
    }

    @DisplayName("특정 게시글에 달린 댓글을 수정할 수 있다.")
    @Test
    public void updateComment() {
        // given
        Member savedMember = memberRepository.save(MemberFixtureFactory.create());
        Board savedBoard = boardRepository.save(BoardFixtureFactory.create());
        Comment oldComment = CommentFixtureFactory.of(savedBoard, savedMember, 1L);
        Comment newComment = CommentFixtureFactory.of(savedBoard, savedMember, 2L);

        Comment savedOld = commentRepository.save(oldComment);

        // when
        savedOld.modify(newComment.getContent());
        Comment savedNew = commentRepository.save(savedOld);

        // then
        assertThat(savedNew).isNotNull();
        assertThat(savedNew.getContent()).isEqualTo(newComment.getContent());
    }

    @DisplayName("특정 게시글의 댓글을 삭제할 수 있다.")
    @Test
    public void deleteComment() {
        // given
        Member savedMember = memberRepository.save(MemberFixtureFactory.create());
        Board savedBoard = boardRepository.save(BoardFixtureFactory.create());
        Comment comment = CommentFixtureFactory.of(savedBoard, savedMember);

        Comment savedComment = commentRepository.save(comment);

        // when
        commentRepository.delete(savedComment);

        // then
        assertThatThrownBy(() -> commentRepository.findById(savedComment.getId()).orElseThrow())
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No value present");
    }

}