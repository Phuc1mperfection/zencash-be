package com.example.zencash.exception;

import com.example.zencash.utils.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Xử lý lỗi AppException theo ErrorCode
    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, Object>> handleAppException(AppException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", ex.getErrorCode().getMessage());
        response.put("code", ex.getErrorCode().getCode());
        response.put("status", ex.getErrorCode().getHttpStatus().value());
        response.put("path", request.getDescription(false));

        return new ResponseEntity<>(response, ex.getErrorCode().getHttpStatus());
    }

    // Xử lý lỗi @Valid nếu dữ liệu request không hợp lệ
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(jakarta.validation.ConstraintViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Invalid input data!");
        response.put("errors", ex.getConstraintViolations().stream().map(v -> v.getMessage()).toList());
        response.put("status", 400);

        return ResponseEntity.badRequest().body(response);
    }

    // Xử lý lỗi chung (Ví dụ: NullPointerException)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Internal server error!");
        response.put("status", 500);
        response.put("path", request.getDescription(false));

        return ResponseEntity.internalServerError().body(response);
    }
}
