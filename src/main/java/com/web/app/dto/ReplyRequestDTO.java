package com.web.app.dto;

import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import com.web.app.domain.reply.Reply;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReplyRequestDTO {

    private Long id; // 수정 요청시 사용
    private Long commentId;

    @NotBlank
    private String memberEmail;

    @NotBlank
    private String content;

    public Reply toEntity(Long id, Member member, Comment comment) {

        return Reply.builder()
                .id(id)
                .comment(comment)
                .member(member)
                .content(this.content)
                .build();
    }
}
