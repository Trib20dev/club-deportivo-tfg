package com.clubdeportivo.domain.model;

import com.clubdeportivo.domain.enums.FacilityStatus;
import com.clubdeportivo.domain.enums.FacilityType;
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
@Table(name = "facilities")
@Getter
@Setter
@Builder
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FacilityType type;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "price_per_hour", nullable = false, precision = 7, scale = 2)
    private BigDecimal pricePerHour;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FacilityStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructor sin argumentos (requerido por JPA)
    public Facility() {
        this.status = FacilityStatus.AVAILABLE;
        this.pricePerHour = BigDecimal.ZERO;
    }

    // Constructor completo
    public Facility(UUID id, String name, FacilityType type, Integer capacity, BigDecimal pricePerHour, FacilityStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.pricePerHour = pricePerHour != null ? pricePerHour : BigDecimal.ZERO;
        this.status = status != null ? status : FacilityStatus.AVAILABLE;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}