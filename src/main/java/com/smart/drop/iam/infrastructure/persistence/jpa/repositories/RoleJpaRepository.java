package com.smart.drop.iam.infrastructure.persistence.jpa.repositories;

import com.smart.drop.iam.infrastructure.persistence.jpa.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad RoleEntity.
 * Proporciona métodos CRUD básicos y consultas personalizadas.
 */
@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, Integer> {

    /**
     * Busca un rol por nombre.
     *
     * @param name el nombre del rol
     * @return Optional con el rol si existe
     */
    Optional<RoleEntity> findByName(String name);
}

