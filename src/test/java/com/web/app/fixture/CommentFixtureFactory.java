package com.web.app.fixture;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;

public class CommentFixtureFactory {

    public static Comment create() {
        EasyRandomParameters param = new EasyRandomParameters();
        param.stringLengthRange(3, 10);
        param.excludeField(FieldPredicates.named("board"));
        param.excludeField(FieldPredicates.named("member"));
        return new EasyRandom(param).nextObject(Comment.class);
    }

    public static Comment create(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.stringLengthRange(3, 10);
        param.excludeField(FieldPredicates.named("board"));
        param.excludeField(FieldPredicates.named("member"));
        return new EasyRandom(param).nextObject(Comment.class);
    }

    public static Comment of(Board board, Member member) {
        Comment comment = create();
        return Comment.builder()
                .member(member)
                .board(board)
                .content(comment.getContent())
                .build();
    }

    public static Comment of(Board board, Member member, Long seed) {
        Comment comment = create(seed);
        return Comment.builder()
                .member(member)
                .board(board)
                .content(comment.getContent())
                .build();
    }
}
