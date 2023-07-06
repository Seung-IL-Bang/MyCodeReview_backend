package com.web.app.controller;

import com.web.app.domain.review.Review;
import com.web.app.dto.ReviewDTO;
import com.web.app.service.ReviewService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/board/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final ModelMapper modelMapper;
    
    @GetMapping("/{id}")
    public ResponseEntity getReview(@PathVariable("id") @Positive Long id) {

        ReviewDTO review = reviewService.read(id);

        ReviewDTO response = modelMapper.map(review, ReviewDTO.class);

        return new ResponseEntity(response, HttpStatus.OK);
    }
}
