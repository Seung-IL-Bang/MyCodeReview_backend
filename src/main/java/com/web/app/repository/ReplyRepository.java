package com.web.app.repository;

import com.web.app.domain.comment.Comment;
import com.web.app.domain.reply.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findAllByComment_IdOrderByCreatedAtAsc(Long Id);

    void deleteRepliesByCommentIs(Comment comment);
}
