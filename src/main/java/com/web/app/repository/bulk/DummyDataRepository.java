package com.web.app.repository.bulk;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Repository
public class DummyDataRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public void bulkInsert(List<Board> boards) {
        String sql = String.format("""
                INSERT INTO %s (title, content, writer, email, difficulty, created_at, modified_at, link, like_count, version)
                VALUES (:title, :content, :writer, :email, :difficulty, :createdAt, :modifiedAt, :link, :likeCount, :version)
                """, "board");

        SqlParameterSource[] params = boards.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, params);
    }

    public void bulkInsertForMember(List<Member> members) {
        String sql = String.format("""
                INSERT INTO %s (email, member_role, picture, name, created_at, modified_at)
                VALUES (:email, :memberRole, :picture, :name, :createdAt, :modifiedAt)
                """, "member");

        List<MapSqlParameterSource> parametersList = IntStream.range(0, members.size())
                .mapToObj(index -> {
                    MapSqlParameterSource params = new MapSqlParameterSource();
                    params.addValue("email", members.get(index).getEmail());
                    params.addValue("memberRole", members.get(index).getMemberRole().getKey().substring(5));
                    params.addValue("picture", members.get(index).getPicture());
                    params.addValue("name", members.get(index).getName());
                    params.addValue("createdAt", members.get(index).getCreatedAt());
                    params.addValue("modifiedAt", members.get(index).getModifiedAt());
                    return params;
                }).collect(Collectors.toList());

        namedParameterJdbcTemplate.batchUpdate(sql, parametersList.toArray(new SqlParameterSource[0]));
    }

    public void bulkInsertForTagList(List<Board> boards) {
        List<Long> latestBoardIds = getLatestBoardIds(boards.size());
        String sql = String.format("""
                INSERT INTO %s (board_id, tag_list)
                VALUES (:id, :tag)
                """, "board_tag_list");

        List<SqlParameterSource> parametersList = IntStream.range(0, boards.size())
                .boxed()
                .flatMap(index -> boards.get(index).getTagList().stream()
                        .map(tag -> {
                            MapSqlParameterSource params = new MapSqlParameterSource();
                            params.addValue("id", latestBoardIds.get(index));
                            params.addValue("tag", tag);
                            return params;
                        }))
                .collect(Collectors.toList());

        namedParameterJdbcTemplate.batchUpdate(sql, parametersList.toArray(new SqlParameterSource[0]));
    }

    public void bulkInsertForComment(List<Board> boards, List<Comment> comments, List<Member> members) {
        List<Long> latestBoardIds = getLatestBoardIds(boards.size());
        String sql = String.format("""
                INSERT INTO %s (board_id, member_email, content, created_at, modified_at)
                VALUES (:board_id, :email, :content, :createdAt, :modifiedAt)
                """, "comment");

        List<MapSqlParameterSource> parametersList = new ArrayList<>();

        /** 주의!!!: Comments size <= Members size ? 통과 : ArrayIndexOutOfBoundsException **/
        for (Long boardId : latestBoardIds) {
            IntStream.range(0, comments.size())
                    .forEach(index -> {
                        MapSqlParameterSource params = new MapSqlParameterSource();
                        params.addValue("board_id", boardId);
                        params.addValue("email", members.get(index).getEmail());
                        params.addValue("content", comments.get(index).getContent());
                        params.addValue("createdAt", comments.get(index).getCreatedAt());
                        params.addValue("modifiedAt", comments.get(index).getModifiedAt());
                        parametersList.add(params);
                    });
        }

        namedParameterJdbcTemplate.batchUpdate(sql, parametersList.toArray(new SqlParameterSource[0]));
    }

    private List<Long> getLatestBoardIds(int limit) {
        String sql = "SELECT id FROM board ORDER BY id DESC LIMIT :limit";
        SqlParameterSource params = new MapSqlParameterSource().addValue("limit", limit);
        return namedParameterJdbcTemplate.queryForList(sql, params, Long.class);
    }

}
