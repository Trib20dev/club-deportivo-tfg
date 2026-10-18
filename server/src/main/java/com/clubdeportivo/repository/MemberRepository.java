package com.clubdeportivo.repository;

import com.clubdeportivo.domain.enums.UserStatus;
import com.clubdeportivo.domain.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByPersonId(UUID personId);
    List<Member> findByStatus(UserStatus status);
}