package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.reply.Reply;
import com.web.app.domain.review.Review;
import com.web.app.dto.*;
import com.web.app.mediator.GetEmailFromJWT;
import com.web.app.repository.*;
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
    private final LikesRepository likesRepository;
    private final ReviewRepository reviewRepository;

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final ModelMapper modelMapper;
    private final GetEmailFromJWT getEmailFromJWT;

    @Override
    public Long register(BoardRequestDTO boardRequestDTO) {

        Board board = modelMapper.map(boardRequestDTO, Board.class);

        Board save = boardRepository.save(board);

        return save.getId();
    }

    @Override
    public BoardResponseDTO read(Long id, String requestEmail) {

        Board board = boardRepository.findById(id).orElseThrow(() -> {
            throw new NoSuchElementException("해당 회원은 존재하지 않습니다.");
        });

        BoardResponseDTO dto = modelMapper.map(board, BoardResponseDTO.class);

        List<Long> liked;
        if (requestEmail.isBlank()) {
            liked = new ArrayList<>();
        } else {
            liked = likesRepository.isLiked(id, requestEmail);
        }

        List<Review> reviews = reviewRepository.findAllByBoardIsOrderByIdDesc(board);

        List<Comment> comments = commentRepository.findAllByBoardIsOrderByCreatedAtAsc(board);

        List<CommentResponseDTO> commentListDTO = comments.stream()
                .map(comment -> new CommentResponseDTO(comment, requestEmail))
                .collect(Collectors.toList());

        commentListDTO.stream()
                .forEach(commentResponseDTO -> {
                    Long replyId = commentResponseDTO.getId();
                    List<Reply> findList = replyRepository.findAllByComment_IdOrderByCreatedAtAsc(replyId);
                    List<ReplyResponseDTO> replyListDTO = findList.stream().map(reply -> new ReplyResponseDTO(reply, requestEmail))
                            .collect(Collectors.toList());

                    commentResponseDTO.setRepliesCount(findList.size());
                    commentResponseDTO.setReplies(replyListDTO);
                });


        List<ReviewListDTO> reviewListDTOS = reviews.stream()
                .map(review -> modelMapper.map(review, ReviewListDTO.class))
                .collect(Collectors.toList());

        if (!board.getEmail().equals(requestEmail) || requestEmail.isBlank()) {
            dto.setMyBoard(false);
        } else {
            dto.setMyBoard(true);
        }

        dto.setReviewList(reviewListDTOS);
        dto.setLiked(!liked.isEmpty());
        dto.setLikeCount(board.getLikeCount());
        dto.setCommentList(commentListDTO);
        dto.setCommentsCount(commentListDTO.size());

        return dto;
    }

    @Override
    public List<Board> readAll(String email) {

        List<Board> listAll = boardRepository.findListAll(email);

        return listAll;
    }

    @Override
    public PageResponseDTO<BoardRequestDTO> readAllWithPaging(String email, PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("id");

        Page<Board> result = boardRepository.findByEmailOrderByIdDesc(email, pageable);

        List<BoardRequestDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardRequestDTO.class))
                .collect(Collectors.toList());

        int total = boardRepository.getCount(email);

        PageResponseDTO<BoardRequestDTO> pageResponseDTO = PageResponseDTO.<BoardRequestDTO>builder()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .total(total)
                .build();
        return pageResponseDTO;
    }

    // Version 2
    @Override
    public PageResponseWithCategoryDTO<BoardListResponseDTO> readAllWithPagingAndSearch(String email, PageRequestDTO pageRequestDTO) {

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
        List<BoardListResponseDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardListResponseDTO.class))
                .collect(Collectors.toList());

        // 전체 조회 응답
        if (types == null) {
            return PageResponseWithCategoryDTO.builderByAll(pageRequestDTO, dtoList, boards.size(), dtoTags); // boards.size() : 필터링된 개수가 아니고, 필터링 하기 전 총 개수
        }

        // 검색 || 필터링 응답
        return PageResponseWithCategoryDTO.builderByFilter(pageRequestDTO, dtoList, boards.size(), dtoTags, filteredTotal);
    }

    @Override
    public Board modify(HttpServletRequest request, Long id, BoardRequestDTO boardRequestDTO) {

        Optional<Board> result = boardRepository.findById(id);

        Board board = result.orElseThrow();

        String email = getEmailFromJWT.execute(request);

        if (!board.getEmail().equals(email) || email.isBlank()) {
            throw new RuntimeException("해당 요청은 게시글 작성자만 가능합니다.");
        }

        board.change(boardRequestDTO.getTitle(), boardRequestDTO.getContent(), boardRequestDTO.getTagList(), boardRequestDTO.getLink(), boardRequestDTO.getDifficulty());
        return boardRepository.save(board);
    }

    @Override
    public void remove(HttpServletRequest request, Long id) {


        // 예외 처리를 위해 deleteById 미사용
        Board board = boardRepository.findById(id).orElseThrow();

        String email = getEmailFromJWT.execute(request);

        if (!board.getEmail().equals(email) || email.isBlank()) {
            throw new RuntimeException("해당 요청은 게시글 작성자만 가능합니다.");
        }

        // 외래키 제약조건에 의해 Reviews 먼저 삭제
        reviewRepository.deleteReviewsByBoardIs(board);

        boardRepository.delete(board);
    }

    @Override
    public PageResponseDTO<BoardResponseDTO> readPublicAllWithPaging(PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("createdAt");

        Page<Board> boards = boardRepository.searchPublicAll(
                pageRequestDTO.getTypes(),
                pageRequestDTO.getKeyword(),
                pageRequestDTO.getDifficulties(),
                pageRequestDTO.getTag(),
                pageable);

        List<BoardResponseDTO> dtoList = boards.getContent().stream()
                .map(board -> modelMapper.map(board, BoardResponseDTO.class))
                .collect(Collectors.toList());


        return PageResponseDTO.<BoardResponseDTO>builder()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) boards.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<BoardResponseDTO> readByEmailLikeBoardsWithPaging(String email, PageRequestDTO pageRequestDTO) {

        Page<Board> favoriteListByEmail = boardRepository.findFavoriteListByEmail(email, pageRequestDTO.getPageable());

        List<BoardResponseDTO> dtoList = favoriteListByEmail.getContent().stream()
                .map(board -> modelMapper.map(board, BoardResponseDTO.class)).toList();


        return PageResponseDTO.<BoardResponseDTO>builder()
                .dtoList(dtoList)
                .total((int) favoriteListByEmail.getTotalElements())
                .pageRequestDTO(pageRequestDTO)
                .build();
    }
}
