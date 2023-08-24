package com.web.app.dto;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDTO {

    private Long id; // 수정 요청시 사용
    private String content;
    private Long boardId;

    @NotBlank
    private String memberEmail;

    public Comment toEntity(Long id, Board board, Member member, String content) {
        return Comment.builder()
                .id(id)
                .content(content)
                .board(board)
                .member(member)
                .build();
    }
}
