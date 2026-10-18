package com.clubdeportivo.service;

import com.clubdeportivo.dto.request.UpdateMemberRequest;
import com.clubdeportivo.dto.response.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MemberService {

    /**
     * Recupera el perfil del socio asociado al email especificado.
     *
     * @param email Email del usuario autenticado.
     * @return MemberResponse con los datos del socio, persona y membresía.
     */
    MemberResponse getCurrentMemberProfile(String email);

    /**
     * Actualiza los datos personales (Person) del socio autenticado.
     *
     * @param email   Email del usuario autenticado.
     * @param request Datos a actualizar.
     * @return MemberResponse actualizado.
     */
    MemberResponse updateCurrentMemberProfile(String email, UpdateMemberRequest request);

    /**
     * Busca y retorna un socio por su identificador UUID.
     *
     * @param memberId Identificador UUID del socio.
     * @return MemberResponse con los datos del socio.
     */
    MemberResponse getMemberById(UUID memberId);

    /**
     * Retorna un listado paginado de socios.
     *
     * @param pageable Parámetros de paginación.
     * @return Página de MemberResponse.
     */
    Page<MemberResponse> getAllMembers(Pageable pageable);

    /**
     * Modifica el estado del socio (ej. ACTIVE, INACTIVE, SUSPENDED).
     *
     * @param memberId Identificador UUID del socio.
     * @param status   Nuevo estado para el socio.
     * @return MemberResponse con el estado actualizado.
     */
    MemberResponse changeMemberStatus(UUID memberId, String status);
}

