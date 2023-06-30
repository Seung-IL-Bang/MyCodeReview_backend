package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.dto.BoardDTO;
import com.web.app.dto.PageRequestDTO;
import com.web.app.dto.PageResponseDTO;
import com.web.app.dto.PageResponseWithCategoryDTO;
import com.web.app.mediator.GetEmailFromJWT;
import com.web.app.repository.BoardRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;
    private final GetEmailFromJWT getEmailFromJWT;

    @Override
    public Long register(BoardDTO boardDTO) {

        Board board = modelMapper.map(boardDTO, Board.class);

        Board save = boardRepository.save(board);

        return save.getId();
    }

    @Override
    public BoardDTO read(Long id) {

        Optional<Board> result = boardRepository.findById(id);

        Board board = result.orElseThrow();

        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);

        return boardDTO;
    }

    @Override
    public List<Board> readAll(String email) {

        List<Board> listAll = boardRepository.findListAll(email);

        return listAll;
    }

    @Override
    public PageResponseDTO<BoardDTO> readAllWithPaging(String email, PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("id");

        Page<Board> result = boardRepository.findByEmailOrderByIdDesc(email, pageable);

        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());

        int total = boardRepository.getCount(email);

        PageResponseDTO<BoardDTO> pageResponseDTO = PageResponseDTO.<BoardDTO>builder()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .total(total)
                .build();
        return pageResponseDTO;
    }

    // Version 2
    @Override
    public PageResponseWithCategoryDTO<BoardDTO> readAllWithPagingAndSearch(String email, PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("id");

        String[] types = pageRequestDTO.getTypes();

        String[] difficulties = pageRequestDTO.getDifficulties();

        String keyword = pageRequestDTO.getKeyword();

        String tag = pageRequestDTO.getTag();

        List<Board> boards = boardRepository.findListAll(email);

        Page<Board> result = boardRepository.searchAll(types, email, keyword, difficulties, tag, pageable);

        long filteredTotal = 0;
        if (types != null) {
            filteredTotal = boardRepository.filteredAll(types, email, keyword, difficulties, tag);
        }

        // 사용자별 태그 목록
        Map<String, Integer> dtoTags = new HashMap<>();
        // 사용자별 태그 목록 집계
        boards.stream()
                .forEach(board -> board.getTagList().stream().forEach(t ->

                        {
                            if (dtoTags.containsKey(t)) {
                                Integer freq = dtoTags.get(t);
                                dtoTags.put(t, ++freq);
                            } else {
                                dtoTags.put(t, 1);
                            }
                        }
                ));


        // 게시글 목록
        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());

        // 전체 조회 응답
        if (types == null) {
            return PageResponseWithCategoryDTO.builderByAll(pageRequestDTO, dtoList, boards.size(), dtoTags); // boards.size() : 필터링된 개수가 아니고, 필터링 하기 전 총 개수
        }

        // 검색 || 필터링 응답
        return PageResponseWithCategoryDTO.builderByFilter(pageRequestDTO, dtoList, boards.size(), dtoTags, filteredTotal);
    }

    @Override
    public Board modify(HttpServletRequest request, Long id, BoardDTO boardDTO) {

        Optional<Board> result = boardRepository.findById(id);

        Board board = result.orElseThrow();

        String email = getEmailFromJWT.execute(request);

        if (!board.getEmail().equals(email)) {
            throw new RuntimeException("해당 요청은 게시글 작성자만 가능합니다.");
        }

        board.change(boardDTO.getTitle(), boardDTO.getContent(), boardDTO.getTagList(), boardDTO.getLink(), boardDTO.getDifficulty());
        return boardRepository.save(board);
    }

    @Override
    public void remove(HttpServletRequest request, Long id) {
        // 예외 처리를 위해 deleteById 미사용
        Board board = boardRepository.findById(id).orElseThrow();

        String email = getEmailFromJWT.execute(request);

        if (!board.getEmail().equals(email)) {
            throw new RuntimeException("해당 요청은 게시글 작성자만 가능합니다.");
        }

        boardRepository.delete(board);
    }
}
