package com.clubdeportivo.controller;

import com.clubdeportivo.dto.request.UpdateMemberRequest;
import com.clubdeportivo.dto.request.UpdateMemberStatusRequest;
import com.clubdeportivo.dto.response.ApiResponse;
import com.clubdeportivo.dto.response.MemberResponse;
import com.clubdeportivo.exception.BadRequestException;
import com.clubdeportivo.exception.UnauthorizedException;
import com.clubdeportivo.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Obtiene el perfil del socio autenticado actualmente.
     *
     * @param authentication Contexto de autenticación de Spring Security.
     * @return Perfil del socio autenticado.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> getCurrentMemberProfile(Authentication authentication) {
        String email = extractEmail(authentication);
        MemberResponse response = memberService.getCurrentMemberProfile(email);
        return ResponseEntity.ok(ApiResponse.success("Perfil de socio recuperado exitosamente", response));
    }

    /**
     * Actualiza los datos personales del socio autenticado actualmente.
     *
     * @param authentication Contexto de autenticación de Spring Security.
     * @param request        Datos de contacto a actualizar.
     * @return Perfil del socio actualizado.
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> updateCurrentMemberProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateMemberRequest request) {
        String email = extractEmail(authentication);
        MemberResponse response = memberService.updateCurrentMemberProfile(email, request);
        return ResponseEntity.ok(ApiResponse.success("Perfil de socio actualizado exitosamente", response));
    }

    /**
     * Obtiene la ficha de un socio por su identificador UUID.
     * Requiere roles de gestión (ADMINISTRADOR, RECEPCIONISTA, JEFE_SALA).
     *
     * @param id Identificador UUID del socio.
     * @return Ficha completa del socio.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'JEFE_SALA')")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(@PathVariable UUID id) {
        MemberResponse response = memberService.getMemberById(id);
        return ResponseEntity.ok(ApiResponse.success("Socio recuperado exitosamente", response));
    }

    /**
     * Obtiene el listado paginado de socios del club.
     * Requiere roles de gestión (ADMINISTRADOR, RECEPCIONISTA, JEFE_SALA).
     *
     * @param pageable Parámetros de paginación (page, size, sort).
     * @return Página de socios.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'JEFE_SALA')")
    public ResponseEntity<ApiResponse<Page<MemberResponse>>> getAllMembers(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<MemberResponse> response = memberService.getAllMembers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Listado de socios recuperado exitosamente", response));
    }

    /**
     * Modifica el estado de un socio (ej. ACTIVE, INACTIVE, SUSPENDED).
     * Requiere rol ADMINISTRADOR.
     * Permite recibir el nuevo estado vía query param o body JSON.
     *
     * @param id          Identificador UUID del socio.
     * @param statusParam Query parameter opcional 'status'.
     * @param requestBody Cuerpo JSON opcional con el campo 'status'.
     * @return Socio con el estado modificado.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<MemberResponse>> changeMemberStatus(
            @PathVariable UUID id,
            @RequestParam(name = "status", required = false) String statusParam,
            @RequestBody(required = false) UpdateMemberStatusRequest requestBody) {
        String status = (requestBody != null && requestBody.getStatus() != null && !requestBody.getStatus().isBlank())
                ? requestBody.getStatus()
                : statusParam;

        if (status == null || status.isBlank()) {
            throw new BadRequestException("El parámetro o campo 'status' es obligatorio");
        }

        MemberResponse response = memberService.changeMemberStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Estado del socio actualizado exitosamente", response));
    }

    private String extractEmail(Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException("Usuario no autenticado");
        }
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return authentication.getName();
    }
}

