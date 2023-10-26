package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.reply.Reply;
import com.web.app.domain.review.Review;
import com.web.app.dto.*;
import com.web.app.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
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

    private final CacheManager cacheManager;
    private final ReentrantLock lock = new ReentrantLock();


    @Override
    public Long register(BoardRequestDTO boardRequestDTO) {

        Board board = modelMapper.map(boardRequestDTO, Board.class);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        if (!violations.isEmpty()) { // 유효성 검사 실패 시 처리; 예외 던지기, 오류 메시지 반환 등
            List<ConstraintViolation<Board>> violationsList = new ArrayList<>(violations);
            throw new ValidationException(violationsList.get(0).getMessage());
        }

        Board save = boardRepository.save(board);

        return save.getId();
    }

    @Override
    public BoardResponseDTO read(Long id, String requestEmail) {

        Board board = boardRepository.findById(id).orElseThrow(() -> {
            throw new NoSuchElementException("해당 게시글은 존재하지 않습니다.");
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


    // Version 2
    @Override
    public PageResponseWithCategoryDTO<BoardListResponseDTO> readAllWithPagingAndSearch(HttpServletRequest request, PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("id");

        String[] types = pageRequestDTO.getTypes();

        String[] difficulties = pageRequestDTO.getDifficulties();

        String keyword = pageRequestDTO.getKeyword();

        String tag = pageRequestDTO.getTag();

        String email = (String) request.getAttribute("userEmail");

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

        String email = (String) request.getAttribute("userEmail");

        if (!board.getEmail().equals(email) || email.isBlank()) {
            throw new RuntimeException("해당 요청은 게시글 작성자만 가능합니다.");
        }

        Board newBoard = modelMapper.map(boardRequestDTO, Board.class);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Board>> violations = validator.validate(newBoard);

        if (!violations.isEmpty()) { // 유효성 검사 실패 시 처리; 예외 던지기, 오류 메시지 반환 등
            List<ConstraintViolation<Board>> violationsList = new ArrayList<>(violations);
            throw new ValidationException(violationsList.get(0).getMessage());
        }

        board.change(newBoard.getTitle(), newBoard.getContent(), newBoard.getTagList(), newBoard.getLink(), newBoard.getDifficulty());
        return boardRepository.save(board);
    }

    @Override
    public void remove(HttpServletRequest request, Long id) {
        // 예외 처리를 위해 deleteById 미사용
        Board board = boardRepository.findById(id).orElseThrow();

        String email = (String) request.getAttribute("userEmail");

        if (!board.getEmail().equals(email) || email.isBlank()) {
            throw new RuntimeException("해당 요청은 게시글 작성자만 가능합니다.");
        }

        // 외래키 제약조건에 의해 Reviews, Likes, Comments, Replies 먼저 삭제
        reviewRepository.deleteReviewsByBoardIs(board);
        likesRepository.deleteLikesByBoardIs(board);
        List<Comment> comments = commentRepository.findAllByBoardIs(board);
        comments.stream().forEach(replyRepository::deleteRepliesByCommentIs);
        commentRepository.deleteAll(comments);


        boardRepository.delete(board);
    }

    @Override
    public PageResponseDTO<BoardListResponseDTO> readPublicAllWithPagingAndSearch(PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("createdAt");
        PageImplDeSerializeDTO<BoardListResponseDTO> boards = null;

        if (pageRequestDTO.getTypes() == null) {
            Cache boardsCache = cacheManager.getCache("boards");
            String cacheKey = String.valueOf(pageable.getPageNumber());
            boards = boardsCache.get(cacheKey, PageImplDeSerializeDTO.class);

            if (boards != null) { // Caching Hit
                return PageResponseDTO.<BoardListResponseDTO>builder()
                        .pageRequestDTO(pageRequestDTO)
                        .dtoList(boards.getList())
                        .total(boards.getTotal()) // count(board_id)
                        .build();
            }

            lock.lock(); // Caching Miss
            try {
                boards = boardRepository.searchPublicAll( // set cache
                        pageRequestDTO.getTypes(),
                        pageRequestDTO.getKeyword(),
                        pageRequestDTO.getDifficulties(),
                        pageRequestDTO.getTag(),
                        pageable);
                return PageResponseDTO.<BoardListResponseDTO>builder()
                        .pageRequestDTO(pageRequestDTO)
                        .dtoList(boards.getList())
                        .total(boards.getTotal()) // count(board_id)
                        .build();
            } finally {
                lock.unlock();
            }
        } else { // types != null 인 경우 캐싱 없이 DB에서 조회
            boards = boardRepository.searchPublicAll(
                    pageRequestDTO.getTypes(),
                    pageRequestDTO.getKeyword(),
                    pageRequestDTO.getDifficulties(),
                    pageRequestDTO.getTag(),
                    pageable);

            return PageResponseDTO.<BoardListResponseDTO>builder()
                    .pageRequestDTO(pageRequestDTO)
                    .dtoList(boards.getList())
                    .total(boards.getTotal()) // count(board_id)
                    .build();
        }

    }

    @Override
    public PageResponseDTO<BoardResponseDTO> readByEmailLikeBoardsWithPaging(HttpServletRequest request, PageRequestDTO pageRequestDTO) {

        String email = (String) request.getAttribute("userEmail");

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
