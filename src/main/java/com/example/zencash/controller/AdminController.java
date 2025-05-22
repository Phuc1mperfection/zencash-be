package com.example.zencash.controller;

import com.example.zencash.entity.User;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public AdminController(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
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
     @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        
        // Update user info
        user.setEmail(userDetails.getEmail());
        user.setUsername(userDetails.getUsername());
        user.setFullname(userDetails.getFullname());
        user.setAvatar(userDetails.getAvatar());
        user.setCurrency(userDetails.getCurrency());
        user.setActive(userDetails.isActive());
        user.setRoles(userDetails.getRoles());
        
        // Only update password if it's provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        // Save updated user
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

}
