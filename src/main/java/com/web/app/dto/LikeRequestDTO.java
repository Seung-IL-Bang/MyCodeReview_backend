package com.web.app.dto;

import com.web.app.domain.board.Board;
import com.web.app.domain.likes.Likes;
import com.web.app.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LikeRequestDTO {

    private Long boardId;
    private String memberEmail;

    public Likes toEntity(Board board, Member member) {
        return Likes.builder()
                .board(board)
                .member(member)
                .build();
    }
}
