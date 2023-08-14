package com.web.app.dto;

import com.web.app.domain.review.Review;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewResponseDTO {

    private Long id;
    private Long boardId;
    private String subTitle;
    private String title;
    private String content;
    private Set<String> tagList;
    private List<ReviewListDTO> reviewList;
    private List<CommentResponseDTO> commentList;
    private String link;
    private String difficulty;
    private String writer;
    private String email;


}
