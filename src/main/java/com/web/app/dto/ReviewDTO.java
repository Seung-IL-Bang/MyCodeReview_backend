package com.web.app.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewDTO {

    private Long id;
    private String subTitle;
    private String content;
}
