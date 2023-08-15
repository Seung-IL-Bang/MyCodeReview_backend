package com.web.app.repository;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByBoardIsOrderByCreatedAtDesc(Board board);
}
