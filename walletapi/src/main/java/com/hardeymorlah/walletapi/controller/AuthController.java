package com.hardeymorlah.walletapi.controller;

import com.hardeymorlah.walletapi.dto.*;
import com.hardeymorlah.walletapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(
            @Valid @RequestBody RegisterRequest request
    ) {

        ApiResponse<?> response = userService.registerUser(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> loginUser(
            @Valid @RequestBody LoginRequest request
    ) {

        ApiResponse<?> response = userService.loginUser(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {

        userService.forgotPassword(request);

        ApiResponse<?> response = ApiResponse.builder()
                .success(true)
                .message("Password reset token sent to email")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {

        userService.resetPassword(request);

        ApiResponse<?> response = ApiResponse.builder()
                .success(true)
                .message("Password reset successful")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {

        return ResponseEntity.ok(
                userService.refreshToken(request)
        );
    }
}

