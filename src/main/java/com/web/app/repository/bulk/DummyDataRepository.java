package com.web.app.repository.bulk;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.likes.Likes;
import com.web.app.domain.member.Member;
import com.web.app.domain.reply.Reply;
import com.web.app.domain.review.Review;
import lombok.RequiredArgsConstructor;
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

    public void bulkInsertForBoard(List<Board> boards, List<Member> members) {
        String sql = String.format("""
                INSERT INTO %s (title, content, writer, email, difficulty, created_at, modified_at, link, like_count, version)
                VALUES (:title, :content, :writer, :email, :difficulty, :createdAt, :modifiedAt, :link, :likeCount, :version)
                """, "board");

        SqlParameterSource[] params = IntStream.range(0, boards.size())
                .mapToObj(i -> {
                    Board board = boards.get(i);
                    String memberEmail = members.get(i % members.size()).getEmail();  // Cycle through members' emails

                    MapSqlParameterSource paramSource = new MapSqlParameterSource();
                    paramSource.addValue("title", board.getTitle());
                    paramSource.addValue("content", board.getContent());
                    paramSource.addValue("writer", board.getWriter());
                    paramSource.addValue("email", memberEmail);  // set the member's email
                    paramSource.addValue("difficulty", board.getDifficulty());
                    paramSource.addValue("createdAt", board.getCreatedAt());
                    paramSource.addValue("modifiedAt", board.getModifiedAt());
                    paramSource.addValue("link", board.getLink());
                    paramSource.addValue("likeCount", board.getLikeCount());
                    paramSource.addValue("version", board.getVersion());

                    return paramSource;
                })
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, params);
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

    public void bulkInsertForReview(List<Board> boards, List<Review> reviews) {
        List<Long> latestBoardIds = getLatestBoardIds(boards.size());

        String sql = String.format("""
                INSERT INTO %s (board_id, sub_title, content, created_at, modified_at)
                VALUES (:board_id, :subTitle, :content, :createdAt, :modifiedAt)
                """, "review");

        List<MapSqlParameterSource> parametersList = new ArrayList<>();

        for (Long boardId : latestBoardIds) {
            IntStream.range(0, reviews.size())
                    .forEach(index -> {
                        MapSqlParameterSource params = new MapSqlParameterSource();
                        params.addValue("board_id", boardId);
                        params.addValue("subTitle", reviews.get(index).getSubTitle());
                        params.addValue("content", reviews.get(index).getContent());
                        params.addValue("createdAt", reviews.get(index).getCreatedAt());
                        params.addValue("modifiedAt", reviews.get(index).getModifiedAt());
                        parametersList.add(params);
                    });
        }

        namedParameterJdbcTemplate.batchUpdate(sql, parametersList.toArray(new SqlParameterSource[0]));

    }

    public void bulkInsertForComment(List<Board> boards, List<Member> members, List<Comment> comments) {
        List<Long> latestBoardIds = getLatestBoardIds(boards.size());

        String sql = String.format("""
                INSERT INTO %s (board_id, member_email, content, created_at, modified_at)
                VALUES (:board_id, :email, :content, :createdAt, :modifiedAt)
                """, "comment");

        List<MapSqlParameterSource> parametersList = new ArrayList<>();

        for (Long boardId : latestBoardIds) {
            IntStream.range(0, comments.size())
                    .forEach(index -> {
                        MapSqlParameterSource params = new MapSqlParameterSource();
                        params.addValue("board_id", boardId);
                        params.addValue("email", members.get(index % members.size()).getEmail()); // Cycle through members' emails
                        params.addValue("content", comments.get(index).getContent());
                        params.addValue("createdAt", comments.get(index).getCreatedAt());
                        params.addValue("modifiedAt", comments.get(index).getModifiedAt());
                        parametersList.add(params);
                    });
        }

        namedParameterJdbcTemplate.batchUpdate(sql, parametersList.toArray(new SqlParameterSource[0]));
    }

    public void bulkInsertForReply(List<Board> boards, List<Member> members, List<Comment> comments, List<Reply> replies) {
        List<Long> latestCommentIds = getLatestCommentIds(boards.size() * comments.size());

        String sql = String.format("""
                INSERT INTO %s (comment_id, member_email, content, created_at, modified_at)
                VALUES (:comment_id, :email, :content, :createdAt, :modifiedAt)
                """, "reply");

        List<MapSqlParameterSource> parametersList = new ArrayList<>();

        for (Long commentId : latestCommentIds) {
            IntStream.range(0, replies.size())
                    .forEach(index -> {
                        MapSqlParameterSource params = new MapSqlParameterSource();
                        params.addValue("comment_id", commentId);
                        params.addValue("email", members.get(index % members.size()).getEmail()); // Cycle through members' emails
                        params.addValue("content", replies.get(index).getContent());
                        params.addValue("createdAt", replies.get(index).getCreatedAt());
                        params.addValue("modifiedAt", replies.get(index).getModifiedAt());
                        parametersList.add(params);
                    });
        }
        namedParameterJdbcTemplate.batchUpdate(sql, parametersList.toArray(new SqlParameterSource[0]));
    }

    public void bulkInsertForLike(List<Board> boards, List<Member> members, List<Likes> likes) {
        if (likes.size() > members.size()) { // likes 는 members 보다 작거나 같아야 함.
            throw new IllegalArgumentException("The size of likes should be lower than or equal to the size of members.");
        }

        List<Long> latestBoardIds = getLatestBoardIds(boards.size());
        List<String> latestMemberIds = getLatestMemberIds(members.size());
        String sql = String.format("""
                INSERT INTO %s (board_id, member_email, created_at, modified_at)
                VALUES (:board_id, :email, :createdAt, :modifiedAt)
                """, "likes");

        List<MapSqlParameterSource> parametersList = new ArrayList<>();

        /** 주의!!!: Likes size <= Members size ? 통과 : ArrayIndexOutOfBoundsException **/
        for (Long boardId : latestBoardIds) {
            IntStream.range(0, likes.size())
                    .forEach(index -> {
                        MapSqlParameterSource params = new MapSqlParameterSource();
                        params.addValue("board_id", boardId);
                        params.addValue("email", latestMemberIds.get(index));
                        params.addValue("createdAt", likes.get(index).getCreatedAt());
                        params.addValue("modifiedAt", likes.get(index).getModifiedAt());
                        parametersList.add(params);
                    });
        }

        namedParameterJdbcTemplate.batchUpdate(sql, parametersList.toArray(new SqlParameterSource[0]));

        String updateLikedCountSQL = """
                UPDATE board 
                SET like_count = (
                    SELECT COUNT(*) 
                    FROM likes 
                    WHERE board_id = board.id
                )
                WHERE id IN (:boardIds)
            """;

        MapSqlParameterSource updateParams = new MapSqlParameterSource();
        updateParams.addValue("boardIds", latestBoardIds);

        namedParameterJdbcTemplate.update(updateLikedCountSQL, updateParams);
    }

    private List<Long> getLatestBoardIds(int limit) {
        String sql = "SELECT id FROM board ORDER BY id DESC LIMIT :limit";
        SqlParameterSource params = new MapSqlParameterSource().addValue("limit", limit);
        return namedParameterJdbcTemplate.queryForList(sql, params, Long.class);
    }

    private List<String> getLatestMemberIds(int limit) {
        String sql = "SELECT email FROM member ORDER BY created_at DESC LIMIT :limit";
        SqlParameterSource params = new MapSqlParameterSource().addValue("limit", limit);
        return namedParameterJdbcTemplate.queryForList(sql, params, String.class);
    }

    private List<Long> getLatestCommentIds(int limit) {
        String sql = "SELECT id from comment ORDER BY id DESC LIMIT :limit";
        SqlParameterSource params = new MapSqlParameterSource().addValue("limit", limit);
        return namedParameterJdbcTemplate.queryForList(sql, params, Long.class);
    }

}
