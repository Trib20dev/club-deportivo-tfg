package com.clubdeportivo.domain.model;

import com.clubdeportivo.domain.enums.MembershipType;
import com.clubdeportivo.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "memberships")
@Getter
@Setter
@Builder
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MembershipType type;

    @Column(name = "discount_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructor sin argumentos (requerido por JPA)
    public Membership() {
        this.type = MembershipType.BASIC;
        this.discountPercentage = BigDecimal.ZERO;
        this.status = UserStatus.ACTIVE;
    }

    // Constructor completo
    public Membership(UUID id, Member member, MembershipType type, BigDecimal discountPercentage, UserStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.member = member;
        this.type = type != null ? type : MembershipType.BASIC;
        this.discountPercentage = discountPercentage != null ? discountPercentage : BigDecimal.ZERO;
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
