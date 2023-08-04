package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.likes.Likes;
import com.web.app.domain.member.Member;
import com.web.app.dto.LikeRequestDTO;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.fixture.MemberFixtureFactory;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.LikesRepository;
import com.web.app.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class LikesServiceTest {

    @InjectMocks
    private LikesServiceImpl likesService;

    @Mock
    private LikesRepository likesRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BoardRepository boardRepository;

    @DisplayName("특정 유저의 특정 게시글을 좋아요 할 수 있다.")
    @Test
    public void postLike() {
        // given
        Board board = BoardFixtureFactory.createById(1L);
        Member member = MemberFixtureFactory.create();


        LikeRequestDTO likeRequestDTO = new LikeRequestDTO(board.getId(), member.getEmail());

        doReturn(Optional.of(member))
                .when(memberRepository)
                .findById(anyString());

        doReturn(Optional.of(board))
                .when(boardRepository)
                .findById(anyLong());

        // when
        likesService.postLike(likeRequestDTO);

        // then
        verify(likesRepository, times(1)).save(any(Likes.class));
        assertThatCode(() -> likesService.postLike(likeRequestDTO))
                .doesNotThrowAnyException();
    }

    // TODO: 동일한 게시글에 중복으로 좋아요를 할 수 없다.
    @DisplayName("동일한 게시글에 중복으로 좋아요를 할 수 없다.")
    @Test
    public void duplicatePostLike() {
        // given
        Member savedMember = memberRepository.save(MemberFixtureFactory.create());
        Board savedBoard = boardRepository.save(BoardFixtureFactory.create());

        LikeRequestDTO likeRequestDTO = new LikeRequestDTO(savedBoard.getId(), savedMember.getEmail());

        // when
        likesService.postLike(likeRequestDTO);
        
        // then
        assertThatThrownBy(() -> likesService.postLike(likeRequestDTO))
                .isInstanceOf(IllegalAccessError.class)
                .hasMessage("해당 게시글은 이미 좋아요를 했습니다.");
    }
}