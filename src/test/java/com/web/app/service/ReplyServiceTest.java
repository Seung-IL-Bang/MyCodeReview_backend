package com.web.app.service;

import com.web.app.IntegrationTestSupport;
import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import com.web.app.domain.reply.Reply;
import com.web.app.dto.ReplyRequestDTO;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.fixture.CommentFixtureFactory;
import com.web.app.fixture.MemberFixtureFactory;
import com.web.app.fixture.ReplyFixtureFactory;
import com.web.app.mediator.GetEmailFromJWT;
import com.web.app.repository.CommentRepository;
import com.web.app.repository.MemberRepository;
import com.web.app.repository.ReplyRepository;
import com.web.app.util.JWTUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ReplyServiceTest extends IntegrationTestSupport {

    @InjectMocks
    private ReplyServiceImpl replyService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private GetEmailFromJWT getEmailFromJWT;

    @Autowired
    private JWTUtil jwtUtil;


    @DisplayName("로그인한 유저가 특정 댓글에 답글을 달 수 있다.")
    @Test
    void registerReply() {
        // given
        Board board = BoardFixtureFactory.createById(1L);
        Member member = MemberFixtureFactory.create();
        Comment comment = CommentFixtureFactory.createById(board, member, 1L);
        Reply reply = ReplyFixtureFactory.of(comment, member);

        doReturn(Optional.of(member))
                .when(memberRepository)
                .findById(anyString());
        doReturn(Optional.of(comment))
                .when(commentRepository)
                .findById(anyLong());
        doReturn(reply)
                .when(replyRepository)
                .save(any(Reply.class));

        ReplyRequestDTO replyRequestDTO = new ReplyRequestDTO(null, comment.getId(), member.getEmail(), "testReply");

        // when // then
        assertThatCode(() -> replyService.register(replyRequestDTO))
                .doesNotThrowAnyException();
        verify(replyRepository, times(1)).save(any(Reply.class));
    }
    
    @DisplayName("로그인한 유저가 특정 댓글에 본인이 작성한 답글을 수정할 수 있다.")
    @Test
    void modifyReply() {
        // given
        Board board = BoardFixtureFactory.createById(1L);
        Member member = MemberFixtureFactory.create();
        Comment comment = CommentFixtureFactory.createById(board, member, 1L);


        doReturn(Optional.of(member))
                .when(memberRepository)
                .findById(anyString());
        doReturn(Optional.of(comment))
                .when(commentRepository)
                .findById(anyLong());

        ReplyRequestDTO replyRequestDTO = new ReplyRequestDTO(1L, comment.getId(), member.getEmail(), "testReply");
        Reply reply = replyRequestDTO.toEntity(replyRequestDTO.getId(), member, comment);

        doReturn(Optional.of(reply))
                .when(replyRepository)
                .findById(anyLong());

        MockHttpServletRequest request = getRequestWithJWT(member.getName(), member.getEmail());
        doReturn(member.getEmail())
                .when(getEmailFromJWT)
                .execute(any(MockHttpServletRequest.class));

        // when // then
        assertThatCode(() -> replyService.update(replyRequestDTO, request))
                .doesNotThrowAnyException();
        verify(replyRepository, times(1)).findById(anyLong());
        verify(replyRepository, times(1)).save(any(Reply.class));
    }
    
    @DisplayName("로그인한 유저가 특정 댓글에 본인 답글을 삭제할 수 있다.")
    @Test
    void removeReply() {
        // given
        Board board = BoardFixtureFactory.createById(1L);
        Member member = MemberFixtureFactory.create();
        Comment comment = CommentFixtureFactory.createById(board, member, 1L);

        Reply replyFixture = ReplyFixtureFactory.of(comment, member);
        Reply reply = Reply.builder()
                .id(1L)
                .content(replyFixture.getContent())
                .comment(replyFixture.getComment())
                .member(replyFixture.getMember())
                .build();

        doReturn(Optional.of(reply))
                .when(replyRepository)
                .findById(anyLong());

        MockHttpServletRequest request = getRequestWithJWT(member.getName(), member.getEmail());
        doReturn(member.getEmail())
                .when(getEmailFromJWT)
                .execute(any(MockHttpServletRequest.class));
        
        // when // then
        assertThatCode(() -> replyService.remove(reply.getId(), request))
                .doesNotThrowAnyException();
        verify(replyRepository, times(1)).findById(anyLong());
        verify(replyRepository, times(1)).delete(any(Reply.class));
    }

    private MockHttpServletRequest getRequestWithJWT(String writer, String email) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + jwtUtil.generateToken(Map.of("email", email, "name", writer), 1));
        return request;
    }
}
