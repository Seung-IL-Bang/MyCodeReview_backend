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
import com.web.app.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long register(Review review, Long boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow();
        review.setBoard(board);
        Review saved = reviewRepository.save(review);

        return saved.getId();
    }

    @Override
    public ReviewResponseDTO read(Long id) {

        Review findReview = reviewRepository.findById(id).orElseThrow();

        List<Review> reviews = reviewRepository.findAllByBoardIsOrderByIdDesc(findReview.getBoard());

        List<Comment> comments = commentRepository.findAllByBoardIsOrderByCreatedAtDesc(findReview.getBoard());

        List<CommentResponseDTO> commentListDTO = comments.stream()
                .map(CommentResponseDTO::new)
                .collect(Collectors.toList());

        List<ReviewListDTO> reviewListDTOS = reviews.stream()
                .map(review -> modelMapper.map(review, ReviewListDTO.class))
                .collect(Collectors.toList());

        ReviewResponseDTO responseDTO = findReview.toResponseDTO(reviewListDTOS);

        responseDTO.setCommentList(commentListDTO);

        return responseDTO;
    }

    @Override
    public Review modify(ReviewRequestDTO reviewRequestDTO , Long id) {

        Review findOne = reviewRepository.findById(id).orElseThrow();

        findOne.change(reviewRequestDTO.getSubTitle(), reviewRequestDTO.getContent()); // dirty check ? => not

        reviewRepository.save(findOne);

        return findOne;
    }

    @Override
    public void remove(Long id) {
        // TODO: 삭제 실패 시 예외 처리 추가
        reviewRepository.deleteById(id);

    }
}
