package com.web.app.dto;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentRequestDTO {

    private String content;
    private Long boardId;
    private String memberEmail;

    public Comment toEntity(Board board, Member member, String content) {
        return Comment.builder()
                .content(content)
                .board(board)
                .member(member)
                .build();
    }
}
