package com.web.app.dto;

import lombok.*;

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
    private String writer;
    private String email;

}
