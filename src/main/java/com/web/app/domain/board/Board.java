package com.web.app.domain.board;

import com.web.app.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
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

    @Column
    private String link;

    @ElementCollection
    private Set<String> tagList;


    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }


}
