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

}