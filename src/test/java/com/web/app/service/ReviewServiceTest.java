package com.web.app.service;

import com.web.app.IntegrationTestSupport;
import com.web.app.domain.board.Board;
import com.web.app.domain.review.Review;
import com.web.app.dto.ReviewResponseDTO;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.ReviewRepository;
import com.web.app.util.JWTUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional(readOnly = true)
class ReviewServiceTest extends IntegrationTestSupport {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private JWTUtil jwtUtil;

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

        MockHttpServletRequest requestWithJWT = getRequestWithJWT(board.getWriter(), board.getEmail());

        // when
        ReviewResponseDTO read = reviewService.read(savedReview.getId(), requestWithJWT);

        // then
        assertThat(read.getContent()).isEqualTo(review.getContent());
        assertThat(read.getSubTitle()).isEqualTo(review.getSubTitle());

    }

    private MockHttpServletRequest getRequestWithJWT(String writer, String email) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + jwtUtil.generateToken(Map.of("email", email, "name", writer), 1));
        return request;
    }
}