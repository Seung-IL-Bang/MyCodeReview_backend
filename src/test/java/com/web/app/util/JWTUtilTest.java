package com.web.app.util;

import com.web.app.config.CustomSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class JWTUtilTest {

    @Autowired
    JWTUtil jwtUtil;


    @DisplayName("회원의 권한과 이메일의 정보를 가진 JWT 토큰이 정상 발행된다.")
    @Test
    void generateToken() {
        //given
        Map<String, Object> claims = Map.of("email", "test@gmail.com", "role", "ROLE_USER");

        // when
        String token = jwtUtil.generateToken(claims, 1);

        // then
        assertThat(token).isNotBlank();
    }

    @DisplayName("권한이 필요한 API 요청이 들어온 경우 토큰이 유효한지 검증한다.")
    @Test
    void validateToken() {
        //given
        Map<String, Object> claims = Map.of("email", "test@gmail.com", "role", "ROLE_USER");
        String token = jwtUtil.generateToken(claims, 1);


        // when
        Map<String, Object> result = jwtUtil.validateToken(token);

        // then
        assertThat(result.get("email")).isEqualTo("test@gmail.com");
        assertThat(result.get("role")).isEqualTo("ROLE_USER");
    }

}