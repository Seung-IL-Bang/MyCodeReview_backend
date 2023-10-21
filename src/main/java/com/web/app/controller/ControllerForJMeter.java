package com.web.app.controller;

import com.web.app.domain.board.Board;
import com.web.app.dto.PageRequestDTO;
import com.web.app.service.ServiceForJMeter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ControllerForJMeter {

    private final ServiceForJMeter serviceForJMeter;


    @GetMapping("/auth/jmeter/find-list-all")
    public ResponseEntity findListAll(HttpServletRequest request) {
        List<Board> response = serviceForJMeter.findListAll(request);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping("/auth/jmeter/find-favorite-list-by-email")
    public ResponseEntity findFavoriteListByEmail(HttpServletRequest request) {
        List<Board> response = serviceForJMeter.findFavoriteListByEmail(request);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping("/auth/jmeter/is-liked")
    public ResponseEntity isLiked(HttpServletRequest request, @RequestParam("boardId") Long boardId) {
        List<Long> response = serviceForJMeter.isLiked(request, boardId);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping("/jmeter/search/public/all")
    public ResponseEntity searchPublicAll(@Valid PageRequestDTO pageRequestDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            pageRequestDTO = PageRequestDTO.builder().build();
        }

        List<Board> response = serviceForJMeter.searchPublicAll(pageRequestDTO);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping("/auth/jmeter/search/all")
    public ResponseEntity searchPublicAll(HttpServletRequest request, @Valid PageRequestDTO pageRequestDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            pageRequestDTO = PageRequestDTO.builder().build();
        }

        List<Board> response = serviceForJMeter.searchAll(pageRequestDTO, request);
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
