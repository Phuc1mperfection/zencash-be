package com.example.zencash.controller;

import com.example.zencash.dto.UpdateUserRequest;
import com.example.zencash.dto.UserResponse;
import com.example.zencash.exception.AppException;
import com.example.zencash.entity.User;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.utils.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        UserResponse response = new UserResponse(
                user.getEmail(), user.getUsername(), user.getName());

        return ResponseEntity.ok(response);
    }


    @PutMapping("/me")
    public User updateUser(@AuthenticationPrincipal User currentUser,
                           @RequestBody @Valid UpdateUserRequest request) {
        // Tìm user trong database
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));

        // Cập nhật thông tin nếu có
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            // Kiểm tra email đã tồn tại chưa
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new AppException(ErrorCode.EMAIL_TAKEN);
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Lưu user mới vào database
        return userRepository.save(user);
    }
    @PostMapping("/verify-password")
    public ResponseEntity<Map<String, String>> verifyPassword(@AuthenticationPrincipal UserDetails userDetails,
                                                              @RequestBody Map<String, String> request) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        String enteredPassword = request.get("password"); // Lấy mật khẩu user nhập
        if (!passwordEncoder.matches(enteredPassword, user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS); // Sai mật khẩu
        }

        Map<String, String> response = new HashMap<>();
        response.put("passwordRaw", enteredPassword);
        return ResponseEntity.ok(response);
    }

}
