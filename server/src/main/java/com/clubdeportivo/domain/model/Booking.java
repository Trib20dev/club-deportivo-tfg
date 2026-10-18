package com.clubdeportivo.domain.model;

import com.clubdeportivo.domain.enums.BookingStatus;
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
@Table(name = "bookings")
@Getter
@Setter
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    @Column(name = "total_price", nullable = false, precision = 7, scale = 2)
    private BigDecimal totalPrice;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructor sin argumentos (requerido por JPA)
    public Booking() {
        this.status = BookingStatus.CONFIRMED;
        this.totalPrice = BigDecimal.ZERO;
    }

    // Constructor completo
    public Booking(UUID id, Member member, Facility facility, LocalDateTime startTime, LocalDateTime endTime, BookingStatus status, BigDecimal totalPrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.member = member;
        this.facility = facility;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status != null ? status : BookingStatus.CONFIRMED;
        this.totalPrice = totalPrice != null ? totalPrice : BigDecimal.ZERO;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}