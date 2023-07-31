package com.web.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.app.domain.review.Review;
import com.web.app.dto.ReviewRequestDTO;
import com.web.app.dto.ReviewResponseDTO;
import com.web.app.fixture.ReviewFixtureFactory;
import com.web.app.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private ModelMapper modelMapper;



    @DisplayName("Review ID 로 해당 Review 를 조회 한다.")
    @Test
    @WithMockUser
    void getReview() throws Exception {
        //given
        ReviewResponseDTO reviewResponseDTO = ReviewFixtureFactory.createResponseDTO();

        given(reviewService.read(any(Long.class))).willReturn(reviewResponseDTO);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/auth/board/review/%d", reviewResponseDTO.getId()))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subTitle").value(reviewResponseDTO.getSubTitle()))
                .andExpect(jsonPath("$.content").value(reviewResponseDTO.getContent()))
                .andExpect(jsonPath("$.id").value(reviewResponseDTO.getId()));
    }


    @DisplayName("특정 Board 게시글에 Sub-Review 를 추가한다.")
    @Test
    @WithMockUser
    void postReview() throws Exception {
        // given
        ReviewRequestDTO reviewRequestDTO = ReviewFixtureFactory.createRequestDTO();

        given(reviewService.register(any(Review.class), any(Long.class)))
                .willReturn(any(Long.class));


        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/auth/board/review/%d", any(Long.class)))
                        .content(objectMapper.writeValueAsString(reviewRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("해당 리뷰 Id 의 리뷰글을 수정할 수 있다.")
    @Test
    @WithMockUser
    void putReview() throws Exception {
        // given
        Long reviewId = any(Long.class);
        ReviewRequestDTO reviewRequestDTO = ReviewFixtureFactory.createRequestDTO();

        given(reviewService.modify(any(ReviewRequestDTO.class), reviewId))
                .willReturn(null);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.put(String.format("/auth/board/review/%d", reviewId))
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(reviewRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Updated Id: %d Review", reviewId)));
    }

    @DisplayName("해당 리뷰 Id 의 리뷰글을 삭제할 수 있다.")
    @Test
    @WithMockUser
    void deleteReview() throws Exception {
        // given
        willDoNothing().given(reviewService).remove(any(Long.class));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/auth/board/review/%d", 1L))
                        .with(csrf()))
                .andDo(print())
                .andExpect(content().string(String.format("Deleted %d Review", 1L)));
    }
}