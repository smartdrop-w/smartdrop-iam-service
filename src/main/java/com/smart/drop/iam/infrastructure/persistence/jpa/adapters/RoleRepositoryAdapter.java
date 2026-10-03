package com.smart.drop.iam.infrastructure.persistence.jpa.adapters;

import com.smart.drop.iam.domain.model.Role;
import com.smart.drop.iam.domain.model.repositories.RoleRepository;
import com.smart.drop.iam.domain.exceptions.RoleNotFoundException;
import com.smart.drop.iam.infrastructure.persistence.jpa.entities.RoleEntity;
import com.smart.drop.iam.infrastructure.persistence.jpa.repositories.RoleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa la interfaz RoleRepository del dominio.
 * Traduce entre entidades JPA y entidades de dominio.
 */
@Component
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;

    public RoleRepositoryAdapter(RoleJpaRepository roleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    public Role save(Role role) {
        RoleEntity entity = new RoleEntity(role.getName(), role.getDescription());
        RoleEntity saved = roleJpaRepository.save(entity);
        return toDomainModel(saved);
    }

    @Override
    public Optional<Role> findById(Integer id) {
        return roleJpaRepository.findById(id).map(this::toDomainModel);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name).map(this::toDomainModel);
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll()
                .stream()
                .map(this::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Role update(Role role) {
        if (role.getId() == null) {
            throw new IllegalArgumentException("Cannot update role without ID");
        }

        RoleEntity entity = roleJpaRepository.findById(role.getId())
                .orElseThrow(() -> RoleNotFoundException.withId(role.getId()));

        entity.setName(role.getName());
        entity.setDescription(role.getDescription());

        RoleEntity updated = roleJpaRepository.save(entity);
        return toDomainModel(updated);
    }

    @Override
    public boolean deleteById(Integer id) {
        if (roleJpaRepository.existsById(id)) {
            roleJpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public long count() {
        return roleJpaRepository.count();
    }

    /**
     * Traduce una entidad JPA a una entidad de dominio.
     *
     * @param entity la entidad JPA
     * @return la entidad de dominio
     */
    private Role toDomainModel(RoleEntity entity) {
        return Role.createFromDatabase(
                entity.getRoleId(),
                entity.getName(),
                entity.getDescription()
        );
    }
}

