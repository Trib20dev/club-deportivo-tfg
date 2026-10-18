package com.clubdeportivo.service.impl;

import com.clubdeportivo.domain.enums.MembershipType;
import com.clubdeportivo.domain.enums.UserStatus;
import com.clubdeportivo.domain.model.Member;
import com.clubdeportivo.domain.model.Membership;
import com.clubdeportivo.domain.model.Person;
import com.clubdeportivo.domain.model.Role;
import com.clubdeportivo.domain.model.User;
import com.clubdeportivo.dto.request.UpdateMemberRequest;
import com.clubdeportivo.dto.response.MemberResponse;
import com.clubdeportivo.exception.BadRequestException;
import com.clubdeportivo.exception.ResourceNotFoundException;
import com.clubdeportivo.repository.MemberRepository;
import com.clubdeportivo.repository.MembershipRepository;
import com.clubdeportivo.repository.PersonRepository;
import com.clubdeportivo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
@DisplayName("MemberServiceImpl Tests")
class MemberServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MembershipRepository membershipRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private User sampleUser;
    private Person samplePerson;
    private Member sampleMember;
    private Membership sampleMembership;
    private UUID userId;
    private UUID personId;
    private UUID memberId;
    private UUID membershipId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        personId = UUID.randomUUID();
        memberId = UUID.randomUUID();
        membershipId = UUID.randomUUID();

        Role role = new Role(UUID.randomUUID(), "ROLE_SOCIO");

        sampleUser = User.builder()
                .id(userId)
                .email("socio@clubdeportivo.com")
                .passwordHash("hashedPass")
                .status(UserStatus.ACTIVE)
                .roles(Set.of(role))
                .build();

        samplePerson = Person.builder()
                .id(personId)
                .user(sampleUser)
                .dni("12345678A")
                .name("Juan")
                .lastName("Pérez García")
                .phone("+34611223344")
                .address("Calle Mayor 10")
                .build();

        sampleMember = Member.builder()
                .id(memberId)
                .person(samplePerson)
                .joinDate(LocalDate.of(2025, 1, 15))
                .status(UserStatus.ACTIVE)
                .build();

        sampleMembership = Membership.builder()
                .id(membershipId)
                .member(sampleMember)
                .type(MembershipType.PREMIUM)
                .discountPercentage(new BigDecimal("15.00"))
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("getCurrentMemberProfile: Éxito al recuperar el perfil del socio autenticado")
    void testGetCurrentMemberProfile_Success() {
        when(userRepository.findByEmail("socio@clubdeportivo.com")).thenReturn(Optional.of(sampleUser));
        when(personRepository.findByUserId(userId)).thenReturn(Optional.of(samplePerson));
        when(memberRepository.findByPersonId(personId)).thenReturn(Optional.of(sampleMember));
        when(membershipRepository.findByMemberId(memberId)).thenReturn(Optional.of(sampleMembership));

        MemberResponse response = memberService.getCurrentMemberProfile("socio@clubdeportivo.com");

        assertNotNull(response);
        assertEquals(memberId, response.getId());
        assertEquals("socio@clubdeportivo.com", response.getEmail());
        assertEquals("12345678A", response.getDni());
        assertEquals("Juan", response.getName());
        assertEquals("Pérez García", response.getLastName());
        assertEquals("+34611223344", response.getPhone());
        assertEquals("Calle Mayor 10", response.getAddress());
        assertEquals(LocalDate.of(2025, 1, 15), response.getJoinDate());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals("PREMIUM", response.getMembershipType());
        assertEquals(new BigDecimal("15.00"), response.getDiscountPercentage());

        verify(userRepository, times(1)).findByEmail("socio@clubdeportivo.com");
        verify(personRepository, times(1)).findByUserId(userId);
        verify(memberRepository, times(1)).findByPersonId(personId);
        verify(membershipRepository, times(1)).findByMemberId(memberId);
    }

    @Test
    @DisplayName("getCurrentMemberProfile: Socio sin membresía explícita asigna valores por defecto")
    void testGetCurrentMemberProfile_WithoutMembership() {
        when(userRepository.findByEmail("socio@clubdeportivo.com")).thenReturn(Optional.of(sampleUser));
        when(personRepository.findByUserId(userId)).thenReturn(Optional.of(samplePerson));
        when(memberRepository.findByPersonId(personId)).thenReturn(Optional.of(sampleMember));
        when(membershipRepository.findByMemberId(memberId)).thenReturn(Optional.empty());

        MemberResponse response = memberService.getCurrentMemberProfile("socio@clubdeportivo.com");

        assertNotNull(response);
        assertEquals(memberId, response.getId());
        assertNull(response.getMembershipType());
        assertEquals(BigDecimal.ZERO, response.getDiscountPercentage());
    }

    @Test
    @DisplayName("getCurrentMemberProfile: Lanza ResourceNotFoundException si el usuario no existe")
    void testGetCurrentMemberProfile_UserNotFound() {
        when(userRepository.findByEmail("desconocido@clubdeportivo.com")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                memberService.getCurrentMemberProfile("desconocido@clubdeportivo.com"));

        assertTrue(ex.getMessage().contains("Usuario no encontrado con email"));
        verify(personRepository, never()).findByUserId(any());
        verify(memberRepository, never()).findByPersonId(any());
    }

    @Test
    @DisplayName("getCurrentMemberProfile: Lanza ResourceNotFoundException si la persona no existe")
    void testGetCurrentMemberProfile_PersonNotFound() {
        when(userRepository.findByEmail("socio@clubdeportivo.com")).thenReturn(Optional.of(sampleUser));
        when(personRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                memberService.getCurrentMemberProfile("socio@clubdeportivo.com"));

        assertTrue(ex.getMessage().contains("Datos personales no encontrados"));
        verify(memberRepository, never()).findByPersonId(any());
    }

    @Test
    @DisplayName("getCurrentMemberProfile: Lanza ResourceNotFoundException si la ficha Member no existe")
    void testGetCurrentMemberProfile_MemberNotFound() {
        when(userRepository.findByEmail("socio@clubdeportivo.com")).thenReturn(Optional.of(sampleUser));
        when(personRepository.findByUserId(userId)).thenReturn(Optional.of(samplePerson));
        when(memberRepository.findByPersonId(personId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                memberService.getCurrentMemberProfile("socio@clubdeportivo.com"));

        assertTrue(ex.getMessage().contains("Ficha de socio no encontrada"));
    }

    @Test
    @DisplayName("updateCurrentMemberProfile: Éxito al actualizar los datos personales")
    void testUpdateCurrentMemberProfile_Success() {
        UpdateMemberRequest request = UpdateMemberRequest.builder()
                .name("Juan Carlos")
                .lastName("Pérez González")
                .phone("+34699887766")
                .address("Calle Nueva 45")
                .build();

        Person updatedPerson = Person.builder()
                .id(personId)
                .user(sampleUser)
                .dni("12345678A")
                .name("Juan Carlos")
                .lastName("Pérez González")
                .phone("+34699887766")
                .address("Calle Nueva 45")
                .build();

        when(userRepository.findByEmail("socio@clubdeportivo.com")).thenReturn(Optional.of(sampleUser));
        when(personRepository.findByUserId(userId)).thenReturn(Optional.of(samplePerson));
        when(memberRepository.findByPersonId(personId)).thenReturn(Optional.of(sampleMember));
        when(personRepository.save(any(Person.class))).thenReturn(updatedPerson);
        when(membershipRepository.findByMemberId(memberId)).thenReturn(Optional.of(sampleMembership));

        MemberResponse response = memberService.updateCurrentMemberProfile("socio@clubdeportivo.com", request);

        assertNotNull(response);
        assertEquals("Juan Carlos", response.getName());
        assertEquals("Pérez González", response.getLastName());
        assertEquals("+34699887766", response.getPhone());
        assertEquals("Calle Nueva 45", response.getAddress());

        verify(personRepository, times(1)).save(samplePerson);
    }

    @Test
    @DisplayName("updateCurrentMemberProfile: Lanza ResourceNotFoundException si el usuario no existe")
    void testUpdateCurrentMemberProfile_UserNotFound() {
        UpdateMemberRequest request = UpdateMemberRequest.builder()
                .name("Juan")
                .lastName("Pérez")
                .build();

        when(userRepository.findByEmail("noexiste@clubdeportivo.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                memberService.updateCurrentMemberProfile("noexiste@clubdeportivo.com", request));

        verify(personRepository, never()).save(any());
    }

    @Test
    @DisplayName("getMemberById: Éxito al obtener socio por UUID")
    void testGetMemberById_Success() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(sampleMember));
        when(membershipRepository.findByMemberId(memberId)).thenReturn(Optional.of(sampleMembership));

        MemberResponse response = memberService.getMemberById(memberId);

        assertNotNull(response);
        assertEquals(memberId, response.getId());
        assertEquals("socio@clubdeportivo.com", response.getEmail());
        assertEquals("Juan", response.getName());
        assertEquals("12345678A", response.getDni());
        assertEquals("ACTIVE", response.getStatus());

        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    @DisplayName("getMemberById: Lanza ResourceNotFoundException cuando no se encuentra el socio")
    void testGetMemberById_NotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(memberRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                memberService.getMemberById(nonExistentId));

        verify(memberRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("getAllMembers: Retorna página de socios con membresías asociadas")
    void testGetAllMembers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Member> memberPage = new PageImpl<>(List.of(sampleMember), pageable, 1);

        when(memberRepository.findAll(pageable)).thenReturn(memberPage);
        when(membershipRepository.findByMemberIdIn(List.of(memberId))).thenReturn(List.of(sampleMembership));

        Page<MemberResponse> result = memberService.getAllMembers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        MemberResponse item = result.getContent().get(0);
        assertEquals(memberId, item.getId());
        assertEquals("Juan", item.getName());
        assertEquals("PREMIUM", item.getMembershipType());

        verify(memberRepository, times(1)).findAll(pageable);
        verify(membershipRepository, times(1)).findByMemberIdIn(List.of(memberId));
    }

    @Test
    @DisplayName("getAllMembers: Retorna página vacía cuando no hay socios")
    void testGetAllMembers_Empty() {
        Pageable pageable = PageRequest.of(0, 10);
        when(memberRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<MemberResponse> result = memberService.getAllMembers(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(membershipRepository, never()).findByMemberIdIn(any());
    }

    @Test
    @DisplayName("changeMemberStatus: Éxito al cambiar estado a INACTIVE")
    void testChangeMemberStatus_Success_Inactive() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(sampleMember));
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(membershipRepository.findByMemberId(memberId)).thenReturn(Optional.of(sampleMembership));
        when(membershipRepository.save(any(Membership.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MemberResponse response = memberService.changeMemberStatus(memberId, "INACTIVE");

        assertNotNull(response);
        assertEquals("INACTIVE", response.getStatus());
        assertEquals(UserStatus.INACTIVE, sampleMember.getStatus());
        assertEquals(UserStatus.INACTIVE, sampleMembership.getStatus());

        verify(memberRepository, times(1)).save(sampleMember);
        verify(membershipRepository, times(1)).save(sampleMembership);
    }

    @Test
    @DisplayName("changeMemberStatus: Éxito al cambiar estado a SUSPENDED")
    void testChangeMemberStatus_Success_Suspended() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(sampleMember));
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(membershipRepository.findByMemberId(memberId)).thenReturn(Optional.of(sampleMembership));
        when(membershipRepository.save(any(Membership.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MemberResponse response = memberService.changeMemberStatus(memberId, "SUSPENDED");

        assertNotNull(response);
        assertEquals("SUSPENDED", response.getStatus());
        assertEquals(UserStatus.SUSPENDED, sampleMember.getStatus());
    }

    @Test
    @DisplayName("changeMemberStatus: Lanza BadRequestException si el estado es inválido")
    void testChangeMemberStatus_InvalidStatus() {
        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                memberService.changeMemberStatus(memberId, "ESTADO_NO_EXISTE"));

        assertTrue(ex.getMessage().contains("Estado inválido"));
        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("changeMemberStatus: Lanza BadRequestException si el estado es nulo o vacío")
    void testChangeMemberStatus_BlankStatus() {
        assertThrows(BadRequestException.class, () ->
                memberService.changeMemberStatus(memberId, "   "));
        assertThrows(BadRequestException.class, () ->
                memberService.changeMemberStatus(memberId, null));
    }

    @Test
    @DisplayName("changeMemberStatus: Lanza ResourceNotFoundException si el socio no existe")
    void testChangeMemberStatus_MemberNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(memberRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                memberService.changeMemberStatus(nonExistentId, "ACTIVE"));
    }
}

