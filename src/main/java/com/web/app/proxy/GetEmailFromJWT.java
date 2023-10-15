package com.web.app.proxy;

import com.web.app.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetEmailFromJWT {

    private final JWTUtil jwtUtil;

    public String execute(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            return "";
        }

        String accessToken = authorization.substring(7);

        return jwtUtil.getEmail(accessToken);
    }
}
