package com.web.app.fixture;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import com.web.app.domain.reply.Reply;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;

public class ReplyFixtureFactory {

    public static Reply create() {
        EasyRandomParameters param = new EasyRandomParameters();
        param.stringLengthRange(3, 10);
        param.excludeField(FieldPredicates.named("comment"));
        param.excludeField(FieldPredicates.named("member"));
        return new EasyRandom(param).nextObject(Reply.class);
    }

    public static Reply create(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.stringLengthRange(3, 10);
        param.excludeField(FieldPredicates.named("comment"));
        param.excludeField(FieldPredicates.named("member"));
        return new EasyRandom(param).nextObject(Reply.class);
    }

    public static Reply of(Comment comment, Member member) {
        Reply reply = create();
        return Reply.builder()
                .member(member)
                .comment(comment)
                .content(reply.getContent())
                .build();
    }

    public static Reply of(Comment comment, Member member, Long seed) {
        Reply reply = create(seed);
        return Reply.builder()
                .member(member)
                .comment(comment)
                .content(reply.getContent())
                .build();
    }
}
