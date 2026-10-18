package com.clubdeportivo.controller;

import com.clubdeportivo.dto.request.LoginRequest;
import com.clubdeportivo.dto.request.RegisterRequest;
import com.clubdeportivo.dto.response.ApiResponse;
import com.clubdeportivo.dto.response.AuthResponse;
import com.clubdeportivo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Inicio de sesión exitoso", authResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario registrado exitosamente", authResponse));
    }
}
