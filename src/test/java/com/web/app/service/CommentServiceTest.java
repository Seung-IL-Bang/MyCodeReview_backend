package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import com.web.app.dto.CommentRequestDTO;
import com.web.app.exception.BusinessLogicException;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.fixture.CommentFixtureFactory;
import com.web.app.fixture.MemberFixtureFactory;
import com.web.app.mediator.GetEmailFromJWT;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.CommentRepository;
import com.web.app.repository.MemberRepository;
import com.web.app.repository.ReplyRepository;
import com.web.app.util.JWTUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CommentServiceTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private GetEmailFromJWT getEmailFromJWT;

    @Autowired
    private JWTUtil jwtUtil;

    @DisplayName("회원은 특정 게시글에 댓글을 달 수 있다.")
    @Test
    public void registerComment() {
        // given
        Board board = BoardFixtureFactory.createById(1L);
        Member member = MemberFixtureFactory.create();
        Comment comment = CommentFixtureFactory.of(board, member);

        // why ??? given 은 왜 에러를 던지는지 doReturn 하고 비교
        doReturn(Optional.of(member))
                .when(memberRepository)
                .findById(anyString());
        doReturn(Optional.of(board))
                .when(boardRepository)
                .findById(anyLong());
        doReturn(comment)
                .when(commentRepository)
                .save(any(Comment.class));

        CommentRequestDTO commentRequestDTO = new CommentRequestDTO(null, "testComment", board.getId(), member.getEmail());

        // when // then

        assertThatCode(() -> commentService.register(commentRequestDTO))
                .doesNotThrowAnyException();
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @DisplayName("댓글 작성자는 본인의 댓글을 수정할 수 있다.")
    @Test
    void modifyComment() {
        // given
        Board board = BoardFixtureFactory.createById(1L);
        Member member = MemberFixtureFactory.create();

        doReturn(Optional.of(member))
                .when(memberRepository)
                .findById(anyString());
        doReturn(Optional.of(board))
                .when(boardRepository)
                .findById(anyLong());

        CommentRequestDTO commentRequestDTO = new CommentRequestDTO(1L, "newComment", board.getId(), member.getEmail());

        Comment comment = commentRequestDTO.toEntity(1L, board, member, "newComment");

        doReturn(Optional.of(comment))
                .when(commentRepository)
                .findById(anyLong());

        MockHttpServletRequest request = getRequestWithJWT(member.getName(), member.getEmail());
        doReturn(member.getEmail())
                .when(getEmailFromJWT)
                .execute(any(MockHttpServletRequest.class));

        // when // then
        assertThatCode(() -> commentService.update(commentRequestDTO, request))
                .doesNotThrowAnyException();
        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @DisplayName("작성자 이외의 회원이 댓글 수정을 하면 예외를 던진다.")
    @Test
    void modifyComment2() {
        // given
        Board board = BoardFixtureFactory.createById(1L);
        Member member1 = MemberFixtureFactory.create(1L);
        Member member2 = MemberFixtureFactory.create(2L);

        doReturn(Optional.of(member1))
                .when(memberRepository)
                .findById(anyString());
        doReturn(Optional.of(board))
                .when(boardRepository)
                .findById(anyLong());

        CommentRequestDTO commentRequestDTO = new CommentRequestDTO(1L, "newComment", board.getId(), member1.getEmail());

        Comment comment = commentRequestDTO.toEntity(1L, board, member1, "newComment");

        doReturn(Optional.of(comment))
                .when(commentRepository)
                .findById(anyLong());

        MockHttpServletRequest request = getRequestWithJWT(member2.getName(), member2.getEmail());
        doReturn(member2.getEmail())
                .when(getEmailFromJWT)
                .execute(any(MockHttpServletRequest.class));

        // when // then
        assertThatThrownBy(() -> commentService.update(commentRequestDTO, request))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessage("해당 작업은 작성자 본인만 수행할 수 있습니다.");
    }

    @DisplayName("댓글 작성자는 본인의 댓글을 삭제할 수 있다.")
    @Test
    void removeComment() {
        // given
        Board board = BoardFixtureFactory.createById(1L);
        Member member = MemberFixtureFactory.create();
        Comment comment = Comment
                .builder()
                .id(1L)
                .content("testDelete")
                .board(board)
                .member(member).build();

        doReturn(Optional.of(comment))
                .when(commentRepository)
                .findById(anyLong());

        MockHttpServletRequest request = getRequestWithJWT(member.getName(), member.getEmail());
        doReturn(member.getEmail())
                .when(getEmailFromJWT)
                .execute(any(MockHttpServletRequest.class));

        // when // then
        assertThatCode(() -> commentService.remove(1L, request))
                .doesNotThrowAnyException();
        verify(commentRepository, times(1)).findById(anyLong());
        verify(replyRepository, times(1)).deleteRepliesByCommentIs(any(Comment.class));
        verify(commentRepository, times(1)).delete(any(Comment.class));
    }

    @DisplayName("작성자 이외의 회원이 댓글 삭제를 하면 예외를 던진다.")
    @Test
    void removeComment2() {
        // given
        Board board = BoardFixtureFactory.createById(1L);
        Member member1 = MemberFixtureFactory.create(1L);
        Member member2 = MemberFixtureFactory.create(2L);
        Comment comment = Comment
                .builder()
                .id(1L)
                .content("testDelete")
                .board(board)
                .member(member1).build();

        doReturn(Optional.of(comment))
                .when(commentRepository)
                .findById(anyLong());

        MockHttpServletRequest request = getRequestWithJWT(member2.getName(), member2.getEmail());
        doReturn(member2.getEmail())
                .when(getEmailFromJWT)
                .execute(any(MockHttpServletRequest.class));

        // when // then
        assertThatThrownBy(() -> commentService.remove(comment.getId(), request))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessage("해당 작업은 작성자 본인만 수행할 수 있습니다.");
    }


    private MockHttpServletRequest getRequestWithJWT(String writer, String email) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + jwtUtil.generateToken(Map.of("email", email, "name", writer), 1));
        return request;
    }

}