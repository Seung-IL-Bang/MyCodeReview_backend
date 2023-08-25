package com.web.app.controller;

import com.web.app.dto.ReplyRequestDTO;
import com.web.app.dto.ReplyResponseDTO;
import com.web.app.exception.BusinessLogicException;
import com.web.app.service.ReplyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/auth/reply")
    public ResponseEntity postReply(@Valid @RequestBody ReplyRequestDTO replyRequestDTO) {
        ReplyResponseDTO response = replyService.register(replyRequestDTO);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    @PutMapping("/auth/reply")
    public ResponseEntity putReply(@Valid @RequestBody ReplyRequestDTO replyRequestDTO, HttpServletRequest request) throws BusinessLogicException {
        replyService.update(replyRequestDTO, request);
        return new ResponseEntity("답글이 수정되었습니다.", HttpStatus.OK);
    }

    @DeleteMapping("/auth/reply/{id}")
    public ResponseEntity deleteReply(@Positive @PathVariable("id") Long id, HttpServletRequest request) throws BusinessLogicException{
        replyService.remove(id, request);
        return new ResponseEntity("답글이 삭제되었습니다.", HttpStatus.OK);
    }
}
