package com.web.app.repository;

import com.web.app.domain.board.Board;
import com.web.app.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findById(Long id);

    List<Review> findAllByBoardIsOrderByIdDesc(Board board);

    void deleteReviewsByBoardIs(Board board);


}
