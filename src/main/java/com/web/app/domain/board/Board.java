package com.web.app.domain.board;

import com.web.app.domain.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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

    @Size(min = 1, max = 200, message = "제목의 글자 수는 200 자를 넘어서면 안됩니다.")
    @Column(length = 200, nullable = false)
    private String title;

    @Size(min = 1, max = 3000, message = "본문의 글자 수는 3000 자를 넘어서면 안됩니다.")
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

    @Column
    @Min(value = 0)
    private int likeCount = 0;

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
        this.likeCount += 1;
    }

    public void downLike() {
        this.likeCount -= 1;
    }


}
