package com.example.zencash.controller;

import com.example.zencash.dto.AuthResponse;
import com.example.zencash.dto.ChangePasswordRequest;
import com.example.zencash.dto.UpdateUserRequest;
import com.example.zencash.dto.UserResponse;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.service.AuthService;
import com.example.zencash.service.UserService;
import com.example.zencash.utils.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private static final String AVATAR_DIR = "../image/avatar/";

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
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
        UserResponse response = userService.updateUser(userDetails.getUsername(), request, request.getAvatar());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            userService.changePassword(userDetails.getUsername(), request);
            return ResponseEntity.ok("Change password successfully!");
        } catch (AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(e.getMessage());
        }
    }

    @PostMapping("/delete-account")
    public ResponseEntity<AuthResponse> deleteAccount(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        String token = authHeader.substring(7);
        return ResponseEntity.ok(authService.deleteAccount(token));
    }

}