package com.clubdeportivo.service.impl;

import com.clubdeportivo.domain.enums.UserStatus;
import com.clubdeportivo.domain.model.Member;
import com.clubdeportivo.domain.model.Membership;
import com.clubdeportivo.domain.model.Person;
import com.clubdeportivo.domain.model.User;
import com.clubdeportivo.dto.request.UpdateMemberRequest;
import com.clubdeportivo.dto.response.MemberResponse;
import com.clubdeportivo.exception.BadRequestException;
import com.clubdeportivo.exception.ResourceNotFoundException;
import com.clubdeportivo.repository.MemberRepository;
import com.clubdeportivo.repository.MembershipRepository;
import com.clubdeportivo.repository.PersonRepository;
import com.clubdeportivo.repository.UserRepository;
import com.clubdeportivo.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;

    public MemberServiceImpl(UserRepository userRepository,
                             PersonRepository personRepository,
                             MemberRepository memberRepository,
                             MembershipRepository membershipRepository) {
        this.userRepository = userRepository;
        this.personRepository = personRepository;
        this.memberRepository = memberRepository;
        this.membershipRepository = membershipRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getCurrentMemberProfile(String email) {
        log.info("Recuperando perfil de socio para email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        Person person = personRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Datos personales no encontrados para el usuario: " + email));

        Member member = memberRepository.findByPersonId(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ficha de socio no encontrada para el usuario: " + email));

        Membership membership = membershipRepository.findByMemberId(member.getId()).orElse(null);

        return mapToMemberResponse(member, user, person, membership);
    }

    @Override
    @Transactional
    public MemberResponse updateCurrentMemberProfile(String email, UpdateMemberRequest request) {
        log.info("Actualizando perfil de socio para email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        Person person = personRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Datos personales no encontrados para el usuario: " + email));

        Member member = memberRepository.findByPersonId(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ficha de socio no encontrada para el usuario: " + email));

        person.setName(request.getName());
        person.setLastName(request.getLastName());
        person.setPhone(request.getPhone());
        person.setAddress(request.getAddress());

        Person savedPerson = personRepository.save(person);
        member.setPerson(savedPerson);

        Membership membership = membershipRepository.findByMemberId(member.getId()).orElse(null);

        return mapToMemberResponse(member, user, savedPerson, membership);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberById(UUID memberId) {
        log.info("Buscando socio por ID: {}", memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Socio", "id", memberId));

        Person person = member.getPerson();
        User user = (person != null) ? person.getUser() : null;
        Membership membership = membershipRepository.findByMemberId(member.getId()).orElse(null);

        return mapToMemberResponse(member, user, person, membership);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberResponse> getAllMembers(Pageable pageable) {
        log.info("Recuperando listado paginado de socios: {}", pageable);
        Page<Member> membersPage = memberRepository.findAll(pageable);
        if (membersPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<UUID> memberIds = membersPage.map(Member::getId).toList();
        List<Membership> memberships = membershipRepository.findByMemberIdIn(memberIds);
        Map<UUID, Membership> membershipMap = memberships.stream()
                .collect(Collectors.toMap(
                        m -> m.getMember().getId(),
                        m -> m,
                        (existing, replacement) -> existing
                ));

        return membersPage.map(member -> {
            Person person = member.getPerson();
            User user = (person != null) ? person.getUser() : null;
            Membership membership = membershipMap.get(member.getId());
            return mapToMemberResponse(member, user, person, membership);
        });
    }

    @Override
    @Transactional
    public MemberResponse changeMemberStatus(UUID memberId, String status) {
        log.info("Cambiando estado del socio {} a {}", memberId, status);
        if (status == null || status.isBlank()) {
            throw new BadRequestException("El estado no puede estar vacío");
        }

        UserStatus newStatus;
        try {
            newStatus = UserStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Estado inválido: '" + status + "'. Estados permitidos: " + Arrays.toString(UserStatus.values()));
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Socio", "id", memberId));

        member.setStatus(newStatus);
        Member savedMember = memberRepository.save(member);

        Membership membership = membershipRepository.findByMemberId(savedMember.getId()).orElse(null);
        if (membership != null) {
            membership.setStatus(newStatus);
            membershipRepository.save(membership);
        }

        Person person = savedMember.getPerson();
        User user = (person != null) ? person.getUser() : null;

        return mapToMemberResponse(savedMember, user, person, membership);
    }

    private MemberResponse mapToMemberResponse(Member member, User user, Person person, Membership membership) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(user != null ? user.getEmail() : null)
                .dni(person != null ? person.getDni() : null)
                .name(person != null ? person.getName() : null)
                .lastName(person != null ? person.getLastName() : null)
                .phone(person != null ? person.getPhone() : null)
                .address(person != null ? person.getAddress() : null)
                .joinDate(member.getJoinDate())
                .status(member.getStatus() != null ? member.getStatus().name() : null)
                .membershipType(membership != null && membership.getType() != null ? membership.getType().name() : null)
                .discountPercentage(membership != null && membership.getDiscountPercentage() != null ? membership.getDiscountPercentage() : BigDecimal.ZERO)
                .build();
    }
}

