package com.web.app.domain.board;

import com.web.app.domain.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(length = 3000, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;

    @Column(length = 50, nullable = false)
    private String email;

    @Column(length = 10)
    private String difficulty;

    @Column
    private String link;

    @ElementCollection
    private Set<String> tagList;

    @Min(value = 0L)
    private Long likeCount = 0L;

    @Version
    private Integer version = 0;

    public void change(String title, String content, Set<String> tagList, String link, String difficulty) {
        this.title = title;
        this.content = content;
        this.tagList = tagList;
        this.link = link;
        this.difficulty = difficulty;
    }


    public void upLike() {
        this.likeCount += 1L;
    }

    public void downLike() {
        this.likeCount -= 1L;
    }


}
