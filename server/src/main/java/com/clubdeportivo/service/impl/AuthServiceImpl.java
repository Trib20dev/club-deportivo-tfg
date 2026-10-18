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
import com.clubdeportivo.security.CustomUserDetails;
import com.clubdeportivo.security.jwt.JwtTokenProvider;
import com.clubdeportivo.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final RoleRepository roleRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider tokenProvider,
                           PasswordEncoder passwordEncoder,
                           UserRepository userRepository,
                           PersonRepository personRepository,
                           MemberRepository memberRepository,
                           MembershipRepository membershipRepository,
                           RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.personRepository = personRepository;
        this.memberRepository = memberRepository;
        this.membershipRepository = membershipRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Iniciando sesión para email: {}", loginRequest.getEmail());
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            log.warn("Fallo de autenticación para email {}: credenciales inválidas", loginRequest.getEmail());
            throw new UnauthorizedException("Credenciales inválidas: email o contraseña incorrectos");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .email(loginRequest.getEmail())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Registrando nuevo socio con email: {} y DNI: {}", registerRequest.getEmail(), registerRequest.getDni());

        // 1. Validar unicidad de email y DNI
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("El email '" + registerRequest.getEmail() + "' ya está registrado");
        }
        if (personRepository.existsByDni(registerRequest.getDni())) {
            throw new DuplicateResourceException("El DNI '" + registerRequest.getDni() + "' ya está registrado");
        }

        // 2. Obtener rol de SOCIO por defecto
        Role socioRole = roleRepository.findByName("ROLE_SOCIO")
                .orElseGet(() -> roleRepository.save(new Role(UUID.randomUUID(), "ROLE_SOCIO")));

        // 3. Crear y guardar entidad User
        User user = User.builder()
                .email(registerRequest.getEmail())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .status(UserStatus.ACTIVE)
                .roles(new HashSet<>(Set.of(socioRole)))
                .build();
        User savedUser = userRepository.save(user);

        // 4. Crear y guardar entidad Person
        Person person = Person.builder()
                .user(savedUser)
                .dni(registerRequest.getDni())
                .name(registerRequest.getName())
                .lastName(registerRequest.getLastName())
                .phone(registerRequest.getPhone())
                .address(registerRequest.getAddress())
                .build();
        Person savedPerson = personRepository.save(person);

        // 5. Crear y guardar entidad Member
        Member member = Member.builder()
                .person(savedPerson)
                .joinDate(LocalDate.now())
                .status(UserStatus.ACTIVE)
                .build();
        Member savedMember = memberRepository.save(member);

        // 6. Crear y guardar entidad Membership
        Membership membership = Membership.builder()
                .member(savedMember)
                .type(MembershipType.BASIC)
                .discountPercentage(BigDecimal.ZERO)
                .status(UserStatus.ACTIVE)
                .build();
        membershipRepository.save(membership);

        // 7. Generar token JWT para el nuevo socio
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        String token = tokenProvider.generateToken(authentication);

        List<String> roles = List.of(socioRole.getName());

        log.info("Socio registrado exitosamente con ID de usuario: {}", savedUser.getId());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .email(savedUser.getEmail())
                .roles(roles)
                .build();
    }
}
