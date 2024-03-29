package com.web.app.repository;

import com.web.app.domain.board.Board;
import com.web.app.domain.likes.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    @Query(value = "select l.id from likes l where l.board_id = :boardId and l.member_email = :memberEmail", nativeQuery = true)
    List<Long> isLiked(@Param("boardId") Long boardId, @Param("memberEmail") String memberEmail);

    void deleteLikesByBoardIs(Board board);
}
