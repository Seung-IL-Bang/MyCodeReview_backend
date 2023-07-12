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
public class BoardResponseDTO {

        private Long id;
        private String title;
        private String content;
        private Set<String> tagList;
        private List<ReviewListDTO> reviewList;
        private String link;
        private String difficulty;
        private String writer;
        private String email;
}
