package com.web.app.security.filter;

import com.google.gson.Gson;
import com.web.app.security.exception.RefreshTokenException;
import com.web.app.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class RefreshTokenCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    private final String refreshPath;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("===================================Refresh Token Check Filter=====================================");

        String path = request.getRequestURI();

        if (!path.equals(refreshPath)) { // 요청 URI 가 refreshPath 와 같지 않으면, refreshToken 에 대해 유효 검사 진행하지 않음.
            log.info("===================================Skip Refresh Token Check Filter===================================");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("=================================Run Refresh Token=================================");

        Map<String, String> tokens = parseRequestJSON(request);

        String refreshToken = tokens.get("refreshToken");

        try {
            Map<String, Object> refreshClaims = checkRefreshToken(refreshToken);

            Integer exp = (Integer) refreshClaims.get("exp");

            Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);
            Date current = new Date(System.currentTimeMillis());

            long getTime = (expTime.getTime() - current.getTime());

            String email = refreshClaims.get("email").toString();
            String role = refreshClaims.get("role").toString();

            // Access Token Update
            String accessToken = jwtUtil.generateToken(Map.of("email", email, "role", role), 1);

            if (getTime < (1000 * 60 * 60 * 24 * 3)) { // refreshToken 만료기간이 3일 이하라면
                log.info("===================================Refresh Token Update===================================");
                refreshToken = jwtUtil.generateToken(Map.of("email", email, "role", role), 30);
            }

            sendTokens(accessToken, refreshToken, response);
        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
        }
    }

    private Map<String, String> parseRequestJSON(HttpServletRequest request) {

        try (Reader reader = new InputStreamReader(request.getInputStream())) {

            Gson gson = new Gson();

            return gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private Map<String, Object> checkRefreshToken(String refreshToken) throws RefreshTokenException {
        try {
            return jwtUtil.validateToken(refreshToken);
        } catch (ExpiredJwtException expiredJwtException) {
            throw new RefreshTokenException(RefreshTokenException.ERROR_CASE.OLD_REFRESH);
        } catch (MalformedJwtException malformedJwtException) {
            throw new RefreshTokenException(RefreshTokenException.ERROR_CASE.NO_REFRESH);
        } catch (Exception exception) {
            new RefreshTokenException(RefreshTokenException.ERROR_CASE.NO_REFRESH);
        }
        return null;
    }

    private void sendTokens(String accessToken, String refreshToken, HttpServletResponse response) {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        String jsonStr = gson.toJson(Map.of("accessToken", accessToken, "refreshToken", refreshToken)); // Map -> JSON 매핑

        try {
            response.getWriter().println(jsonStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
