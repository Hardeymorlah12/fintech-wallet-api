package com.hardeymorlah.walletapi.service;

import com.hardeymorlah.walletapi.dto.*;
import com.hardeymorlah.walletapi.entity.PasswordResetToken;
import com.hardeymorlah.walletapi.entity.RefreshToken;
import com.hardeymorlah.walletapi.entity.Role;
import com.hardeymorlah.walletapi.entity.User;
import com.hardeymorlah.walletapi.exception.EmailAlreadyExistsException;
import com.hardeymorlah.walletapi.exception.InvalidCredentialsException;
import com.hardeymorlah.walletapi.repository.PasswordResetTokenRepository;
import com.hardeymorlah.walletapi.repository.UserRepository;
import com.hardeymorlah.walletapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final EmailService emailService;

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

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
        log.info(
                "User registered successfully with id: {}",
                savedUser.getId()
        );

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
        log.info(
                "Login attempt for email: {}",
                request.getEmail()
        );
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

        String accessToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();

        return ApiResponse.builder()
                .success(true)
                .message("Login successful")
                .data(authResponse)
                .timestamp(LocalDateTime.now())
                .build();


    }

    public void forgotPassword(ForgotPasswordRequest request) {
        log.info(
                "Password reset requested for email: {}",
                request.getEmail()
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken =
                PasswordResetToken.builder()
                        .token(token)
                        .user(user)
                        .expiryDate(
                                LocalDateTime.now().plusMinutes(15)
                        )
                        .build();

        passwordResetTokenRepository.save(resetToken);

        String message =
                "Use this token to reset your password: "
                        + token;

        emailService.sendEmail(
                user.getEmail(),
                "Password Reset",
                message
        );
        log.info(
                "Password reset token sent to email: {}",
                user.getEmail()
        );
    }

    public void resetPassword(ResetPasswordRequest request) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(request.getToken())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid reset token"
                                ));

        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Reset token has expired"
            );
        }

        User user = resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);
        log.info(
                "Password reset successful for user id: {}",
                user.getId()
        );
        passwordResetTokenRepository.delete(resetToken);
    }

    public ApiResponse<?> refreshToken(
            RefreshTokenRequest request
    ) {

        String accessToken =
                refreshTokenService.generateNewAccessToken(
                        request.getRefreshToken()
                );

        RefreshTokenResponse response =
                RefreshTokenResponse.builder()
                        .accessToken(accessToken)
                        .build();

        return ApiResponse.builder()
                .success(true)
                .message("Access token refreshed successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
    }
}