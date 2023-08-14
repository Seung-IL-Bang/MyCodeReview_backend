package com.web.app.controller;

import com.web.app.domain.review.Review;
import com.web.app.dto.ReviewRequestDTO;
import com.web.app.dto.ReviewResponseDTO;
import com.web.app.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;
    private final ModelMapper modelMapper;
    
    @GetMapping("/board/review/{id}")
    public ResponseEntity getReview(@PathVariable("id") @Positive Long id) {

        ReviewResponseDTO response = reviewService.read(id);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/auth/board/review/{board-id}")
    public ResponseEntity postReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO, @PathVariable("board-id") Long boardId) {

        Long registeredId = reviewService.register(modelMapper.map(reviewRequestDTO, Review.class), boardId);
        return new ResponseEntity(registeredId, HttpStatus.CREATED);
    }

    @PutMapping("/auth/board/review/{id}")
    public ResponseEntity putReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO, @PathVariable("id") Long id) {

        reviewService.modify(reviewRequestDTO, id);

        return new ResponseEntity("Updated Id: " + id + " Review", HttpStatus.OK);
    }

    @DeleteMapping("/auth/board/review/{id}")
    public ResponseEntity deleteReview(@PathVariable("id") Long id) {

        reviewService.remove(id);

        return new ResponseEntity("Deleted " + id + " Review", HttpStatus.OK);
    }
}
