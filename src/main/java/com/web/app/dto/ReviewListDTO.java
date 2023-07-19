package com.web.app.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewListDTO {

    private Long id;
    private String content;
    private String subTitle;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
