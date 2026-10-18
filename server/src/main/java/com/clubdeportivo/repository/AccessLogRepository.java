package com.clubdeportivo.repository;

import com.clubdeportivo.domain.enums.AccessStatus;
import com.clubdeportivo.domain.model.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, UUID> {
    List<AccessLog> findByMemberId(UUID memberId);
    List<AccessLog> findByStatus(AccessStatus status);
    List<AccessLog> findByAccessTimeBetween(LocalDateTime start, LocalDateTime end);
}