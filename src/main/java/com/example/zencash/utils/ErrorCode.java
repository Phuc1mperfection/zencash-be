package com.example.zencash.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USERNAME_TAKEN("Username already taken!", 1001),
    EMAIL_TAKEN("Email already registered!", 1002),
    INVALID_INPUT("Invalid input data!", 1003),
    INTERNAL_ERROR("Internal server error!", 1004),
    INVALID_CREDENTIALS("Invalid email or password!", 1006);

    private final String message;
    private final int code;
}
