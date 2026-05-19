package com.hardeymorlah.walletapi.exception;

import com.hardeymorlah.walletapi.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<?>> handleValidationErrors(
                MethodArgumentNotValidException ex
        ) {

            Map<String, String> errors = new HashMap<>();

            ex.getBindingResult()
                    .getFieldErrors()
                    .forEach(error ->
                            errors.put(error.getField(), error.getDefaultMessage())
                    );

            ApiResponse<?> response = ApiResponse.builder()
                    .success(false)
                    .message("Validation failed")
                    .data(errors)
                    .timestamp(LocalDateTime.now())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }


        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<ApiResponse<?>> handleEmailAlreadyExistsException(
                EmailAlreadyExistsException ex
        ) {

            ApiResponse<?> response = ApiResponse.builder()
                    .success(false)
                    .message(ex.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ApiResponse<?>> handleInvalidCredentialsException(
                InvalidCredentialsException ex
        ) {

            ApiResponse<?> response = ApiResponse.builder()
                    .success(false)
                    .message(ex.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }


        @ExceptionHandler(WalletAlreadyExistsException.class)
        public ResponseEntity<ApiResponse<?>> handleWalletAlreadyExistsException(
                WalletAlreadyExistsException ex
        ) {

            ApiResponse<?> response = ApiResponse.builder()
                    .success(false)
                    .message(ex.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        @ExceptionHandler(InvalidTransferException.class)
        public ResponseEntity<ApiResponse<?>> handleInvalidTransferException(
                InvalidTransferException ex
        ) {

            ApiResponse<?> response = ApiResponse.builder()
                    .success(false)
                    .message(ex.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(ReceiverWalletNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleReceiverWalletNotFound(
                ReceiverWalletNotFoundException ex
        ) {

            ApiResponse<Object> response = ApiResponse.builder()
                    .success(false)
                    .message(ex.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.badRequest().body(response);
        }
    }
