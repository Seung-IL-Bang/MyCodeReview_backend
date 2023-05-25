package com.web.app.security.handler;

import com.google.gson.Gson;
import com.web.app.security.dto.MemberSecurityDTO;
import com.web.app.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final PasswordEncoder passwordEncoder;
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

        Gson gson = new Gson();

        Map<String, Object> tokenMap = Map.of("accessToken", accessToken, "refreshToken", refreshToken);

        String json = gson.toJson(tokenMap);

        response.getWriter().println(json);
//        response.sendRedirect("/");
    }
}
