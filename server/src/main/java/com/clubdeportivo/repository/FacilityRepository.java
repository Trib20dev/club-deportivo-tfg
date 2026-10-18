package com.clubdeportivo.repository;

import com.clubdeportivo.domain.enums.FacilityStatus;
import com.clubdeportivo.domain.enums.FacilityType;
import com.clubdeportivo.domain.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, UUID> {
    Optional<Facility> findByName(String name);
    List<Facility> findByType(FacilityType type);
    List<Facility> findByStatus(FacilityStatus status);
}