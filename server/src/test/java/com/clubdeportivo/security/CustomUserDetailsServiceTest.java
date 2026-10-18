package com.clubdeportivo.security;

import com.clubdeportivo.domain.enums.UserStatus;
import com.clubdeportivo.domain.model.Role;
import com.clubdeportivo.domain.model.User;
import com.clubdeportivo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    @DisplayName("Debe cargar CustomUserDetails exitosamente con los roles correspondientes")
    void testLoadUserByUsernameSuccess() {
        Role socioRole = new Role(UUID.randomUUID(), "ROLE_SOCIO");
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("socio@clubdeportivo.com")
                .passwordHash("$2a$10$hashedpassword")
                .status(UserStatus.ACTIVE)
                .roles(Set.of(socioRole))
                .build();

        when(userRepository.findByEmail("socio@clubdeportivo.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("socio@clubdeportivo.com");

        assertNotNull(userDetails);
        assertEquals("socio@clubdeportivo.com", userDetails.getUsername());
        assertEquals("$2a$10$hashedpassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SOCIO")));
        verify(userRepository, times(1)).findByEmail("socio@clubdeportivo.com");
    }

    @Test
    @DisplayName("Debe anteponer el prefijo ROLE_ si el rol no lo incluye")
    void testLoadUserByUsernameRolePrefixing() {
        Role adminRole = new Role(UUID.randomUUID(), "ADMIN");
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("admin@clubdeportivo.com")
                .passwordHash("$2a$10$hashedpassword")
                .status(UserStatus.ACTIVE)
                .roles(Set.of(adminRole))
                .build();

        when(userRepository.findByEmail("admin@clubdeportivo.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin@clubdeportivo.com");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Debe lanzar UsernameNotFoundException si el usuario no existe")
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByEmail("noexiste@clubdeportivo.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("noexiste@clubdeportivo.com"));
        verify(userRepository, times(1)).findByEmail("noexiste@clubdeportivo.com");
    }
}

