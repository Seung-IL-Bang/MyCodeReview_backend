package com.web.app.controller;

import com.web.app.dto.LikeRequestDTO;
import com.web.app.service.LikesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;

    @PostMapping("/auth/like")
    public ResponseEntity postLike(@Valid @RequestBody LikeRequestDTO likeRequestDTO) {
        likesService.postLike(likeRequestDTO);
        return new ResponseEntity("Liked " + likeRequestDTO.getBoardId() + " board", HttpStatus.OK);
    }
}
