package com.web.app.security.exception;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class RefreshTokenException extends RuntimeException {

    private ERROR_CASE error_case;

    public enum ERROR_CASE {
        No_ACCESS,
        BAD_ACCESS,
        NO_REFRESH,
        OLD_REFRESH,
        BAD_REFRESH
    }

    public RefreshTokenException(ERROR_CASE error_case) {
        super(error_case.name());
        this.error_case = error_case;
    }

    public void sendResponseError(HttpServletResponse response) {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        String responseStr = gson.toJson(Map.of("message", error_case.name(), "time", new Date()));

        try {
            response.getWriter().println(responseStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
