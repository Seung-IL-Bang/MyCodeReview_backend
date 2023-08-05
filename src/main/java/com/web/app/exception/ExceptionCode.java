package com.web.app.exception;

import lombok.Getter;

public enum ExceptionCode {

    // Likes
    NOT_DUPLICATE_LIKE(406, "해당 게시글은 이미 좋아요를 했습니다."),
    EMPTY_LIKE(404, "이미 좋아요를 취소한 상태입니다.");


    @Getter
    private final int statusCode;

    @Getter
    private final String message;

    ExceptionCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
