package com.clubdeportivo.domain.model;

import com.clubdeportivo.domain.enums.PaymentMethod;
import com.clubdeportivo.domain.enums.PaymentStatus;
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
@Table(name = "payments")
@Getter
@Setter
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 255)
    private String concept;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 50)
    private PaymentMethod paymentMethod;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructor sin argumentos (requerido por JPA)
    public Payment() {
        this.amount = BigDecimal.ZERO;
        this.status = PaymentStatus.PENDING;
    }

    // Constructor completo
    public Payment(UUID id, Member member, BigDecimal amount, String concept, LocalDateTime paymentDate, PaymentStatus status, PaymentMethod paymentMethod, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.member = member;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.concept = concept;
        this.paymentDate = paymentDate;
        this.status = status != null ? status : PaymentStatus.PENDING;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}