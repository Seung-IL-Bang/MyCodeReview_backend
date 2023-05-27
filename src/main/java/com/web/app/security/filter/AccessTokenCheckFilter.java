package com.web.app.security.filter;

import com.web.app.security.auth.CustomOAuth2UserService;
import com.web.app.security.exception.AccessTokenException;
import com.web.app.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class AccessTokenCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("=================================== Access Token Check Filter =====================================");

        String path = request.getRequestURI();

        if (!path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // '/auth/**' 모든 경로에 대해 액세스 토큰 유효성 검사
        try {
            validateAccessToken(request); // Access Token 유효성 검증
            filterChain.doFilter(request, response);
        } catch (AccessTokenException accessTokenException) {
            log.info("----------------- AccessToken Exception -----------------");
            accessTokenException.sendResponseError(response);
        }
    }

    private void validateAccessToken(HttpServletRequest request) throws AccessTokenException {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || authorization.length() < 8) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }

        String tokenType = authorization.substring(0, 6); // slicing: Bearer
        String tokenBody = authorization.substring(7); // slicing: accessToken

        if (!tokenType.equals("Bearer")) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
        }

        try {
            jwtUtil.validateToken(tokenBody);
        } catch (MalformedJwtException malformedJwtException) {
            log.info("MalformedJwtException---------------------------------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);
        } catch (SignatureException signatureException) {
            log.info("SignatureException------------------------------------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);
        } catch (ExpiredJwtException expiredJwtException) {
            log.info("ExpiredJwtException-----------------------------------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
        }
    }
}
