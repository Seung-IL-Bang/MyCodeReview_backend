package com.web.app.controller;

import com.web.app.domain.board.Board;
import com.web.app.dto.*;
import com.web.app.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시글 개별 상세 조회
    @GetMapping("/board/{id}")
    public ResponseEntity getBoard(@PathVariable("id") @Positive Long id, HttpServletRequest request) {

        BoardResponseDTO response = boardService.read(id, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // Main 홈 화면 - 전체 조회, 제목 검색
    @GetMapping("/board/list")
    public ResponseEntity getPublicBoardList(@Valid PageRequestDTO pageRequestDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            pageRequestDTO = PageRequestDTO.builder().build();
        }

        PageResponseDTO<BoardResponseDTO> response = boardService.readPublicAllWithPagingAndSearch(pageRequestDTO);

        return new ResponseEntity(response, HttpStatus.OK);
    }


    // 회원 - My Reviews 조회
    // 회원 - 제목, 난이도, 태그 동적 쿼리
    @GetMapping("/auth/board/v2/list")
    public ResponseEntity getBoardListWithPagingAndSearch(@Valid PageRequestDTO pageRequestDTO, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            pageRequestDTO = PageRequestDTO.builder().build();
        }

        PageResponseWithCategoryDTO<BoardListResponseDTO> response = boardService.readAllWithPagingAndSearch(request, pageRequestDTO);

        return new ResponseEntity(response, HttpStatus.OK);
    }


    // 회원 - My Favorites 조회
    @GetMapping("/auth/board/liked/list")
    public ResponseEntity getLikedBoards(@Valid PageRequestDTO pageRequestDTO, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            pageRequestDTO = PageRequestDTO.builder().build();
        }

        PageResponseDTO<BoardResponseDTO> response = boardService.readByEmailLikeBoardsWithPaging(request, pageRequestDTO);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping("/auth/board")
    public ResponseEntity postBoard(@Valid @RequestBody BoardRequestDTO boardRequestDTO) {

        Long id = boardService.register(boardRequestDTO);

        return new ResponseEntity(id, HttpStatus.CREATED);
    }

    @PutMapping("/auth/board/{id}")
    public ResponseEntity putBoard(HttpServletRequest request, @PathVariable("id") @Positive Long id, @Valid @RequestBody BoardRequestDTO boardRequestDTO) {

        Board board = boardService.modify(request, id, boardRequestDTO);

        return new ResponseEntity(board, HttpStatus.OK);
    }

    @DeleteMapping("/auth/board/{id}")
    public void deleteBoard(HttpServletRequest request, @PathVariable("id") @Positive Long id) {
        boardService.remove(request, id);
    }
}
