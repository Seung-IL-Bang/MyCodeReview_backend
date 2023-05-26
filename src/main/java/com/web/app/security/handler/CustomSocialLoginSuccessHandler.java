package com.web.app.security.handler;

import com.web.app.security.dto.MemberSecurityDTO;
import com.web.app.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("========================== Social Login Success Handler ==========================");

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        MemberSecurityDTO principal = (MemberSecurityDTO) authentication.getPrincipal();

        Map<String, Object> claims = Map.of(
                "email", principal.getEmail()
                , "role", principal.getRole());

        String accessToken = jwtUtil.generateToken(claims, 1);
        String refreshToken = jwtUtil.generateToken(claims, 30);

        String uri = UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host("localhost")
                .port(3000)
                .path("/")
                .build()
                .toUri()
                .toString();


        Cookie[] cookies = {
                new Cookie("accessToken", accessToken),
                new Cookie("refreshToken", refreshToken)
        };

        for (Cookie cookie : cookies) {
            cookie.setMaxAge(30 * 60);
            cookie.setDomain("localhost");
            cookie.setPath("/");
            cookie.setSecure(true);
            response.addCookie(cookie);
        }

        getRedirectStrategy().sendRedirect(request, response, uri);
    }
}
