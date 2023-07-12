package com.web.app.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewRequestDTO {

    private String content;
    private String subTitle;

    @Builder
    public ReviewRequestDTO(String content, String subTitle) {
        this.content = content;
        this.subTitle = subTitle;
    }
}
