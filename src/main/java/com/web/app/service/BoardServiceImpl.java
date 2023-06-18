package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.dto.BoardDTO;
import com.web.app.dto.PageRequestDTO;
import com.web.app.dto.PageResponseDTO;
import com.web.app.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;

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

        int total = boardRepository.getCount();

        PageResponseDTO<BoardDTO> pageResponseDTO = PageResponseDTO.<BoardDTO>builder()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .total(total)
                .build();
        return pageResponseDTO;
    }

    @Override
    public Board modify(Long id, BoardDTO boardDTO) {

        // TODO: 작성자만 수정 가능
        Optional<Board> result = boardRepository.findById(id);

        Board board = result.orElseThrow();

        board.change(boardDTO.getTitle(), boardDTO.getContent());
        return boardRepository.save(board);
    }

    @Override
    public void remove(Long id) {
        // TODO: 작성자만 삭제 가능
        // 예외 처리를 위해 deleteById 미사용
        Board board = boardRepository.findById(id).orElseThrow();
        boardRepository.delete(board);
    }
}
