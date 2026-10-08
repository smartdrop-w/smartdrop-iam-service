package com.smart.drop.iam.infrastructure.persistence.jpa.repositories;

import com.smart.drop.iam.infrastructure.persistence.jpa.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad UserEntity.
 * Proporciona métodos CRUD básicos y consultas personalizadas.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Integer> {

    /**
     * Busca un usuario por email.
     *
     * @param email el email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<UserEntity> findByEmail(String email);
}

