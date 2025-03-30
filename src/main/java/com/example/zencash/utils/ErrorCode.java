package com.example.zencash.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USERNAME_TAKEN("Username already taken!", 1001, HttpStatus.CONFLICT),
    EMAIL_TAKEN("Email already registered!", 1002, HttpStatus.CONFLICT),
    INVALID_INPUT("Invalid input data!", 1003, HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("Internal server error!", 1004, HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_CREDENTIALS("Invalid email or password!", 1006, HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("Invalid refresh token", 1005, HttpStatus.FORBIDDEN),
    TOKEN_EXPIRED("Token has expired", 1007, HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("Invalid token!", 1008, HttpStatus.FORBIDDEN);

    private final String message;
    private final int code;
    private final HttpStatus httpStatus;

}
