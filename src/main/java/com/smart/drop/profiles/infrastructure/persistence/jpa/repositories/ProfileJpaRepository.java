package com.smart.drop.profiles.infrastructure.persistence.jpa.repositories;

import com.smart.drop.profiles.infrastructure.persistence.jpa.entities.ProfileEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, Integer> {

    @EntityGraph(attributePaths = "preferences")
    Optional<ProfileEntity> findByProfileId(Integer profileId);

    @EntityGraph(attributePaths = "preferences")
    Optional<ProfileEntity> findByUserId(Integer userId);

    @Override
    @EntityGraph(attributePaths = "preferences")
    List<ProfileEntity> findAll();
}

