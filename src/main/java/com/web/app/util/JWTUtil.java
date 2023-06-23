package com.web.app.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class JWTUtil {

    @Value("${com.web.jwt.secret}")
    private String key;

    public String generateToken(Map<String, Object> claims, int days) {

        log.info("============================ Generate Token ================================");

        // headers
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        // claims
        Map<String, Object> payloads = new HashMap<>();
        payloads.putAll(claims);

        // available period
        int time = (60 * 24) * days;

        String jwt = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant()))
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();

        return jwt;
    }

    public Map<String, Object> validateToken(String token) throws JwtException {

        Map<String, Object> claims = null;

        claims = Jwts.parser()
                .setSigningKey(key.getBytes()) // Set Key -> 키가 다를 경우: SignatureException
                .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러 -> 만료 토큰인 경우: ExpiredJwtException
                .getBody();

        return claims;
    }

    public String getEmail(String token) throws JwtException {

        Map<String, Object> claims = null;

        claims = Jwts.parser()
                .setSigningKey(key.getBytes())
                .parseClaimsJws(token)
                .getBody();

        return claims.get("email").toString();
    }

}
