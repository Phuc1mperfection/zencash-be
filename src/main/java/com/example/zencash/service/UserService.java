package com.example.zencash.service;

import com.example.zencash.dto.ChangePasswordRequest;
import com.example.zencash.dto.UpdateUserRequest;
import com.example.zencash.dto.UserResponse;
import com.example.zencash.entity.User;
import com.example.zencash.exception.AppException;
import com.example.zencash.repository.UserRepository;
import com.example.zencash.utils.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Lấy thông tin người dùng
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        return new UserResponse(user.getEmail(), user.getUsername(), user.getFullname(), user.getAvatar(),user.getCurrency());
    }

    // Cập nhật thông tin người dùng
    public UserResponse updateUser(String email, UpdateUserRequest request, String avatarFilename) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (request.getFullname() != null) {
            user.setFullname(request.getFullname());
        }

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }

        // Xử lý avatar đúng logic
        if ((request.getAvatar() == null || request.getAvatar().isBlank()) &&
                (user.getAvatar() == null || user.getAvatar().isBlank())) {
            user.setAvatar("hinh-cute-meo.jpg");
        } else if (request.getAvatar() != null && !request.getAvatar().isBlank()) {
            user.setAvatar(request.getAvatar());
        }

        if (request.getCurrency() != null) {
            user.setCurrency(request.getCurrency());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new AppException(ErrorCode.EMAIL_TAKEN);
            }
            user.setEmail(request.getEmail());
        }

        userRepository.save(user);
        return new UserResponse(user.getEmail(), user.getUsername(), user.getFullname(), user.getAvatar(), user.getCurrency());
    }


    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_ERROR);
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new AppException(ErrorCode.PASSWORD_ERROR);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void deactivateAccount(User user) {
        user.setActive(false);
        userRepository.save(user);
    }
}
