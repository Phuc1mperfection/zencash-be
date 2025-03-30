package com.example.zencash.service;

import com.example.zencash.dto.AuthResponse;
import com.example.zencash.dto.LoginResponse;
import com.example.zencash.dto.RefreshTokenRequest;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.TokenRepository;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.utils.ErrorCode;
import com.example.zencash.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service

@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;

    public String register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_TAKEN);
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_TAKEN);
        }

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Collections.singleton("USER"));
            userRepository.save(user);
            return "User registered successfully!";
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }
    }
    public AuthResponse login(LoginResponse loginResponse) {
        User user = userRepository.findByEmail(loginResponse.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(loginResponse.getPasswordRaw(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        // Tạo Access Token và Refresh Token
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new AuthResponse(user.getUsername(), user.getEmail(), accessToken, refreshToken);
    }
    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Kiểm tra refresh token hợp lệ hay không
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Lấy thông tin user từ refresh token
        String email = jwtUtil.extractEmail(refreshToken);
        String username = jwtUtil.extractUsername(refreshToken);

        // Tạo Access Token mới
        String newAccessToken = jwtUtil.generateAccessToken(email, username);

        return new AuthResponse(username, email, newAccessToken, refreshToken);
    }

}

