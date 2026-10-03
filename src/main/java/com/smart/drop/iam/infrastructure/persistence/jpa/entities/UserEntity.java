package com.smart.drop.iam.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad JPA que mapea a la tabla Users.
 * Utiliza anotaciones de persistencia para mapeo ORM.
 */
@Entity
@Table(name = "Users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId")
    private Integer userId;

    @Column(name = "FullName", nullable = false, length = 100)
    private String fullName;

    @Column(name = "Email", nullable = false, length = 150, unique = true)
    private String email;

    @Column(name = "PasswordHash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "CreatedAt", nullable = true)
    private LocalDateTime createdAt;

    @Column(name = "ResetToken", length = 100)
    private String resetToken;

    @Column(name = "IsTwoFactorEnabled", nullable = false)
    private Boolean isTwoFactorEnabled = false;

    @Column(name = "TwoFactorCode", length = 6)
    private String twoFactorCode;

    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "UserRoles",
            joinColumns = @JoinColumn(name = "UserId"),
            inverseJoinColumns = @JoinColumn(name = "RoleId")
    )
    private Set<RoleEntity> roles = new HashSet<>();

    // Constructores
    public UserEntity() {
    }

    public UserEntity(String fullName, String email, String passwordHash) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    public void addRole(RoleEntity role) {
        this.roles.add(role);
    }

    public void removeRole(RoleEntity role) {
        this.roles.remove(role);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

