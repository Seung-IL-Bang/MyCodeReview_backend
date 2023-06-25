package com.web.app.repository;

import com.web.app.domain.board.Board;
import com.web.app.repository.search.BoardSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {

    @Query("select b from Board b where b.email = :email")
    List<Board> findListAll(String email);

    @Query("select count(b.id) from Board b where b.email = :email")
    int getCount(String email);

    // ** Deprecated **
    // nativeQuery 사용한 페이징 목록 조회
    @Query(value = "select * from board b where b.email = :email order by b.id desc limit :skip, :size", nativeQuery = true)
    List<Board> getListWithPaging(String email, int skip, int size);

    // 쿼리 메소드 사용한 페이징 목록 조회 with Pageable
    Page<Board> findByEmailOrderByIdDesc(String email, Pageable pageable);

}
