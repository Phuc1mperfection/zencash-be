package com.example.zencash.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // ==== 1000-1099: AUTH & USER ====
    USERNAME_TAKEN("Username already taken!", 1001, HttpStatus.CONFLICT),
    EMAIL_TAKEN("Email already registered!", 1002, HttpStatus.CONFLICT),
    INVALID_INPUT("Invalid input data!", 1003, HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("Internal server error!", 1004, HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REFRESH_TOKEN("Invalid refresh token", 1005, HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS("Invalid email or password!", 1006, HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("Token has expired", 1007, HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("Invalid token!", 1008, HttpStatus.FORBIDDEN),

    USERNAME_REQUIRED("Username cannot be empty!", 1009, HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED("Email cannot be empty!", 1010, HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_SHORT("Password must be at least 6 characters!", 1011, HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED("Password cannot be empty!", 1012, HttpStatus.BAD_REQUEST),
    PASSWORD_ERROR("The current password is incorrect or the new password does not match.", 1013, HttpStatus.BAD_REQUEST),

    USER_NOT_FOUND("User not found!", 1014, HttpStatus.NOT_FOUND),
    UNAUTHORIZED("Unauthorized access!", 1015, HttpStatus.UNAUTHORIZED),

    // ==== 1100-1199: CATEGORY ====
    CATEGORY_NOT_FOUND("Category not found.", 1101, HttpStatus.BAD_REQUEST),
    CATEGORY_ALREADY_EXISTS("Category already exists", 1102, HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_CATEGORY_ACTION("You do not have permission to perform this action on category.", 1103, HttpStatus.BAD_REQUEST),

    // ==== 1200-1299: CATEGORY GROUP ====
    CATEGORY_GROUP_NOT_FOUND("Category group not found.", 1201, HttpStatus.BAD_REQUEST),
    CATEGORY_GROUP_ALREADY_EXISTS("Category group already exists", 1202, HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_GROUP_ACTION("You do not have permission to perform this action on category group.", 1203, HttpStatus.BAD_REQUEST),

    // ==== 1300-1399: BUDGET ====
    BUDGET_NOT_FOUND("Budget not found", 1301, HttpStatus.BAD_REQUEST),

    // ==== 1400-1499: TRANSACTION ====
    TRANSACTION_NOT_FOUND("Transaction not found", 1401, HttpStatus.BAD_REQUEST);

    private final String message;
    private final int code;
    private final HttpStatus httpStatus;

}
