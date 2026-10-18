package com.clubdeportivo.service;

import com.clubdeportivo.dto.request.LoginRequest;
import com.clubdeportivo.dto.request.RegisterRequest;
import com.clubdeportivo.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest loginRequest);

    AuthResponse register(RegisterRequest registerRequest);
}

