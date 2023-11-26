package com.web.app.dto;

import com.web.app.domain.board.Board;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardListResponseDTO {

    private Long id;
    private String title;
    private Set<String> tagList;
    private String difficulty;
    private int likeCount;
    private String writer;
    private String email;

    public static BoardListResponseDTO of(Board board) {
        //  tagList를 새로운 Set 또는 List에 복사하여 DTO에 저장합니다. 이렇게 하면 원본 엔터티의 프록시 컬렉션 대신 실제 데이터가 포함된 새로운 컬렉션이 DTO에 저장됩니다.
        Set<String> tags = new HashSet<>(board.getTagList());
        return BoardListResponseDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .tagList(tags)
                .difficulty(board.getDifficulty())
                .writer(board.getWriter())
                .email(board.getEmail())
                .likeCount(board.getLikeCount())
                .build();
    }

}
