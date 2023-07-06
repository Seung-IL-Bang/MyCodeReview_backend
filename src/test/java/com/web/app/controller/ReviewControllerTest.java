package com.web.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.app.domain.review.Review;
import com.web.app.dto.ReviewDTO;
import com.web.app.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
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


    // TODO : 인가를 위한 Authorization 헤더 JWT 토큰 추가
    @DisplayName("Review ID 로 해당 Review 를 조회한다.")
    @Test
    void getReview() throws Exception {
        //given
        Long id = 1L;
        ReviewDTO reviewDTO = ReviewDTO.builder()
                .id(id)
                .subTitle("subtitle")
                .content("content")
                .build();

        when(reviewService.read(id)).thenReturn(reviewDTO);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/board/review/1")
                        .header("Authorization"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtitle").value("subtitle"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.id").value(1L));
    }
}