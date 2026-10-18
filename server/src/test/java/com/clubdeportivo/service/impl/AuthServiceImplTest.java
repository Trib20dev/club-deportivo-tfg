package com.clubdeportivo.service.impl;

import com.clubdeportivo.domain.enums.MembershipType;
import com.clubdeportivo.domain.enums.UserStatus;
import com.clubdeportivo.domain.model.Member;
import com.clubdeportivo.domain.model.Membership;
import com.clubdeportivo.domain.model.Person;
import com.clubdeportivo.domain.model.Role;
import com.clubdeportivo.domain.model.User;
import com.clubdeportivo.dto.request.LoginRequest;
import com.clubdeportivo.dto.request.RegisterRequest;
import com.clubdeportivo.dto.response.AuthResponse;
import com.clubdeportivo.exception.DuplicateResourceException;
import com.clubdeportivo.exception.UnauthorizedException;
import com.clubdeportivo.repository.MemberRepository;
import com.clubdeportivo.repository.MembershipRepository;
import com.clubdeportivo.repository.PersonRepository;
import com.clubdeportivo.repository.RoleRepository;
import com.clubdeportivo.repository.UserRepository;
import com.clubdeportivo.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl Tests")
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private RoleRepository roleRepository;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<Person> personCaptor;

    @Captor
    private ArgumentCaptor<Member> memberCaptor;

    @Captor
    private ArgumentCaptor<Membership> membershipCaptor;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                authenticationManager,
                tokenProvider,
                passwordEncoder,
                userRepository,
                personRepository,
                memberRepository,
                membershipRepository,
                roleRepository
        );
    }

    @Test
    @DisplayName("login() debe autenticar y retornar AuthResponse con token válido y roles")
    void testLoginSuccess() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("socio@clubdeportivo.com")
                .password("password123")
                .build();

        Authentication mockAuth = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_SOCIO"))).when(mockAuth).getAuthorities();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(tokenProvider.generateToken(mockAuth)).thenReturn("mock-jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("socio@clubdeportivo.com", response.getEmail());
        assertTrue(response.getRoles().contains("ROLE_SOCIO"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider, times(1)).generateToken(mockAuth);
    }

    @Test
    @DisplayName("login() con credenciales inválidas debe lanzar UnauthorizedException")
    void testLoginBadCredentials() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("socio@clubdeportivo.com")
                .password("wrongpassword")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest));
        assertTrue(ex.getMessage().contains("Credenciales inválidas"));
    }

    @Test
    @DisplayName("register() debe crear y asociar correctamente User, Person, Member y Membership con sus campos exactos")
    void testRegisterSuccess() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("nuevo.socio@clubdeportivo.com")
                .password("secreto123")
                .name("Carlos")
                .lastName("Gómez Pérez")
                .dni("12345678Z")
                .phone("+34600112233")
                .address("Calle Gran Vía 15")
                .build();

        Role socioRole = new Role(UUID.randomUUID(), "ROLE_SOCIO");

        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(personRepository.existsByDni(registerRequest.getDni())).thenReturn(false);
        when(roleRepository.findByName("ROLE_SOCIO")).thenReturn(Optional.of(socioRole));
        when(passwordEncoder.encode("secreto123")).thenReturn("$2a$10$encodedPasswordHash");

        UUID userId = UUID.randomUUID();
        User savedUser = User.builder()
                .id(userId)
                .email(registerRequest.getEmail())
                .passwordHash("$2a$10$encodedPasswordHash")
                .status(UserStatus.ACTIVE)
                .roles(Set.of(socioRole))
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UUID personId = UUID.randomUUID();
        Person savedPerson = Person.builder()
                .id(personId)
                .user(savedUser)
                .dni(registerRequest.getDni())
                .name(registerRequest.getName())
                .lastName(registerRequest.getLastName())
                .phone(registerRequest.getPhone())
                .address(registerRequest.getAddress())
                .build();
        when(personRepository.save(any(Person.class))).thenReturn(savedPerson);

        UUID memberId = UUID.randomUUID();
        Member savedMember = Member.builder()
                .id(memberId)
                .person(savedPerson)
                .joinDate(LocalDate.now())
                .status(UserStatus.ACTIVE)
                .build();
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        UUID membershipId = UUID.randomUUID();
        Membership savedMembership = Membership.builder()
                .id(membershipId)
                .member(savedMember)
                .type(MembershipType.BASIC)
                .discountPercentage(BigDecimal.ZERO)
                .status(UserStatus.ACTIVE)
                .build();
        when(membershipRepository.save(any(Membership.class))).thenReturn(savedMembership);

        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn("generated-jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("generated-jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("nuevo.socio@clubdeportivo.com", response.getEmail());
        assertEquals(List.of("ROLE_SOCIO"), response.getRoles());

        // Verificación exhaustiva de User
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("nuevo.socio@clubdeportivo.com", capturedUser.getEmail());
        assertEquals("$2a$10$encodedPasswordHash", capturedUser.getPasswordHash());
        assertEquals(UserStatus.ACTIVE, capturedUser.getStatus());
        assertTrue(capturedUser.getRoles().contains(socioRole));

        // Verificación exhaustiva de Person
        verify(personRepository).save(personCaptor.capture());
        Person capturedPerson = personCaptor.getValue();
        assertEquals(savedUser, capturedPerson.getUser());
        assertEquals("12345678Z", capturedPerson.getDni());
        assertEquals("Carlos", capturedPerson.getName());
        assertEquals("Gómez Pérez", capturedPerson.getLastName());
        assertEquals("+34600112233", capturedPerson.getPhone());
        assertEquals("Calle Gran Vía 15", capturedPerson.getAddress());

        // Verificación exhaustiva de Member
        verify(memberRepository).save(memberCaptor.capture());
        Member capturedMember = memberCaptor.getValue();
        assertEquals(savedPerson, capturedMember.getPerson());
        assertEquals(LocalDate.now(), capturedMember.getJoinDate());
        assertEquals(UserStatus.ACTIVE, capturedMember.getStatus());

        // Verificación exhaustiva de Membership
        verify(membershipRepository).save(membershipCaptor.capture());
        Membership capturedMembership = membershipCaptor.getValue();
        assertEquals(savedMember, capturedMembership.getMember());
        assertEquals(MembershipType.BASIC, capturedMembership.getType());
        assertEquals(BigDecimal.ZERO, capturedMembership.getDiscountPercentage());
        assertEquals(UserStatus.ACTIVE, capturedMembership.getStatus());
    }

    @Test
    @DisplayName("register() debe lanzar DuplicateResourceException si el email ya existe")
    void testRegisterDuplicateEmail() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("existente@clubdeportivo.com")
                .password("secreto123")
                .name("Carlos")
                .lastName("Gómez")
                .dni("12345678Z")
                .build();

        when(userRepository.existsByEmail("existente@clubdeportivo.com")).thenReturn(true);

        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> authService.register(registerRequest));

        assertTrue(ex.getMessage().contains("existente@clubdeportivo.com"));
        verify(personRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register() debe lanzar DuplicateResourceException si el DNI ya existe")
    void testRegisterDuplicateDni() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("nuevo@clubdeportivo.com")
                .password("secreto123")
                .name("Carlos")
                .lastName("Gómez")
                .dni("99999999X")
                .build();

        when(userRepository.existsByEmail("nuevo@clubdeportivo.com")).thenReturn(false);
        when(personRepository.existsByDni("99999999X")).thenReturn(true);

        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> authService.register(registerRequest));

        assertTrue(ex.getMessage().contains("99999999X"));
        verify(userRepository, never()).save(any());
        verify(personRepository, never()).save(any());
    }
}

