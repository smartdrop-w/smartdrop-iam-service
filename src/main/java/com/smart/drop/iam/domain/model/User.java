package com.smart.drop.iam.domain.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad de dominio pura para Usuario.
 * Representa un usuario del sistema sin anotaciones JPA o Spring.
 */
public class User {
    private final Integer id;
    private String fullName;
    private final String email;
    private String passwordHash;
    private LocalDateTime createdAt;
    private Set<Role> roles;
    private String resetToken;
    private Boolean isTwoFactorEnabled;
    private String twoFactorCode;

    public User(Integer id, String fullName, String email, String passwordHash, LocalDateTime createdAt, Set<Role> roles, String resetToken, Boolean isTwoFactorEnabled, String twoFactorCode) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.resetToken = resetToken;
        this.isTwoFactorEnabled = isTwoFactorEnabled;
        this.twoFactorCode = twoFactorCode;
    }

    // Factory method para crear un nuevo usuario
    public static User create(String fullName, String email, String passwordHash) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("fullName cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email cannot be null or blank");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("passwordHash cannot be null or blank");
        }
        return new User(null, fullName, email, passwordHash, LocalDateTime.now(), new HashSet<>(), null, false, null);
    }

    // Factory method para crear un usuario desde BD (con ID)
    public static User createFromDatabase(Integer id, String fullName, String email, String passwordHash, Set<Role> roles, LocalDateTime createdAt, String resetToken, Boolean isTwoFactorEnabled, String twoFactorCode) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id must be a positive integer");
        }
        return new User(id, fullName, email, passwordHash, createdAt, roles, resetToken, isTwoFactorEnabled, twoFactorCode);
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Boolean getIsTwoFactorEnabled() {
        return isTwoFactorEnabled;
    }

    public void setIsTwoFactorEnabled(Boolean isTwoFactorEnabled) {
        this.isTwoFactorEnabled = isTwoFactorEnabled;
    }

    public String getTwoFactorCode() {
        return twoFactorCode;
    }

    public void setTwoFactorCode(String twoFactorCode) {
        this.twoFactorCode = twoFactorCode;
    }

    // Methods de negocio
    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(r -> r.getName().equals(roleName));
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", rolesCount=" + roles.size() +
                ", createdAt=" + createdAt +
                '}';
    }
}

