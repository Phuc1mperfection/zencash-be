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
    INVALID_TOKEN("Invalid token!", 1008, HttpStatus.FORBIDDEN),
    USERNAME_REQUIRED("Username cannot be empty!", 1009, HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED("Email cannot be empty!", 1010, HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_SHORT("Password must be at least 6 characters!", 1011, HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED("Password cannot be empty!", 1012, HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("User not found!", 1009, HttpStatus.NOT_FOUND),
    UNAUTHORIZED("Unauthorized access!", 1010, HttpStatus.UNAUTHORIZED),
    PASSWORD_ERROR("The current password is incorrect or the new password does not match.", 1013, HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND("Category not found.",1014, HttpStatus.BAD_REQUEST),
    CATEGORY_GROUP_NOT_FOUND("Category group not found.",1015, HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_CATEGORY_ACTION("You do not have permission to perform this action on category.", 1016, HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_GROUP_ACTION("You do not have permission to perform this action on category group.", 1017, HttpStatus.BAD_REQUEST),
    BUDGET_NOT_FOUND("Budget not found",1018 ,HttpStatus.BAD_REQUEST );


    private final String message;
    private final int code;
    private final HttpStatus httpStatus;

}
