package com.smart.drop.profiles.infrastructure.persistence.jpa.repositories;

import com.smart.drop.profiles.infrastructure.persistence.jpa.entities.PreferencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferencesJpaRepository extends JpaRepository<PreferencesEntity, Integer> {

    Optional<PreferencesEntity> findByProfile_ProfileId(Integer profileId);
}

