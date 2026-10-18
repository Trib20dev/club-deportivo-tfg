package com.clubdeportivo.controller;

import com.clubdeportivo.dto.request.LoginRequest;
import com.clubdeportivo.dto.request.RegisterRequest;
import com.clubdeportivo.dto.response.AuthResponse;
import com.clubdeportivo.exception.GlobalExceptionHandler;
import com.clubdeportivo.exception.UnauthorizedException;
import com.clubdeportivo.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login exitoso debe retornar 200 OK y ApiResponse con AuthResponse")
    void testLoginSuccess() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("socio@clubdeportivo.com")
                .password("password123")
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("jwt-token-xyz")
                .tokenType("Bearer")
                .email("socio@clubdeportivo.com")
                .roles(List.of("ROLE_SOCIO"))
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Inicio de sesión exitoso"))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token-xyz"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.email").value("socio@clubdeportivo.com"))
                .andExpect(jsonPath("$.data.roles[0]").value("ROLE_SOCIO"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login con credenciales inválidas debe retornar 401 UNAUTHORIZED")
    void testLoginUnauthorized() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("socio@clubdeportivo.com")
                .password("wrongpassword")
                .build();

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new UnauthorizedException("Credenciales inválidas: email o contraseña incorrectos"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Credenciales inválidas: email o contraseña incorrectos"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login con campos vacíos debe retornar 400 BAD_REQUEST")
    void testLoginValidationFailure() throws Exception {
        LoginRequest invalidRequest = LoginRequest.builder()
                .email("email-invalido")
                .password("")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación en los campos de la solicitud"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("POST /api/v1/auth/register exitoso debe retornar 201 CREATED y ApiResponse con AuthResponse")
    void testRegisterSuccess() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("nuevo@clubdeportivo.com")
                .password("password123")
                .name("Laura")
                .lastName("Martínez")
                .dni("87654321B")
                .phone("+34655443322")
                .address("Avenida Libertad 20")
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("jwt-token-register")
                .tokenType("Bearer")
                .email("nuevo@clubdeportivo.com")
                .roles(List.of("ROLE_SOCIO"))
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuario registrado exitosamente"))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token-register"))
                .andExpect(jsonPath("$.data.email").value("nuevo@clubdeportivo.com"))
                .andExpect(jsonPath("$.data.roles[0]").value("ROLE_SOCIO"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register con contraseña menor a 6 caracteres debe retornar 400 BAD_REQUEST")
    void testRegisterValidationFailureShortPassword() throws Exception {
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .email("valido@clubdeportivo.com")
                .password("123") // menor a 6
                .name("Laura")
                .lastName("Martínez")
                .dni("87654321B")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details", hasItem(containsString("password"))));
    }
}
