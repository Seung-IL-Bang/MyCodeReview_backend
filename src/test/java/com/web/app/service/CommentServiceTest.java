package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import com.web.app.dto.CommentRequestDTO;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.fixture.MemberFixtureFactory;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.CommentRepository;
import com.web.app.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
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
    private BoardRepository boardRepository;

    @Mock
    private CommentRepository commentRepository;

    @DisplayName("특정 회원의 게시글에 댓글을 달 수 있다.")
    @Test
    public void registerComment() {
        // given
        Board board = BoardFixtureFactory.createById(1L);
        Member member = MemberFixtureFactory.create();

        doReturn(Optional.of(member))
                .when(memberRepository)
                .findById(anyString());
        doReturn(Optional.of(board))
                .when(boardRepository)
                .findById(anyLong());

        CommentRequestDTO commentRequestDTO = new CommentRequestDTO("testComment", board.getId(), member.getEmail());

        // when // then
        assertThatCode(() -> commentService.register(commentRequestDTO))
                .doesNotThrowAnyException();
        verify(commentRepository, times(1)).save(any(Comment.class));
    }


}