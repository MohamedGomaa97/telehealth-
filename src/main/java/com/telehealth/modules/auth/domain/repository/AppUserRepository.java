package com.telehealth.modules.auth.domain.repository;

import com.telehealth.modules.auth.domain.model.AppUser;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository {
    AppUser save(AppUser user);
    Optional<AppUser> findById(UUID id);
    Optional<AppUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
