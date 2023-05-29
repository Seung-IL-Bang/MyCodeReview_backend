package com.web.app.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private String email;
}
