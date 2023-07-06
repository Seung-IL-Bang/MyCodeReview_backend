package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.review.Review;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.ReviewRepository;
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

    @DisplayName("서브 리뷰 ID 를 통해 해당 서브 리뷰를 조회한다.")
    @Test
    public void read() {
        // given
        Board board = Board.builder()
                .content("content test")
                .title("title test")
                .build();
        Board savedBoard = boardRepository.save(board);

        Review review = Review.builder()
                .subTitle("subTitle")
                .content("content sub")
                .board(savedBoard)
                .build();
        reviewRepository.save(review);

        // when
        Review read = reviewService.read(review.getId());

        // then
        assertThat(read.getBoard().getId()).isEqualTo(savedBoard.getId());
        assertThat(read.getContent()).isEqualTo("content sub");
        assertThat(read.getSubTitle()).isEqualTo("subTitle");

    }
}