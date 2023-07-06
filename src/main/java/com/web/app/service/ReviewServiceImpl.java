package com.web.app.service;

import com.web.app.domain.board.Board;
import com.web.app.domain.review.Review;
import com.web.app.dto.ReviewRequestDTO;
import com.web.app.repository.BoardRepository;
import com.web.app.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BoardRepository boardRepository;

    @Override
    public Long register(Review review, Long boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow();
        review.setBoard(board);
        Review saved = reviewRepository.save(review);

        return saved.getId();
    }

    @Override
    public Review read(Long id) {

        Review review = reviewRepository.findById(id).orElseThrow();
        return review;
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
