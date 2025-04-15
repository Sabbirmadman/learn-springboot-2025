package com.lelarn.dreamshops.service.auth;

import com.lelarn.dreamshops.request.LoginRequest;
import com.lelarn.dreamshops.request.RegisterRequest;
import com.lelarn.dreamshops.response.AuthResponse;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}