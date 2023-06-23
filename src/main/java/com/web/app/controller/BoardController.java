package com.web.app.controller;

import com.web.app.domain.board.Board;
import com.web.app.dto.BoardDTO;
import com.web.app.dto.PageRequestDTO;
import com.web.app.dto.PageResponseDTO;
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
@RequestMapping("/auth/board")
public class BoardController {

    private final BoardService boardService;
    private final GetBoardListFromEmailOfJWT getBoardListFromEmailOfJWT;
    private final GetEmailFromJWT getEmailFromJWT;

    @GetMapping("/{id}")
    public ResponseEntity getBoard(@PathVariable("id") @Positive Long id) {

        BoardDTO boardDTO = boardService.read(id);

        return new ResponseEntity<>(boardDTO, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity getBoardList(HttpServletRequest request) {

        List<Board> boardList = getBoardListFromEmailOfJWT.execute(request);

        return new ResponseEntity(boardList, HttpStatus.OK);
    }

    @GetMapping("/v1/list")
    public ResponseEntity getBoardListWithPaging(@Valid PageRequestDTO pageRequestDTO, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            pageRequestDTO = PageRequestDTO.builder().build();
        }

        String email = getEmailFromJWT.execute(request);
        PageResponseDTO<BoardDTO> pageResponseDTO = boardService.readAllWithPaging(email, pageRequestDTO);


        return new ResponseEntity(pageResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/v2/list")
    public ResponseEntity getBoardListWithPagingAndSearch(@Valid PageRequestDTO pageRequestDTO, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            pageRequestDTO = PageRequestDTO.builder().build();
        }

        String email = getEmailFromJWT.execute(request);

        PageResponseDTO<BoardDTO> pageResponseDTO = boardService.readAllWithPagingAndSearch(email, pageRequestDTO);

        return new ResponseEntity(pageResponseDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity postBoard(@RequestBody BoardDTO boardDTO) {

        Long id = boardService.register(boardDTO);

        return new ResponseEntity(id, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity putBoard(HttpServletRequest request, @PathVariable("id") @Positive Long id, @RequestBody BoardDTO boardDTO) {

        Board board = boardService.modify(request, id, boardDTO);

        return new ResponseEntity(board, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteBoard(HttpServletRequest request, @PathVariable("id") @Positive Long id) {
        boardService.remove(request, id);
    }
}
