package com.example.zencash.service;

import com.example.zencash.dto.AuthResponseDTO;
import com.example.zencash.dto.LoginDTO;
import com.example.zencash.entity.Token;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.TokenRepository;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.utils.ErrorCode;
import com.example.zencash.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
            userRepository.save(user);
            return "User registered successfully!";
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }
    }
    public AuthResponseDTO login(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(loginDTO.getPasswordRaw(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        tokenRepository.deleteByUser(user);

        String token = jwtUtil.generateAccessToken(user); // Đổi tên biến từ accessToken thành token
        String refreshToken = jwtUtil.generateRefreshToken(user);

        Token tokenEntity = Token.builder()
                .token(token)
                .expirationDate(jwtUtil.getExpirationDate(token))
                .refreshToken(refreshToken)
                .refreshExpirationDate(jwtUtil.getExpirationDate(refreshToken))
                .revoked(false)
                .expired(false)
                .user(user)
                .build();
        tokenRepository.save(tokenEntity);

        return new AuthResponseDTO(user.getUsername(), user.getEmail(), token);
    }

}

