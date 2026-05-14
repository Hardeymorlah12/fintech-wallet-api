package com.hardeymorlah.walletapi.controller;

import com.hardeymorlah.walletapi.dto.ApiResponse;
import com.hardeymorlah.walletapi.dto.RegisterRequest;
import com.hardeymorlah.walletapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

