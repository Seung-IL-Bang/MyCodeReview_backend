package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.review.Review;
import com.web.app.dto.CommentResponseDTO;
import com.web.app.dto.ReviewListDTO;
import com.web.app.repository.CommentRepository;
import com.web.app.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CachingService {

    private final ReviewRepository reviewRepository;

    private final ModelMapper modelMapper;

    private final CommentRepository commentRepository;

    @Cacheable(value = "boardReviews", key = "#board.id")
    public List<ReviewListDTO> getReviewsForBoard(Board board) {
        List<Review> reviews = reviewRepository.findAllByBoardIsOrderByIdDesc(board);
        List<ReviewListDTO> reviewListDTOS = reviews.stream()
                .map(review -> modelMapper.map(review, ReviewListDTO.class))
                .collect(Collectors.toList());
        return reviewListDTOS;
    }

    @Cacheable(value = "boardComments", key = "#board.id")
    public List<CommentResponseDTO> getCommentsForBoard(Board board) {
        List<Comment> comments = commentRepository.findAllByBoardIsOrderByCreatedAtAsc(board);

        List<CommentResponseDTO> commentListDTO = comments.stream()
                .map(comment -> new CommentResponseDTO(comment))
                .collect(Collectors.toList());
        return commentListDTO;
    }
}
