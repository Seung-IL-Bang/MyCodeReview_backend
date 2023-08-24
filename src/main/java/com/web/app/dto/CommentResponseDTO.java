package com.web.app.dto;

import com.web.app.domain.comment.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {

    private Long id;
    private String content;
    private String memberEmail;
    private String memberName;
    private int repliesCount;
    private List<ReplyResponseDTO> replies;
    private boolean myComment;
    private LocalDateTime modifiedAt;

    public CommentResponseDTO(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.memberName = comment.getMember().getName();
        this.memberEmail = comment.getMember().getEmail();
        this.modifiedAt = comment.getModifiedAt();
    }

    public CommentResponseDTO(Comment comment, String requestEmail) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.memberName = comment.getMember().getName();
        this.memberEmail = comment.getMember().getEmail();
        this.modifiedAt = comment.getModifiedAt();

        if (comment.getMember().getEmail().equals(requestEmail)) {
            this.myComment = true;
        } else {
            this.myComment = false;
        }
    }

}
