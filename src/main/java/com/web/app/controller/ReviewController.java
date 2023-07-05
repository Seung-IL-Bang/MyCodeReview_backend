package com.web.app.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth/board/review")
public class ReviewController {

    @GetMapping("/{id}")
    public ResponseEntity getReview(@PathVariable("id") @Positive Long id) {


        return new ResponseEntity(0, HttpStatus.OK);
    }
}
