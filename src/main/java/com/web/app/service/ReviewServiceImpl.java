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
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j(topic = "kafka-logger")
@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;
    private final LikesRepository likesRepository;
    private final ReplyRepository replyRepository;

    private final CachingService cachingService;


    @Override
    public Long register(HttpServletRequest request, Review review, Long boardId) {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        if (!violations.isEmpty()) { // 유효성 검사 실패 시 처리; 예외 던지기, 오류 메시지 반환 등
            List<ConstraintViolation<Review>> violationsList = new ArrayList<>(violations);
            throw new ValidationException(violationsList.get(0).getMessage());
        }

        Board board = boardRepository.findById(boardId).orElseThrow();

        String requestEmail = (String) request.getAttribute("userEmail");

        if (!board.getEmail().equals(requestEmail) || requestEmail.isBlank()) {
            throw new RuntimeException("해당 요청은 게시글 작성자만 가능합니다.");
        }

        review.setBoard(board);
        Review saved = reviewRepository.save(review);

        log.info(String.format("CREATE REVIEW: id=%d, board_id=%d, writer=%s, email=%s",
                saved.getId(),
                board.getId(),
                board.getWriter(),
                board.getEmail()));

        return saved.getId();
    }

    @Override
    public ReviewResponseDTO read(Long id, String requestEmail) {

        Review findReview = reviewRepository.findById(id).orElseThrow();

        List<Review> reviews = reviewRepository.findAllByBoardIsOrderByIdDesc(findReview.getBoard());

//        List<Comment> comments = commentRepository.findAllByBoardIsOrderByCreatedAtAsc(findReview.getBoard());

        List<Long> liked;
        if (requestEmail.isBlank()) {
            liked = new ArrayList<>();
        } else {
            liked = likesRepository.isLiked(findReview.getBoard().getId(), requestEmail);
        }


        List<CommentResponseDTO> commentsForBoard = cachingService.getCommentsForBoard(findReview.getBoard());

//        List<CommentResponseDTO> commentListDTO = comments.stream()
//                .map(comment -> new CommentResponseDTO(comment, requestEmail))
//                .collect(Collectors.toList());

        commentsForBoard.stream()
                        .forEach(commentResponseDTO -> commentResponseDTO.checkMyComment(requestEmail));



        commentsForBoard.stream()
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

        ReviewResponseDTO responseDTO = findReview.toResponseDTO(reviewListDTOS);

        if (!findReview.getBoard().getEmail().equals(requestEmail) || requestEmail.isBlank()) {
            responseDTO.setMyBoard(false);
        } else {
            responseDTO.setMyBoard(true);
        }

        responseDTO.setCommentList(commentsForBoard);
        responseDTO.setCommentsCount(commentsForBoard.size());
        responseDTO.setLiked(!liked.isEmpty());
        responseDTO.setLikeCount(findReview.getBoard().getLikeCount());

        return responseDTO;
    }

    @Override
    public Review modify(HttpServletRequest request, ReviewRequestDTO reviewRequestDTO , Long id) {

        Review newReview = modelMapper.map(reviewRequestDTO, Review.class);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Review>> violations = validator.validate(newReview);

        if (!violations.isEmpty()) { // 유효성 검사 실패 시 처리; 예외 던지기, 오류 메시지 반환 등
            List<ConstraintViolation<Review>> violationsList = new ArrayList<>(violations);
            throw new ValidationException(violationsList.get(0).getMessage());
        }

        Review findOne = reviewRepository.findById(id).orElseThrow();

        String requestEmail = (String) request.getAttribute("userEmail");

        Board board = findOne.getBoard();

        if (!board.getEmail().equals(requestEmail) || requestEmail.isBlank()) {
            throw new RuntimeException("해당 요청은 게시글 작성자만 가능합니다.");
        }


        findOne.change(newReview.getSubTitle(), newReview.getContent()); // dirty check ? => not

        Review modified = reviewRepository.save(findOne);

        log.info(String.format("UPDATE REVIEW: id=%d, board_id=%d, writer=%s, email=%s",
                modified.getId(),
                board.getId(),
                board.getWriter(),
                board.getEmail()));
        return modified;
    }

    @Override
    public void remove(HttpServletRequest request, Long id) {

        Review review = reviewRepository.findById(id).orElseThrow();

        String requestEmail = (String) request.getAttribute("userEmail");

        Board board = review.getBoard();

        if (!board.getEmail().equals(requestEmail) || requestEmail.isBlank()) {
            throw new RuntimeException("해당 요청은 게시글 작성자만 가능합니다.");
        }

        reviewRepository.deleteById(id);
        log.info(String.format("DELETE REVIEW: id=%d, board_id=%d, writer=%s, email=%s",
                review.getId(),
                board.getId(),
                board.getWriter(),
                board.getEmail()));
    }
}
