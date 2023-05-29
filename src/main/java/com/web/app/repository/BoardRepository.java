package com.web.app.repository;

import com.web.app.domain.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select b from Board b where b.email = :email")
    List<Board> findListAll(String email);
}
