package com.example.zencash.exception;

import com.example.zencash.utils.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // Add this method to retrieve the HTTP status
    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}