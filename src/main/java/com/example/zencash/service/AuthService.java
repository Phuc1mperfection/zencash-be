package com.example.zencash.service;

import com.example.zencash.dto.AuthResponse;
import com.example.zencash.dto.LoginResponse;
import com.example.zencash.dto.RefreshTokenRequest;
import com.example.zencash.entity.Token;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.TokenRepository;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.utils.ErrorCode;
import com.example.zencash.utils.JwtUtil;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public AuthResponse login(LoginResponse loginResponse) {
        User user = userRepository.findByEmail(loginResponse.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(loginResponse.getPasswordRaw(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        // Delete old tokens for the user
        tokenRepository.deleteByUser(user);

        // Generate new Access Token and Refresh Token
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // Save the tokens to the database
        Token token = Token.builder()
                .user(user)
                .token(accessToken) // Access token
                .refreshToken(refreshToken) // Refresh token
                .expired(false)
                .revoked(false)
                .expirationDate(jwtUtil.getExpirationDate(accessToken))
                .refreshExpirationDate(jwtUtil.getExpirationDate(refreshToken))
                .build();
        tokenRepository.save(token);

        return new AuthResponse(user.getUsername(), user.getEmail(), accessToken, refreshToken, user.getFullname());
    }

    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Check if refresh token exists in DB using findByRefreshToken
        Token storedToken = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (storedToken.isRevoked() || storedToken.isExpired()) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Extract user info from token
        String email = jwtUtil.extractEmail(refreshToken);
        String username = jwtUtil.extractUsername(refreshToken);
        String fullname = storedToken.getUser().getFullname();
        // Generate new access token
        String newAccessToken = jwtUtil.generateAccessToken(email, username);

        return new AuthResponse(username, email, newAccessToken, refreshToken,fullname);
    }

    @Transactional
    public void revokeToken(String token) {
        Token storedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REFRESH_TOKEN));

        tokenRepository.delete(storedToken);
    }
}

