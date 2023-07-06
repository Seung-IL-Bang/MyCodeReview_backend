package com.web.app.domain.review;

import com.web.app.domain.BaseTimeEntity;
import com.web.app.domain.board.Board;
import com.web.app.dto.ReviewResponseDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String subTitle;

    @Column(length = 3000, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;


    public void change(String subTitle, String content) {
        this.subTitle = subTitle;
        this.content = content;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public ReviewResponseDTO toResponseDTO() {
        return ReviewResponseDTO.builder()
                .id(id)
                .boardId(board.getId())
                .title(board.getTitle())
                .subTitle(subTitle)
                .content(content)
                .tagList(board.getTagList())
                .link(board.getLink())
                .difficulty(board.getDifficulty())
                .writer(board.getWriter())
                .email(board.getEmail())
                .build();
    }


}
