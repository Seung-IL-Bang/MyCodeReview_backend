package com.web.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.app.dto.ReviewResponseDTO;
import com.web.app.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private ModelMapper modelMapper;



    @DisplayName("Review ID 로 해당 Review 를 조회 한다.")
    @Test
    @WithMockUser
    void getReview() throws Exception {
        //given
        Long id = 1L;
        ReviewResponseDTO reviewResponseDTO = ReviewResponseDTO.builder()
                .id(id)
                .subTitle("subtitle")
                .content("content")
                .build();

        given(reviewService.read(id)).willReturn(reviewResponseDTO);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/auth/board/review/%d", id))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subTitle").value("subtitle"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.id").value(1L));
    }
}