package com.web.app.util;

import com.web.app.IntegrationTestSupport;
import com.web.app.domain.board.Board;
import com.web.app.domain.member.Member;
import com.web.app.dto.LikeRequestDTO;
import com.web.app.exception.BusinessLogicException;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.fixture.MemberFixtureFactory;
import com.web.app.proxy.LikesUseCase;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.LikesRepository;
import com.web.app.repository.MemberRepository;
import com.web.app.service.LikesService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

public class LikeOptimisticLockTest extends IntegrationTestSupport {

    @Autowired
    private LikesUseCase likesUseCase;

    @Autowired
    private LikesService likesService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LikesRepository likesRepository;

    @AfterEach
    void tearDown() {
        likesRepository.deleteAllInBatch();
        boardRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    private final int NUMBER_OF_THREADS = 4;

    @DisplayName("좋아요 기능의 동시성 이슈 발생 시 낙관적 락킹 실패 예외가 던져진다.")
    @Test
    void optimisticLockingLikePost1() {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        Board savedBoard = boardRepository.save(BoardFixtureFactory.create());
        List<Member> savedMembers = IntStream.rangeClosed(1, NUMBER_OF_THREADS)
                .mapToObj(i -> memberRepository.save(MemberFixtureFactory.create((long) i)))
                .collect(Collectors.toList());
        List<LikeRequestDTO> likeRequestDTOs = savedMembers.stream()
                .map(member -> new LikeRequestDTO(savedBoard.getId(), member.getEmail()))
                .collect(Collectors.toList());

        List<Future<?>> futures = likeRequestDTOs.stream()
                .map(likeRequestDTO -> executorService.submit(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        likesService.postLike(likeRequestDTO);
                    } catch (BusinessLogicException | InterruptedException e) {
                        throw new RuntimeException(e.getCause());
                    }
                }))
                .collect(Collectors.toList());

        // when // then
        assertThatThrownBy(() -> {
            futures.forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) { // Future 인터페이스의 get() 메서드는 예외 처리시 본래 예외 'cause' 를 ExecutionException 으로 캡슐화하여 통합 처리한다.
                    throw new RuntimeException(e.getCause()); // getCause() 메서드로 본래 예외인 OptimisticLockingFailureException 추출
                }
            });
        })
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(OptimisticLockingFailureException.class);
    }

    @DisplayName("좋아요 기능의 동시성 이슈 발생 시 자동 재요청 로직이 성공적으로 작동한다.")
    @Test
    void optimisticLockingLikePost2() {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        Board savedBoard = boardRepository.save(BoardFixtureFactory.create());
        List<Member> savedMembers = IntStream.rangeClosed(1, NUMBER_OF_THREADS)
                .mapToObj(i -> memberRepository.save(MemberFixtureFactory.create((long) i)))
                .collect(Collectors.toList());
        List<LikeRequestDTO> likeRequestDTOs = savedMembers.stream()
                .map(member -> new LikeRequestDTO(savedBoard.getId(), member.getEmail()))
                .collect(Collectors.toList());

        List<Future<?>> futures = likeRequestDTOs.stream()
                .map(likeRequestDTO -> executorService.submit(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        likesUseCase.executePost(likeRequestDTO);
                    } catch (BusinessLogicException | InterruptedException e) {
                        throw new RuntimeException(e.getCause());
                    }
                }))
                .collect(Collectors.toList());


        // when // then
        assertThatCode(() -> {
            futures.forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e.getCause());
                }
            });
        }).doesNotThrowAnyException();

        Board LikedBoard = boardRepository.findById(savedBoard.getId()).orElseThrow();
        assertThat(LikedBoard.getLikeCount()).isEqualTo(NUMBER_OF_THREADS);
    }

    @DisplayName("좋아요 취소 기능의 동시성 이슈 발생 시 낙관적 락킹 실패 예외가 던져진다.")
    @Test
    void optimisticLockingLikeDelete1() {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        Board savedBoard = boardRepository.save(BoardFixtureFactory.create());
        List<Member> savedMembers = IntStream.rangeClosed(1, NUMBER_OF_THREADS)
                .mapToObj(i -> memberRepository.save(MemberFixtureFactory.create((long) i)))
                .collect(Collectors.toList());
        List<LikeRequestDTO> likeRequestDTOs = savedMembers.stream()
                .map(member -> new LikeRequestDTO(savedBoard.getId(), member.getEmail()))
                .collect(Collectors.toList());

        likeRequestDTOs.stream().forEach(likeRequestDTO -> {
            try {
                likesUseCase.executePost(likeRequestDTO);
            } catch (BusinessLogicException e) {
                throw new RuntimeException(e);
            }
        });

        List<Future<?>> futures = likeRequestDTOs.stream()
                .map(likeRequestDTO -> executorService.submit(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        likesService.deleteLike(likeRequestDTO);
                    } catch (BusinessLogicException | InterruptedException e) {
                        throw new RuntimeException(e.getCause());
                    }
                }))
                .collect(Collectors.toList());

        // when // then
        assertThatThrownBy(() -> {
            futures.forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e.getCause());
                }
            });
        })
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(OptimisticLockingFailureException.class);
    }

    @DisplayName("좋아요 취소 기능의 동시성 이슈 발생 시 자동 재요청 로직이 성공적으로 작동한다.")
    @Test
    void optimisticLockingLikeDelete2() {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        Board savedBoard = boardRepository.save(BoardFixtureFactory.create());
        List<Member> savedMembers = IntStream.rangeClosed(1, NUMBER_OF_THREADS)
                .mapToObj(i -> memberRepository.save(MemberFixtureFactory.create((long) i)))
                .collect(Collectors.toList());
        List<LikeRequestDTO> likeRequestDTOs = savedMembers.stream()
                .map(member -> new LikeRequestDTO(savedBoard.getId(), member.getEmail()))
                .collect(Collectors.toList());

        likeRequestDTOs.stream().forEach(likeRequestDTO -> {
            try {
                likesUseCase.executePost(likeRequestDTO);
            } catch (BusinessLogicException e) {
                throw new RuntimeException(e);
            }
        });

        List<Future<?>> futures = likeRequestDTOs.stream()
                .map(likeRequestDTO -> executorService.submit(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        likesUseCase.executeDelete(likeRequestDTO);
                    } catch (BusinessLogicException | InterruptedException e) {
                        throw new RuntimeException(e.getCause());
                    }
                }))
                .collect(Collectors.toList());


        // when // then
        assertThatCode(() -> {
            futures.forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e.getCause());
                }
            });
        }).doesNotThrowAnyException();

        Board LikedBoard = boardRepository.findById(savedBoard.getId()).orElseThrow();
        assertThat(LikedBoard.getLikeCount()).isZero();
    }

}
