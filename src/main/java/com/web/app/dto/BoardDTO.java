package com.web.app.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {

    private Long id;
    private String title;
    private String content;
    private Set<String> tagList;
    private String link;
    private String difficulty;
    private String writer;
    private String email;
}
