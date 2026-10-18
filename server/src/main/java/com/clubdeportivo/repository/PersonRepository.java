package com.clubdeportivo.repository;

import com.clubdeportivo.domain.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {
    Optional<Person> findByDni(String dni);
    boolean existsByDni(String dni);
    Optional<Person> findByUserId(UUID userId);
}