package com.web.app.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebRestControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private String PORT;


    @DisplayName("현재 구동되고 있는 애플리케이션의 포트 번호를 확인한다.")
    @Test
    @WithMockUser
    public void getPort() {

        String result = restTemplate.getForObject("/port", String.class);

        assertThat(result).isEqualTo(PORT);
    }
}