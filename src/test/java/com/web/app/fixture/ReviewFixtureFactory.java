package com.web.app.fixture;

import com.web.app.domain.board.Board;
import com.web.app.domain.review.Review;
import com.web.app.dto.ReviewRequestDTO;
import com.web.app.dto.ReviewResponseDTO;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

public class ReviewFixtureFactory {

    public static Review create() {
        EasyRandomParameters param = new EasyRandomParameters();
        param.stringLengthRange(3, 10);
        return new EasyRandom(param).nextObject(Review.class);
    }

    public static ReviewRequestDTO createRequestDTO() {
        EasyRandomParameters param = new EasyRandomParameters();
        param.stringLengthRange(3, 10);
        return new EasyRandom(param).nextObject(ReviewRequestDTO.class);
    }

    public static ReviewRequestDTO createRequestDTO(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.stringLengthRange(3, 10);
        return new EasyRandom(param).nextObject(ReviewRequestDTO.class);
    }

    public static ReviewResponseDTO createResponseDTO() {
        EasyRandomParameters param = new EasyRandomParameters();
        param.stringLengthRange(3, 10);
        return new EasyRandom(param).nextObject(ReviewResponseDTO.class);
    }

    public static ReviewResponseDTO createResponseDTO(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.stringLengthRange(3, 10);
        return new EasyRandom(param).nextObject(ReviewResponseDTO.class);
    }


    public static Review of(Board board) {
        Review review = create();
        return Review.builder()
                .id(review.getId())
                .subTitle(review.getSubTitle())
                .content(review.getContent())
                .board(board)
                .build();
    }



}
