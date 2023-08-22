package com.web.app.repository;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import com.web.app.domain.reply.Reply;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.fixture.CommentFixtureFactory;
import com.web.app.fixture.MemberFixtureFactory;
import com.web.app.fixture.ReplyFixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class ReplyRepositoryTest {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private CommentRepository commentRepository;
    
    @DisplayName("답글을 작성할 수 있다.")
    @Test
    void saveReply() {
        // given
        Board board = BoardFixtureFactory.createById(1L);
        Member member = MemberFixtureFactory.create();
        Comment comment = CommentFixtureFactory.of(board, member);

        Comment savedComment = commentRepository.save(comment);
        Reply reply = ReplyFixtureFactory.of(savedComment, member);

        // when
        Reply saved = replyRepository.save(reply);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getContent()).isEqualTo(reply.getContent());
    }
}