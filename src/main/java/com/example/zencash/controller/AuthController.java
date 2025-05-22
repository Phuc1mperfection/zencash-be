package com.example.zencash.controller;

import com.example.zencash.dto.AuthResponse;
import com.example.zencash.dto.ForgotPasswordRequest;
import com.example.zencash.dto.LoginResponse;
import com.example.zencash.dto.RefreshTokenRequest;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.service.AuthService;
import com.example.zencash.service.MailService;
import com.example.zencash.utils.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final MailService mailService;

    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody User user) {

        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginResponse loginResponse) {

        return ResponseEntity.ok(authService.login(loginResponse));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshAccessToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        String token = authHeader.substring(7); // Cắt bỏ "Bearer "
        return ResponseEntity.ok(authService.logout(token));
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        // Gửi email đến admin thông báo user quên mật khẩu
        mailService.sendFeedbackToAdmin(
            request.getEmail(),
            "Người dùng đã yêu cầu cấp lại mật khẩu. Vui lòng xử lý thủ công."
        );
        return ResponseEntity.ok("Yêu cầu khôi phục mật khẩu đã được gửi.");
    }
}
