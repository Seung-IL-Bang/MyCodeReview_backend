package com.web.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewRequestDTO {

    @NotBlank(message = "본문에 글을 작성해주세요.")
    private String content;
    @NotBlank(message = "부제는 필수입니다.")
    private String subTitle;

    @Builder
    public ReviewRequestDTO(String content, String subTitle) {
        this.content = content;
        this.subTitle = subTitle;
    }
}
