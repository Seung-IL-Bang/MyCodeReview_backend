package com.web.app.controller;

import com.web.app.domain.comment.Comment;
import com.web.app.dto.CommentRequestDTO;
import com.web.app.dto.CommentResponseDTO;
import com.web.app.exception.BusinessLogicException;
import com.web.app.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final ModelMapper modelMapper;

    @PostMapping("/auth/comment")
    public ResponseEntity postComment(@Valid @RequestBody CommentRequestDTO commentRequestDTO) {
        Comment comment = commentService.register(commentRequestDTO);
        CommentResponseDTO response = modelMapper.map(comment, CommentResponseDTO.class);
        return new ResponseEntity<>(response , HttpStatus.CREATED);
    }

    @PutMapping("/auth/comment")
    public ResponseEntity putComment(@Valid @RequestBody CommentRequestDTO commentRequestDTO, HttpServletRequest request) throws BusinessLogicException {
        commentService.update(commentRequestDTO, request);
        return new ResponseEntity<>("Updated Comment!", HttpStatus.OK);
    }

    @DeleteMapping("/auth/comment/{id}")
    public ResponseEntity deleteComment(@PathVariable("id") @Positive Long id, HttpServletRequest request) throws BusinessLogicException {
        commentService.remove(id, request);
        return new ResponseEntity<>("Deleted Comment!", HttpStatus.OK);
    }
}
