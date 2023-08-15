package com.web.app.controller;

import com.web.app.domain.board.Board;
import com.web.app.dto.*;
import com.web.app.mediator.GetBoardListFromEmailOfJWT;
import com.web.app.mediator.GetEmailFromJWT;
import com.web.app.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final GetBoardListFromEmailOfJWT getBoardListFromEmailOfJWT;
    private final GetEmailFromJWT getEmailFromJWT;

    @GetMapping("/board/{id}")
    public ResponseEntity getBoard(@PathVariable("id") @Positive Long id, HttpServletRequest request) {

        String requestEmail = getEmailFromJWT.execute(request);

        BoardResponseDTO response = boardService.read(id, requestEmail);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/auth/board/list")
    public ResponseEntity getBoardList(HttpServletRequest request) {

        List<Board> boardList = getBoardListFromEmailOfJWT.execute(request);

        return new ResponseEntity(boardList, HttpStatus.OK);
    }


    @GetMapping("/board/list")
    public ResponseEntity getPublicBoardList(@Valid PageRequestDTO pageRequestDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            pageRequestDTO = PageRequestDTO.builder().build();
        }

        PageResponseDTO<BoardResponseDTO> response = boardService.readPublicAllWithPaging(pageRequestDTO);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping("/auth/board/v1/list")
    public ResponseEntity getBoardListWithPaging(@Valid PageRequestDTO pageRequestDTO, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            pageRequestDTO = PageRequestDTO.builder().build();
        }

        String email = getEmailFromJWT.execute(request);
        PageResponseDTO<BoardRequestDTO> pageResponseDTO = boardService.readAllWithPaging(email, pageRequestDTO);


        return new ResponseEntity(pageResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/auth/board/v2/list")
    public ResponseEntity getBoardListWithPagingAndSearch(@Valid PageRequestDTO pageRequestDTO, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            pageRequestDTO = PageRequestDTO.builder().build();
        }

        String email = getEmailFromJWT.execute(request);

        PageResponseWithCategoryDTO<BoardListResponseDTO> response = boardService.readAllWithPagingAndSearch(email, pageRequestDTO);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping("/auth/board/liked/list")
    public ResponseEntity getLikedBoards(@Valid PageRequestDTO pageRequestDTO, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            pageRequestDTO = PageRequestDTO.builder().build();
        }

        String email = getEmailFromJWT.execute(request);

        PageResponseDTO<BoardResponseDTO> response = boardService.readByEmailLikeBoardsWithPaging(email, pageRequestDTO);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping("/auth/board")
    public ResponseEntity postBoard(@RequestBody BoardRequestDTO boardRequestDTO) {

        Long id = boardService.register(boardRequestDTO);

        return new ResponseEntity(id, HttpStatus.CREATED);
    }

    @PutMapping("/auth/board/{id}")
    public ResponseEntity putBoard(HttpServletRequest request, @PathVariable("id") @Positive Long id, @RequestBody BoardRequestDTO boardRequestDTO) {

        Board board = boardService.modify(request, id, boardRequestDTO);

        return new ResponseEntity(board, HttpStatus.OK);
    }

    @DeleteMapping("/auth/board/{id}")
    public void deleteBoard(HttpServletRequest request, @PathVariable("id") @Positive Long id) {
        boardService.remove(request, id);
    }
}
