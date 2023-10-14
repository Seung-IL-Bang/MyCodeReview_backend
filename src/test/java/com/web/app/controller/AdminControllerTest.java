package com.web.app.controller;

import com.web.app.ControllerTestSupport;
import com.web.app.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AdminControllerTest extends ControllerTestSupport {


    @DisplayName("사용자 권한에 따른 /auth/admin/** 경로의 적절한 HttpStatus 응답이 가능하다.")
    @ParameterizedTest
    @ValueSource(ints = {200, 401, 403})
    @WithMockUser
    void WhenAdminAuthorityThenAuthorized(int statusCode) throws Exception {
        // given
        HttpStatus status = HttpStatus.resolve(statusCode);
        ApiResponse<Object> response = ApiResponse.of(status, status.name(), null);

        // when
        given(adminService.testAdminAuthority(any(HttpServletRequest.class))).willReturn(response);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/admin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(status.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(status.name().toUpperCase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(status.name()));
    }


}