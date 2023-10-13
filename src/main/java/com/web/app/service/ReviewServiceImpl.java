package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.review.Review;
import com.web.app.dto.CommentResponseDTO;
import com.web.app.dto.ReviewListDTO;
import com.web.app.dto.ReviewRequestDTO;
import com.web.app.dto.ReviewResponseDTO;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.CommentRepository;
import com.web.app.repository.LikesRepository;
import com.web.app.repository.ReviewRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;
    private final LikesRepository likesRepository;


    @Override
    public Long register(HttpServletRequest request, Review review, Long boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow();

        String requestEmail = (String) request.getAttribute("userEmail");

        if (!board.getEmail().equals(requestEmail) || requestEmail.isBlank()) {
            throw new RuntimeException("해당 요청은 게시글 작성자만 가능합니다.");
        }

        review.setBoard(board);
        Review saved = reviewRepository.save(review);

        return saved.getId();
    }

    @Override
    public ReviewResponseDTO read(Long id, HttpServletRequest request) {

        Review findReview = reviewRepository.findById(id).orElseThrow();

        List<Review> reviews = reviewRepository.findAllByBoardIsOrderByIdDesc(findReview.getBoard());

        List<Comment> comments = commentRepository.findAllByBoardIsOrderByCreatedAtAsc(findReview.getBoard());

        Object userEmail = request.getAttribute("userEmail");
        String requestEmail = userEmail == null ? "" : userEmail.toString();

        List<Long> liked;
        if (requestEmail.isBlank()) {
            liked = new ArrayList<>();
        } else {
            liked = likesRepository.isLiked(findReview.getBoard().getId(), requestEmail);
        }

        List<CommentResponseDTO> commentListDTO = comments.stream()
                .map(comment -> new CommentResponseDTO(comment, requestEmail))
                .collect(Collectors.toList());

        List<ReviewListDTO> reviewListDTOS = reviews.stream()
                .map(review -> modelMapper.map(review, ReviewListDTO.class))
                .collect(Collectors.toList());

        ReviewResponseDTO responseDTO = findReview.toResponseDTO(reviewListDTOS);

        if (!findReview.getBoard().getEmail().equals(requestEmail) || requestEmail.isBlank()) {
            responseDTO.setMyBoard(false);
        } else {
            responseDTO.setMyBoard(true);
        }

        responseDTO.setCommentList(commentListDTO);
        responseDTO.setCommentsCount(commentListDTO.size());
        responseDTO.setLiked(!liked.isEmpty());
        responseDTO.setLikeCount(findReview.getBoard().getLikeCount());

        return responseDTO;
    }

    @Override
    public Review modify(HttpServletRequest request, ReviewRequestDTO reviewRequestDTO , Long id) {

        Review findOne = reviewRepository.findById(id).orElseThrow();

        String requestEmail = (String) request.getAttribute("userEmail");

        if (!findOne.getBoard().getEmail().equals(requestEmail) || requestEmail.isBlank()) {
            throw new RuntimeException("해당 요청은 게시글 작성자만 가능합니다.");
        }

        findOne.change(reviewRequestDTO.getSubTitle(), reviewRequestDTO.getContent()); // dirty check ? => not

        reviewRepository.save(findOne);

        return findOne;
    }

    @Override
    public void remove(HttpServletRequest request, Long id) {

        Review review = reviewRepository.findById(id).orElseThrow();

        String requestEmail = (String) request.getAttribute("userEmail");

        if (!review.getBoard().getEmail().equals(requestEmail) || requestEmail.isBlank()) {
            throw new RuntimeException("해당 요청은 게시글 작성자만 가능합니다.");
        }

        reviewRepository.deleteById(id);
    }
}
