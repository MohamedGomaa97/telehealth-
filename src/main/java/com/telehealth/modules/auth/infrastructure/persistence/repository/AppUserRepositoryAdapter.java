package com.telehealth.modules.auth.infrastructure.persistence.repository;

import com.telehealth.modules.auth.domain.model.AppUser;
import com.telehealth.modules.auth.domain.model.UserRole;
import com.telehealth.modules.auth.domain.repository.AppUserRepository;
import com.telehealth.modules.auth.infrastructure.persistence.entity.AppUserJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AppUserRepositoryAdapter implements AppUserRepository {

    private final AppUserJpaRepository jpa;

    @Override
    public AppUser save(AppUser user) {
        AppUserJpaEntity entity = jpa.findById(user.getId()).orElseGet(AppUserJpaEntity::new);
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setRole(user.getRole().name());
        entity.setLinkedId(user.getLinkedId());
        entity.setEnabled(user.isEnabled());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return toDomain(jpa.save(entity));
    }

    @Override
    public Optional<AppUser> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return jpa.findByEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    private AppUser toDomain(AppUserJpaEntity e) {
        AppUser user = AppUser.create(e.getEmail(), e.getPasswordHash(), UserRole.valueOf(e.getRole()), e.getLinkedId());
        user.setId(e.getId());
        user.setEnabled(e.isEnabled());
        user.setCreatedAt(e.getCreatedAt());
        user.setUpdatedAt(e.getUpdatedAt());
        return user;
    }
}
