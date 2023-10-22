package com.web.app.dto;

import com.web.app.domain.board.Board;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponseDTO {

    private Long id;
    private String title;
    private String content;
    private Set<String> tagList;
    private List<ReviewListDTO> reviewList;
    private List<CommentResponseDTO> commentList;
    private int commentsCount;
    private String link;
    private String difficulty;
    private String writer;
    private String email;
    private int likeCount;
    private boolean isLiked;
    private boolean myBoard;

    public static BoardResponseDTO of(Board board) {
        //  tagList를 새로운 Set 또는 List에 복사하여 DTO에 저장합니다. 이렇게 하면 원본 엔터티의 프록시 컬렉션 대신 실제 데이터가 포함된 새로운 컬렉션이 DTO에 저장됩니다.
        Set<String> tags = new HashSet<>(board.getTagList());
        return BoardResponseDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .tagList(tags)
                .difficulty(board.getLink())
                .writer(board.getWriter())
                .email(board.getEmail())
                .likeCount(board.getLikeCount())
                .build();
    }
}
