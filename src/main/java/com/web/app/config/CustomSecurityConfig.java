package com.web.app.config;

import com.web.app.security.handler.CustomSocialLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class CustomSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("================================= filterChain Configuration ================================");

        // formLogin 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);

        // CSRF 토큰 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // OAuth2 Login
        http.oauth2Login(req -> req.loginPage("/login").successHandler(authenticationSuccessHandler()));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("--------------------------------Web Configure--------------------------------");

        return (web -> web.ignoring().requestMatchers(
                PathRequest
                        .toStaticResources()
                        .atCommonLocations()));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 해시 알고리즘으로 패스워드 암호화; 같은 문자열이라도 매번 해시 처리된 결과가 다르다.
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }
}
