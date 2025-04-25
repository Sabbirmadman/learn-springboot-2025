package com.lelarn.dreamshops.controller;

import com.lelarn.dreamshops.request.LoginRequest;
import com.lelarn.dreamshops.request.RegisterRequest;
import com.lelarn.dreamshops.response.ApiResponse;
import com.lelarn.dreamshops.response.AuthResponse;
import com.lelarn.dreamshops.service.auth.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException; // Import AuthenticationException
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ApiResponse.success(HttpStatus.CREATED, "User registered successfully", response);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed due to an internal error",
                    e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ApiResponse.success("Login successful", response);
        } catch (AuthenticationException e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Login failed: Invalid credentials", e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Login failed due to an internal error",
                    e.getMessage());
        }
    }
}