package com.clubdeportivo.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long expirationMs = 3600000; // 1 hora

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(secret, expirationMs);
    }

    @Test
    @DisplayName("Debe generar un token válido a partir de una Authentication")
    void testGenerateTokenFromAuthentication() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "socio@clubdeportivo.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_SOCIO"))
        );

        String token = jwtTokenProvider.generateToken(auth);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("socio@clubdeportivo.com", jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Debe generar un token válido a partir de un username")
    void testGenerateTokenFromUsername() {
        String token = jwtTokenProvider.generateToken("admin@clubdeportivo.com");

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("admin@clubdeportivo.com", jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Debe rechazar un token manipulado o con firma incorrecta")
    void testValidateTokenWithTamperedToken() {
        String token = jwtTokenProvider.generateToken("user@clubdeportivo.com");
        String tamperedToken = token + "corrupted";

        assertFalse(jwtTokenProvider.validateToken(tamperedToken));
    }

    @Test
    @DisplayName("Debe rechazar un token malformado o nulo")
    void testValidateTokenWithInvalidFormat() {
        assertFalse(jwtTokenProvider.validateToken("token.invalido.aleatorio"));
        assertFalse(jwtTokenProvider.validateToken(""));
        assertFalse(jwtTokenProvider.validateToken(null));
    }

    @Test
    @DisplayName("Debe rechazar un token expirado")
    void testValidateExpiredToken() {
        // Provider con expiración inmediata (-1000 ms)
        JwtTokenProvider expiredProvider = new JwtTokenProvider(secret, -1000);
        String expiredToken = expiredProvider.generateToken("expired@clubdeportivo.com");

        assertFalse(jwtTokenProvider.validateToken(expiredToken));
    }
}

