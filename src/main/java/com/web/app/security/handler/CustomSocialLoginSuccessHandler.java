package com.web.app.security.handler;

import com.web.app.security.dto.MemberSecurityDTO;
import com.web.app.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Slf4j(topic = "kafka-logger")
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.domain.scheme}")
    private String SCHEME;

    @Value("${app.domain.host}")
    private String HOST;

    @Value("${app.domain.port}")
    private int PORT;

    @Value("${spring.profiles.active}")
    private String PROFILE;

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        MemberSecurityDTO principal = (MemberSecurityDTO) authentication.getPrincipal();

        Map<String, Object> claims = Map.of(
                "email", principal.getEmail()
                , "role", principal.getRole());

        String accessToken = jwtUtil.generateToken(claims, 1);
        String refreshToken = jwtUtil.generateToken(claims, 30);

        if (PROFILE.equals("prod")) {
            String uri = UriComponentsBuilder
                    .newInstance()
                    .scheme(SCHEME)
                    .host(HOST)
//                    .port(PORT)
                    .path("/tokens")
                    .queryParam("accessToken", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .build()
                    .toUri()
                    .toString();


            getRedirectStrategy().sendRedirect(request, response, uri);

        } else if (PROFILE.equals("local") || PROFILE.equals("test")) {
            String uri = UriComponentsBuilder
                    .newInstance()
                    .scheme(SCHEME)
                    .host(HOST)
                    .port(PORT)
                    .path("/tokens")
                    .queryParam("accessToken", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .build()
                    .toUri()
                    .toString();


            getRedirectStrategy().sendRedirect(request, response, uri);
            log.info(String.format("LOGIN SUCCESS: name=%s, email=%s, role=%s",
                    principal.getName(),
                    principal.getEmail(),
                    principal.getRole()));
        }
    }
}
