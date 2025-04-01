package com.example.zencash.service;

import com.example.zencash.dto.UpdateUserRequest;
import com.example.zencash.dto.UserResponse;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.TokenRepository;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.utils.ErrorCode;
import com.example.zencash.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
    }

    // Lấy thông tin người dùng
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        return new UserResponse(user.getEmail(), user.getUsername(), user.getName());
    }

    // Cập nhật thông tin người dùng
    public UserResponse updateUser(String email, UpdateUserRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new AppException(ErrorCode.EMAIL_TAKEN);
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
        return new UserResponse(user.getEmail(), user.getUsername(), user.getName());
    }

    // Xác minh mật khẩu người dùng
    public boolean verifyPassword(String email, String enteredPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        return passwordEncoder.matches(enteredPassword, user.getPassword());
    }

    public void deactivateAccount(User user) {
        user.setActive(false); // Đánh dấu tài khoản là không hoạt động
        userRepository.save(user); // Lưu thay đổi
    }
}
