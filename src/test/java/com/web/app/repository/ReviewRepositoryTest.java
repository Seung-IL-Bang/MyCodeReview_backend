package com.web.app.repository;

import com.web.app.domain.review.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;


    @DisplayName("해당 Id 의 Review 를 조회한다.")
    @Test
    void findById() {
        //given
        Review review = Review.builder()
                .content("content")
                .subTitle("subTitle")
                .build();

        Review save = reviewRepository.save(review);

        // when
        Review findOne = reviewRepository.findById(save.getId()).get();

        // then
        assertThat(findOne).isNotNull();
        assertThat(findOne.getContent()).isEqualTo("content");
        assertThat(findOne.getSubTitle()).isEqualTo("subTitle");

    }


}