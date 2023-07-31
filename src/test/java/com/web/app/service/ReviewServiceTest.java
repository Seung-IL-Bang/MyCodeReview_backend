package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.review.Review;
import com.web.app.dto.ReviewResponseDTO;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.ReviewRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional(readOnly = true)
@SpringBootTest
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BoardRepository boardRepository;

    @DisplayName("서브 리뷰 ID 를 통해 해당 서브 리뷰를 조회 한다.")
    @Test
    public void read() {
        // given
        Board board = BoardFixtureFactory.create();
        Board savedBoard = boardRepository.save(board);

        Review review = Review.builder()
                .subTitle("subTitle")
                .content("content sub")
                .board(savedBoard)
                .build();
        Review savedReview = reviewRepository.save(review);

        // when
        ReviewResponseDTO read = reviewService.read(savedReview.getId());

        // then
        assertThat(read.getContent()).isEqualTo(review.getContent());
        assertThat(read.getSubTitle()).isEqualTo(review.getSubTitle());

    }
}