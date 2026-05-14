package com.hardeymorlah.walletapi.service;

import com.hardeymorlah.walletapi.dto.ApiResponse;
import com.hardeymorlah.walletapi.dto.RegisterRequest;
import com.hardeymorlah.walletapi.dto.UserResponse;
import com.hardeymorlah.walletapi.entity.Role;
import com.hardeymorlah.walletapi.entity.User;
import com.hardeymorlah.walletapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
    @RequiredArgsConstructor
    public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ApiResponse<?> registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Email already exists")
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        UserResponse userResponse = UserResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .createdAt(savedUser.getCreatedAt())
                .build();

        return ApiResponse.builder()
                .success(true)
                .message("User registered successfully")
                .data(userResponse)
                .timestamp(LocalDateTime.now())
                .build();
    }
}