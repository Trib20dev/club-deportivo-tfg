package com.clubdeportivo.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
public class Role {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    // Constructor vacío (Requerido obligatoriamente por JPA)
    public Role() {
    }

    // Constructor completo
    public Role(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}