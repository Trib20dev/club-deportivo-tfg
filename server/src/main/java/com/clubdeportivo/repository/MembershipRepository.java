package com.clubdeportivo.repository;

import com.clubdeportivo.domain.enums.UserStatus;
import com.clubdeportivo.domain.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID> {
    Optional<Membership> findByMemberId(UUID memberId);
    List<Membership> findByMemberIdIn(List<UUID> memberIds);
    List<Membership> findByStatus(UserStatus status);
}
