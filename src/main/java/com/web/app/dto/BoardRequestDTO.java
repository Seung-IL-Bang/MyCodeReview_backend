package com.web.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardRequestDTO {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "본문에 글을 작성해주세요.")
    private String content;

    private Set<String> tagList;

    private String link;

    @NotBlank(message = "난이도를 선택해주세요.")
    private String difficulty;

    @NotBlank(message = "작성자를 알 수 없습니다.")
    private String writer;

    @NotBlank(message = "작성자의 이메일을 확인할 수 없습니다.")
    private String email;
}
