package com.web.app.service;

import com.web.app.domain.review.Review;
import com.web.app.dto.ReviewRequestDTO;
import com.web.app.dto.ReviewResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface ReviewService {

    Long register(HttpServletRequest request, Review review, Long boardId);

    ReviewResponseDTO read(Long id, HttpServletRequest request);


    Review modify(HttpServletRequest request, ReviewRequestDTO reviewRequestDTO , Long id);

    void remove(HttpServletRequest request, Long id);
}
