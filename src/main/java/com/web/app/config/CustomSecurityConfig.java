package com.web.app.config;

import com.web.app.security.filter.AccessTokenCheckFilter;
import com.web.app.security.filter.RefreshTokenCheckFilter;
import com.web.app.security.handler.CustomSocialLoginSuccessHandler;
import com.web.app.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class CustomSecurityConfig {

    private final JWTUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("=================================filterChain Configuration================================");

        // formLogin 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);

        // CSRF 토큰 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // CORS 설정
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 세션 비활성화 -> 소셜 로그인 후에도 인증 정보가 없다.
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // authentication 전 '/auth/**' 경로에 대해 accessToken 유효성 검증
        http.addFilterBefore(accessTokenCheckFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // authentication 전 '/refreshPath' 경로에 대해 refreshToken 검사 후 Token 재발급 과정 수행
        http.addFilterBefore(refreshTokenCheckFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // OAuth2 Login
        http.oauth2Login(req -> req.loginPage("/login").successHandler(authenticationSuccessHandler()));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("=================================Web Configure=================================");

        return (web -> web.ignoring().requestMatchers(
                PathRequest
                        .toStaticResources()
                        .atCommonLocations()));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("=================================PasswordEncoder=================================");
        return new BCryptPasswordEncoder(); // 해시 알고리즘으로 패스워드 암호화; 같은 문자열이라도 매번 해시 처리된 결과가 다르다.
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        log.info("=================================AuthenticationSuccessHandler=================================");
        return new CustomSocialLoginSuccessHandler(jwtUtil);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("=================================CorsConfigurationSource=================================");
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    private AccessTokenCheckFilter accessTokenCheckFilter(JWTUtil jwtUtil) {
        return new AccessTokenCheckFilter(jwtUtil);
    }

    private RefreshTokenCheckFilter refreshTokenCheckFilter(JWTUtil jwtUtil) {
        return new RefreshTokenCheckFilter(jwtUtil, "/refreshPath");
    }
}
