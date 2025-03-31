package com.example.zencash.controller;

import com.example.zencash.dto.UpdateUserRequest;
import com.example.zencash.dto.UserResponse;
import com.example.zencash.exception.AppException;
import com.example.zencash.entity.User;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.service.UserService;
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
    private final UserService userService;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }
    // Lấy thông tin user hiện tại
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        UserResponse response = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    // Cập nhật thông tin user
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestBody @Valid UpdateUserRequest request) {
        if (userDetails == null) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        UserResponse response = userService.updateUser(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
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