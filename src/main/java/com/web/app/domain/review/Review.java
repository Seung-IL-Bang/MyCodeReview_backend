package com.web.app.domain.review;

import com.web.app.domain.BaseTimeEntity;
import com.web.app.domain.board.Board;
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

    @ManyToOne
    private Board board;


    public void change(String subTitle, String content) {
        this.subTitle = subTitle;
        this.content = content;
    }




}
