package com.web.app.repository;

import com.web.app.domain.board.Board;
import com.web.app.repository.search.BoardSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {

    @Query("select b from Board b where b.email = :email")
    @EntityGraph(attributePaths = "tagList")
    List<Board> findListAll(@Param("email") String email);

    @EntityGraph(attributePaths = "tagList")
    Optional<Board> findById(Long id);

    // ** Deprecated **
    // nativeQuery 사용한 페이징 목록 조회
    @Query(value = "select * from board b where b.email = :email order by b.id desc limit :skip, :size", nativeQuery = true)
    List<Board> getListWithPaging(String email, int skip, int size);


    @Query(value = "select b.* from board b, likes l where l.member_email = :email and l.board_id = b.id order by l.created_at desc",
            countQuery = "select count(b.id) from board b, likes l where l.member_email = :email and l.board_id = b.id",
            nativeQuery = true)
    Page<Board> findFavoriteListByEmail(@Param("email") String email, Pageable pageable);
}
