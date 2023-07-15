package com.web.app.service;

import com.web.app.domain.review.Review;
import com.web.app.dto.ReviewRequestDTO;
import com.web.app.dto.ReviewResponseDTO;

public interface ReviewService {

    Long register(Review review, Long boardId);

    ReviewResponseDTO read(Long id);


    Review modify(ReviewRequestDTO reviewRequestDTO , Long id);

    void remove(Long id);
}
