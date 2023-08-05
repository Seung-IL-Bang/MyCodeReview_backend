package com.web.app.controller;

import com.web.app.dto.LikeRequestDTO;
import com.web.app.exception.BusinessLogicException;
import com.web.app.service.LikesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;

    @PostMapping("/auth/like")
    public ResponseEntity postLike(@Valid @RequestBody LikeRequestDTO likeRequestDTO) throws BusinessLogicException {
        likesService.postLike(likeRequestDTO);
        return new ResponseEntity("Liked " + likeRequestDTO.getBoardId() + " board", HttpStatus.OK);
    }

    @DeleteMapping("/auth/like")
    public ResponseEntity deleteLike(@Valid @RequestBody LikeRequestDTO likeRequestDTO) throws BusinessLogicException {
        likesService.deleteLike(likeRequestDTO);
        return new ResponseEntity("Deleted " + likeRequestDTO.getBoardId() + " board's Like", HttpStatus.OK);
    }
}