package com.web.app.mediator;

import com.web.app.domain.board.Board;
import com.web.app.service.BoardService;
import com.web.app.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetEmailFromJWT {

    private final JWTUtil jwtUtil;
    private final BoardService boardService;

    public String execute(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        String accessToken = authorization.substring(7);

        String email = jwtUtil.getEmail(accessToken);

        return email;
    }
}
