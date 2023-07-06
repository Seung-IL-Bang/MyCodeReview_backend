package com.web.app.service;

import com.web.app.domain.review.Review;
import com.web.app.dto.ReviewDTO;

public interface ReviewService {

    Long register(ReviewDTO reviewDTO);

    ReviewDTO read(Long id);


}
