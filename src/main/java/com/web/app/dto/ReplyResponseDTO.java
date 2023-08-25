package com.web.app.dto;

import com.web.app.domain.reply.Reply;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyResponseDTO {

    private Long id;
    private String content;
    private String memberEmail;
    private String memberName;
    private boolean myReply;
    private LocalDateTime modifiedAt;

    public ReplyResponseDTO(Reply reply) {
        this.id = reply.getId();
        this.content = reply.getContent();
        this.memberName = reply.getMember().getName();
        this.memberEmail = reply.getMember().getEmail();
        this.modifiedAt = reply.getModifiedAt();
    }

    public ReplyResponseDTO(Reply reply, String requestEmail) {

        this.id = reply.getId();
        this.content = reply.getContent();
        this.memberEmail = reply.getMember().getEmail();
        this.memberName = reply.getMember().getName();
        this.modifiedAt = reply.getModifiedAt();

        if (reply.getMember().getEmail().equals(requestEmail)) {
            this.myReply = true;
        } else {
            this.myReply = false;
        }
    }

}
