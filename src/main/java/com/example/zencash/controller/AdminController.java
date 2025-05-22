package com.example.zencash.controller;

import com.example.zencash.entity.User;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final com.example.zencash.service.UserService userService;

    public AdminController(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService,
            com.example.zencash.service.UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.userService = userService;
    }

    @GetMapping("/users/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> resetPasswordForUser(@RequestParam String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        // Tạo mật khẩu mới ngẫu nhiên
        String newPassword = UUID.randomUUID().toString().substring(0, 8);
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // Gửi mail cho user
        mailService.sendPasswordResetEmail(user.getEmail(), newPassword);

        return ResponseEntity.ok("Mật khẩu đã được đặt lại và gửi đến email người dùng.");
    }

    @GetMapping("/users/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchUsers(@RequestParam(required = false) String email,
            @RequestParam(required = false) String username) {
        if (email != null && !email.isEmpty()) {
            return userRepository.findByEmail(email)
                    .map(user -> ResponseEntity.ok(java.util.Collections.singletonList(user)))
                    .orElse(ResponseEntity.ok(java.util.Collections.emptyList()));
        } else if (username != null && !username.isEmpty()) {
            return userRepository.findByUsername(username)
                    .map(user -> ResponseEntity.ok(java.util.Collections.singletonList(user)))
                    .orElse(ResponseEntity.ok(java.util.Collections.emptyList()));
        } else {
            // Return all users if no parameters provided
            // Consider implementing pagination for larger applications
            return ResponseEntity.ok(userRepository.findAll());
        }
    }

    @PatchMapping("/users/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserActiveStatus(@PathVariable UUID id) {
        try {
            userService.toggleUserActiveStatus(id);
            return ResponseEntity.ok(
                    Map.of("success", true,
                            "message", "User active status toggled successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false,
                            "message", e.getMessage()));
        }
    }

}
