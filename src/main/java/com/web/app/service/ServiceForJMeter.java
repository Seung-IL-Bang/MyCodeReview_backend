package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.dto.BoardListResponseDTO;
import com.web.app.dto.PageImplDeSerializeDTO;
import com.web.app.dto.PageRequestDTO;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.LikesRepository;
import com.web.app.repository.search.BoardSearchImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ServiceForJMeter {

    private final BoardRepository boardRepository;
    private final LikesRepository likesRepository;

    private final BoardSearchImpl boardSearch;

    public List<Board> findListAll(HttpServletRequest request) {
        String email = request.getAttribute("userEmail").toString();
        return boardRepository.findListAll(email);
    }

    public List<Board> findFavoriteListByEmail(HttpServletRequest request) {
        String email = request.getAttribute("userEmail").toString();
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        Page<Board> result = boardRepository.findFavoriteListByEmail(email, pageRequestDTO.getPageable());
        return result.getContent();
    }

    public List<Long> isLiked(HttpServletRequest request, Long boardId) {
        String email = request.getAttribute("userEmail").toString();
        return likesRepository.isLiked(boardId, email);
    }

    public List<BoardListResponseDTO> searchPublicAll(PageRequestDTO pageRequestDTO) {

        PageImplDeSerializeDTO<BoardListResponseDTO> boards = boardSearch.searchPublicAll(
                pageRequestDTO.getTypes(),
                pageRequestDTO.getKeyword(),
                pageRequestDTO.getDifficulties(),
                pageRequestDTO.getTag(),
                pageRequestDTO.getPageable());
        return boards.getList();
    }

    public List<Board> searchAll(PageRequestDTO pageRequestDTO, HttpServletRequest request) {
        String email = request.getAttribute("userEmail").toString();
        Page<Board> boards = boardSearch.searchAll(
                pageRequestDTO.getTypes(),
                email,
                pageRequestDTO.getKeyword(),
                pageRequestDTO.getDifficulties(),
                pageRequestDTO.getTag(),
                pageRequestDTO.getPageable());
        return boards.getContent();
    }


}
