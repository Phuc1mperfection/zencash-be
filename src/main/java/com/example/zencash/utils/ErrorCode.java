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
    FILE_UPLOAD_EMPTY("Uploaded file is empty", 1009, HttpStatus.BAD_REQUEST),

    USERNAME_REQUIRED("Username cannot be empty!", 1009, HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED("Email cannot be empty!", 1010, HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_SHORT("Password must be at least 6 characters!", 1011, HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED("Password cannot be empty!", 1012, HttpStatus.BAD_REQUEST),
    PASSWORD_ERROR("The current password is incorrect or the new password does not match.", 1013,
            HttpStatus.BAD_REQUEST),
    PASSWORD_ERROR1("The new password is the same as the new pasword .", 1014,
            HttpStatus.BAD_REQUEST),

    USER_NOT_FOUND("User not found!", 1014, HttpStatus.NOT_FOUND),
    UNAUTHORIZED("Unauthorized access!", 1015, HttpStatus.UNAUTHORIZED),
    ACCOUNT_DISABLED("Account is disabled!", 1016, HttpStatus.FORBIDDEN),

    // ==== 1100-1199: CATEGORY ====
    CATEGORY_NOT_FOUND("Category not found.", 1101, HttpStatus.NOT_FOUND),
    CATEGORY_ALREADY_EXISTS("Category already exists", 1102, HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_CATEGORY_ACTION("You do not have permission to perform this action on category.", 1103,
            HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_DEFAULT_CATEGORY_ACTION("You do not have permission to perform this action on default category.", 1104,
            HttpStatus.BAD_REQUEST),
    ICON_NOT_FOUND("Icon not found", 1105, HttpStatus.BAD_REQUEST),

    // ==== 1200-1299: CATEGORY GROUP ====
    CATEGORY_GROUP_NOT_FOUND("Category group not found.", 1201, HttpStatus.NOT_FOUND),
    CATEGORY_GROUP_ALREADY_EXISTS("Category group already exists", 1202, HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_CATEGORY_GROUP_ACTION("You do not have permission to perform this action on category group.", 1203,
            HttpStatus.BAD_REQUEST),

    // ==== 1300-1399: BUDGET ====
    BUDGET_NOT_FOUND("Budget not found.", 1301, HttpStatus.NOT_FOUND),
    UNAUTHORIZED_BUDGET_ACTION("Unauthorized budget action!", 1302, HttpStatus.BAD_REQUEST),
    BUDGET_NAME_ALREADY_EXISTS("Budget already exists.", 1303, HttpStatus.BAD_REQUEST),
    INVALID_AMOUNT("Invalid amount.", 1304, HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_BUDGET_ACCESS("You do not have permission to perform this action on budget.", 1305,
            HttpStatus.BAD_REQUEST),

    // ==== 1400-1499: TRANSACTION ====
    TRANSACTION_NOT_FOUND("Transaction not found", 1401, HttpStatus.NOT_FOUND),
    INVALID_DATA("Money not found", 1402, HttpStatus.NOT_FOUND),
    ACCESS_DENIED("Access denied", 1404, HttpStatus.FORBIDDEN),
    AI_RESPONSE_INVALID("AI not found", 1403, HttpStatus.BAD_REQUEST),
    // ==== 1500-1599: GOAL ====
    GOAL_NOT_FOUND("Goal not found", 1501, HttpStatus.NOT_FOUND);

    private final String message;
    private final int code;
    private final HttpStatus httpStatus;

}
