package com.web.app.dto;

import com.web.app.domain.comment.Comment;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime modifiedAt;

    public CommentResponseDTO(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.memberName = comment.getMember().getName();
        this.memberEmail = comment.getMember().getEmail();
        this.modifiedAt = comment.getModifiedAt();
    }

}
