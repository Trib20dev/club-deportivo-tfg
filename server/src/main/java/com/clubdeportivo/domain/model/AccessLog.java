package com.clubdeportivo.domain.model;

import com.clubdeportivo.domain.enums.AccessStatus;
import com.clubdeportivo.domain.enums.AccessType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "access_logs")
@Getter
@Setter
@Builder
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "access_time", nullable = false)
    private LocalDateTime accessTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", nullable = false, length = 20)
    private AccessType accessType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccessStatus status;

    @Column(name = "denial_reason", length = 255)
    private String denialReason;

    // Constructor sin argumentos (requerido por JPA)
    public AccessLog() {
        this.accessTime = LocalDateTime.now();
        this.status = AccessStatus.GRANTED;
    }

    // Constructor completo
    public AccessLog(UUID id, Member member, LocalDateTime accessTime, AccessType accessType, AccessStatus status, String denialReason) {
        this.id = id;
        this.member = member;
        this.accessTime = accessTime != null ? accessTime : LocalDateTime.now();
        this.accessType = accessType;
        this.status = status != null ? status : AccessStatus.GRANTED;
        this.denialReason = denialReason;
    }
}