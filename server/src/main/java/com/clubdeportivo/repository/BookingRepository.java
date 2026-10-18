package com.clubdeportivo.repository;

import com.clubdeportivo.domain.enums.BookingStatus;
import com.clubdeportivo.domain.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByMemberId(UUID memberId);
    List<Booking> findByFacilityId(UUID facilityId);
    List<Booking> findByStatus(BookingStatus status);

    // Consulta para validar solapamiento de horarios al intentar reservar una instalación
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.facility.id = :facilityId " +
           "AND b.status <> 'CANCELLED' " +
           "AND b.startTime < :endTime AND b.endTime > :startTime")
    boolean existsOverlappingBooking(
        @Param("facilityId") UUID facilityId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}