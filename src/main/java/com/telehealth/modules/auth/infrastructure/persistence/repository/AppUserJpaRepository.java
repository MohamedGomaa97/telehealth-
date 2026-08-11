package com.telehealth.modules.auth.infrastructure.persistence.repository;

import com.telehealth.modules.auth.infrastructure.persistence.entity.AppUserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppUserJpaRepository extends JpaRepository<AppUserJpaEntity, UUID> {
    Optional<AppUserJpaEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
