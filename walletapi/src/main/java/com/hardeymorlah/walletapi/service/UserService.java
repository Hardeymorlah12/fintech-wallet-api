package com.hardeymorlah.walletapi.service;

import com.hardeymorlah.walletapi.dto.*;
import com.hardeymorlah.walletapi.entity.Role;
import com.hardeymorlah.walletapi.entity.User;
import com.hardeymorlah.walletapi.exception.EmailAlreadyExistsException;
import com.hardeymorlah.walletapi.exception.InvalidCredentialsException;
import com.hardeymorlah.walletapi.repository.UserRepository;
import com.hardeymorlah.walletapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
    @RequiredArgsConstructor
    public class UserService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ApiResponse<?> registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
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

    public ApiResponse<?> loginUser(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password")
                );

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .build();

        return ApiResponse.builder()
                .success(true)
                .message("Login successful")
                .data(authResponse)
                .timestamp(LocalDateTime.now())
                .build();
    }
}