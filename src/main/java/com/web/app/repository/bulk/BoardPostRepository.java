package com.web.app.repository.bulk;

import com.web.app.domain.board.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class BoardPostRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    final static String TABLE = "Board";


    public void bulkInsert(List<Board> boards) {
        String sql = String.format("""
                INSERT INTO %s (title, content, writer, email, difficulty, created_at, modified_at, link, like_count, version)
                VALUES (:title, :content, :writer, :email, :difficulty, :createdAt, :modifiedAt, :link, :likeCount, :version)
                """, TABLE);

        SqlParameterSource[] params = boards.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, params);
    }
}
