package com.smart.drop.iam.infrastructure.persistence.jpa.adapters;

import com.smart.drop.iam.domain.model.User;
import com.smart.drop.iam.domain.model.Role;
import com.smart.drop.iam.domain.model.repositories.UserRepository;
import com.smart.drop.iam.domain.exceptions.UserNotFoundException;
import com.smart.drop.iam.infrastructure.persistence.jpa.entities.UserEntity;
import com.smart.drop.iam.infrastructure.persistence.jpa.entities.RoleEntity;
import com.smart.drop.iam.infrastructure.persistence.jpa.repositories.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa la interfaz UserRepository del dominio.
 * Traduce entre entidades JPA y entidades de dominio.
 */
@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = new UserEntity(user.getFullName(), user.getEmail(), user.getPasswordHash());
        entity.setResetToken(user.getResetToken());
        entity.setIsTwoFactorEnabled(user.getIsTwoFactorEnabled() != null ? user.getIsTwoFactorEnabled() : false);
        entity.setTwoFactorCode(user.getTwoFactorCode());
        UserEntity saved = userJpaRepository.save(entity);
        return toDomainModel(saved);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userJpaRepository.findById(id).map(this::toDomainModel);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(this::toDomainModel);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll()
                .stream()
                .map(this::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public User update(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("Cannot update user without ID");
        }

        UserEntity entity = userJpaRepository.findById(user.getId())
                .orElseThrow(() -> UserNotFoundException.withId(user.getId()));

        entity.setFullName(user.getFullName());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setResetToken(user.getResetToken());
        entity.setIsTwoFactorEnabled(user.getIsTwoFactorEnabled() != null ? user.getIsTwoFactorEnabled() : false);
        entity.setTwoFactorCode(user.getTwoFactorCode());

        // Actualiza los roles
        entity.setRoles(user.getRoles().stream()
                .map(role -> {
                    RoleEntity roleEntity = new RoleEntity();
                    roleEntity.setRoleId(role.getId());
                    roleEntity.setName(role.getName());
                    roleEntity.setDescription(role.getDescription());
                    return roleEntity;
                })
                .collect(Collectors.toSet()));

        UserEntity updated = userJpaRepository.save(entity);
        return toDomainModel(updated);
    }

    @Override
    public boolean deleteById(Integer id) {
        if (userJpaRepository.existsById(id)) {
            userJpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public long count() {
        return userJpaRepository.count();
    }

    /**
     * Traduce una entidad JPA a una entidad de dominio.
     *
     * @param entity la entidad JPA
     * @return la entidad de dominio
     */
    private User toDomainModel(UserEntity entity) {
        var roles = entity.getRoles().stream()
                .map(roleEntity -> Role.createFromDatabase(
                        roleEntity.getRoleId(),
                        roleEntity.getName(),
                        roleEntity.getDescription()
                ))
                .collect(Collectors.toSet());

        return User.createFromDatabase(
                entity.getUserId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                roles,
                entity.getCreatedAt(),
                entity.getResetToken(),
                entity.getIsTwoFactorEnabled(),
                entity.getTwoFactorCode()
        );
    }
}

