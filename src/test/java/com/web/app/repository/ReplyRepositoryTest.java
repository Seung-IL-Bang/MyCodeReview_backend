package com.web.app.repository;

import com.web.app.IntegrationTestSupport;
import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import com.web.app.domain.reply.Reply;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.fixture.CommentFixtureFactory;
import com.web.app.fixture.MemberFixtureFactory;
import com.web.app.fixture.ReplyFixtureFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


class ReplyRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @AfterEach
    void tearDown() {
        replyRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        boardRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }
    
    @DisplayName("답글을 작성할 수 있다.")
    @Test
    void saveReply() {
        // given
        Member member = MemberFixtureFactory.create();
        Member savedMember = memberRepository.save(member);

        Board board = BoardFixtureFactory.of(savedMember);
        Board savedBoard = boardRepository.save(board);

        Comment comment = CommentFixtureFactory.of(savedBoard, savedMember);
        Comment savedComment = commentRepository.save(comment);

        Reply reply = ReplyFixtureFactory.of(savedComment, savedMember);

        // when
        Reply saved = replyRepository.save(reply);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getContent()).isEqualTo(reply.getContent());
    }

    @DisplayName("특정 답글을 조회할 수 있다.")
    @Test
    void findById_Reply() {
        // given
        Member member = MemberFixtureFactory.create();
        Member savedMember = memberRepository.save(member);

        Board board = BoardFixtureFactory.of(savedMember);
        Board savedBoard = boardRepository.save(board);

        Comment comment = CommentFixtureFactory.of(savedBoard, savedMember);
        Comment savedComment = commentRepository.save(comment);

        Reply reply = ReplyFixtureFactory.of(savedComment, savedMember);

        Reply savedReply = replyRepository.save(reply);

        // when
        Reply findOne = replyRepository.findById(savedReply.getId()).orElseThrow();

        // then
        assertThat(findOne).isNotNull();
        assertThat(findOne.getContent()).isEqualTo(reply.getContent());
        assertThat(findOne.getId()).isEqualTo(savedReply.getId());
    }

    @DisplayName("특정 답글을 수정할 수 있다.")
    @Test
    void updateReply() {
        // given
        Member member = MemberFixtureFactory.create();
        Member savedMember = memberRepository.save(member);

        Board board = BoardFixtureFactory.of(savedMember);
        Board savedBoard = boardRepository.save(board);

        Comment comment = CommentFixtureFactory.of(savedBoard, savedMember);
        Comment savedComment = commentRepository.save(comment);

        Reply oldReply = ReplyFixtureFactory.of(savedComment, savedMember, 1L);
        Reply newReply = ReplyFixtureFactory.of(savedComment, savedMember, 2L);

        Reply savedReply = replyRepository.save(oldReply);

        // when
        savedReply.modify(newReply.getContent());
        Reply updatedReply = replyRepository.save(savedReply);

        // then
        assertThat(updatedReply.getContent()).isEqualTo(newReply.getContent());
        assertThat(updatedReply.getId()).isEqualTo(savedReply.getId());
    }

    @DisplayName("특정 답글을 삭제할 수 있다.")
    @Test
    void deleteReply() {
        // given
        Member member = MemberFixtureFactory.create();
        Member savedMember = memberRepository.save(member);

        Board board = BoardFixtureFactory.of(savedMember);
        Board savedBoard = boardRepository.save(board);

        Comment comment = CommentFixtureFactory.of(savedBoard, savedMember);
        Comment savedComment = commentRepository.save(comment);

        Reply reply = ReplyFixtureFactory.of(savedComment, savedMember);

        Reply savedReply = replyRepository.save(reply);

        // when
        replyRepository.delete(savedReply);

        // then
        assertThatThrownBy(() -> replyRepository.findById(savedReply.getId()).orElseThrow())
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("No value present");
    }

    @DisplayName("특정 댓글에 달려있는 모든 답글을 조회할 수 있다.")
    @Test
    void findAllByComment() {
        // given
        Member member = MemberFixtureFactory.create();
        Member savedMember = memberRepository.save(member);

        Board board = BoardFixtureFactory.of(savedMember);
        Board savedBoard = boardRepository.save(board);

        Comment comment = CommentFixtureFactory.of(savedBoard, savedMember);
        Comment savedComment = commentRepository.save(comment);

        Reply reply1 = ReplyFixtureFactory.of(savedComment, savedMember, 1L);
        Reply reply2 = ReplyFixtureFactory.of(savedComment, savedMember, 2L);
        Reply reply3 = ReplyFixtureFactory.of(savedComment, savedMember, 3L);

        List<Reply> savedReplies = replyRepository.saveAll(List.of(reply1, reply2, reply3));

        // when
        List<Reply> findReplyList = replyRepository.findAllByComment_IdOrderByCreatedAtAsc(savedComment.getId());


        // then
        assertThat(findReplyList.size()).isEqualTo(savedReplies.size());
        assertThat(findReplyList)
                .extracting("id")
                .containsExactlyInAnyOrder(
                        savedReplies.get(0).getId(),
                        savedReplies.get(1).getId(),
                        savedReplies.get(2).getId());

    }
}