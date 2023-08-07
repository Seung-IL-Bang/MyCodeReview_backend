package com.web.app.repository;

import com.web.app.domain.board.Board;
import com.web.app.domain.likes.Likes;
import com.web.app.domain.member.Member;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.fixture.MemberFixtureFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class LikesRepositoryTest {

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        likesRepository.deleteAllInBatch();
        boardRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("특정 게시글에 대해 정상적으로 좋아요를 했다.")
    @Test
    public void postLike() {
        // given
        Member savedMember = memberRepository.save(MemberFixtureFactory.create());
        Board savedBoard = boardRepository.save(BoardFixtureFactory.create());

        Likes likes = Likes.builder()
                .member(savedMember)
                .board(savedBoard)
                .build();

        // when
        Likes save = likesRepository.save(likes);

        // then
        assertThat(save.getId()).isNotNull();
        assertThat(save.getBoard()).isEqualTo(savedBoard);
        assertThat(save.getMember()).isEqualTo(savedMember);
    }

    @DisplayName("특정 게시글에 대해 정상적으로 좋아요를 취소한다.")
    @Test
    void deleteLike() {
        // given
        Member savedMember = memberRepository.save(MemberFixtureFactory.create());
        Board savedBoard = boardRepository.save(BoardFixtureFactory.create());

        Likes likes = Likes.builder()
                .member(savedMember)
                .board(savedBoard)
                .build();

        Likes liked = likesRepository.save(likes);

        // when
        likesRepository.deleteById(liked.getId());

        // then
        assertThatThrownBy(() -> likesRepository.findById(liked.getId()).orElseThrow())
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No value present");
    }

    @DisplayName("특정 회원이 특정 게시글에 좋아요 여부를 알 수 있다.")
    @Test
    void isLiked() {
        // given
        Member savedMember1 = memberRepository.save(MemberFixtureFactory.create(1L));
        Member savedMember2 = memberRepository.save(MemberFixtureFactory.create(2L));
        Board savedBoard = boardRepository.save(BoardFixtureFactory.create(3L));

        Likes likes = Likes.builder()
                .member(savedMember1)
                .board(savedBoard)
                .build();

        likesRepository.save(likes);

        // when
        List<Long> isLiked1 = likesRepository.isLiked(savedBoard.getId(), savedMember1.getEmail());
        List<Long> isLiked2 = likesRepository.isLiked(savedBoard.getId(), savedMember2.getEmail());


        // then
        assertThat(isLiked1).hasSize(1);
        assertThat(isLiked2).hasSize(0);
    }

}