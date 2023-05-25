package com.web.app.config;

import com.web.app.security.handler.CustomSocialLoginSuccessHandler;
import com.web.app.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class CustomSecurityConfig {

    private final JWTUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("================================= filterChain Configuration ================================");

        // formLogin 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);

        // CSRF 토큰 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // 세션 비활성화 -> 소셜 로그인 후에도 인증 정보가 없다.
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // authorizeHttpRequests 권한 설정
        // 'ROLE_' is automatically prepended when using hasRole
//        http
//                .authorizeHttpRequests(authz -> {
//                    authz.requestMatchers(HttpMethod.GET, "/auth/**").hasRole("USER")
//                            .requestMatchers(HttpMethod.GET, "/auth2/**").hasRole("USER")
//                            .anyRequest().permitAll(); // 나머지 경로는 모두 허용 [필수]
//                });

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
        return new CustomSocialLoginSuccessHandler(passwordEncoder(), jwtUtil);
    }
}
