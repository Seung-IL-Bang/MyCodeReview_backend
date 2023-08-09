package com.web.app.util;

import com.web.app.domain.board.Board;
import com.web.app.domain.member.Member;
import com.web.app.dto.LikeRequestDTO;
import com.web.app.exception.BusinessLogicException;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.fixture.MemberFixtureFactory;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.MemberRepository;
import com.web.app.service.LikesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class LikeOptimisticLockTest {

    @Autowired
    private LikesService likesService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;


    @DisplayName("낙관적 락을 통한 좋아요 기능 동시성 제어")
    @Test
    void optimisticLockingLike() {
        // given
        int numberOfThreads = 3;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        Board savedBoard = boardRepository.save(BoardFixtureFactory.create());
        Member savedMember = memberRepository.save(MemberFixtureFactory.create());

        LikeRequestDTO likeRequestDTO = new LikeRequestDTO(savedBoard.getId(), savedMember.getEmail());

        Future<?> future1 = executorService.submit(() -> {
            try {
                likesService.postLike(likeRequestDTO);
            } catch (BusinessLogicException e) {
                throw new RuntimeException(e);
            }
        });
        Future<?> future2 = executorService.submit(() -> {
            try {
                likesService.postLike(likeRequestDTO);
            } catch (BusinessLogicException e) {
                throw new RuntimeException(e);
            }
        });
        Future<?> future3 = executorService.submit(() -> {
            try {
                likesService.postLike(likeRequestDTO);
            } catch (BusinessLogicException e) {
                throw new RuntimeException(e);
            }
        });

        // when // then
        assertThatThrownBy(() -> {
            future1.get();
            future2.get();
            future3.get();
        })
                .isInstanceOf(ExecutionException.class);
    }


}
