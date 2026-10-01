package com.smart.drop.iam.domain.model;

/**
 * Entidad de dominio pura para Rol.
 * Representa un rol del sistema sin anotaciones JPA o Spring.
 */
public class Role {
    private final Integer id;
    private final String name;
    private final String description;

    private Role(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Factory method para crear un nuevo rol
    public static Role create(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or blank");
        }
        return new Role(null, name, description);
    }

    // Factory method para crear un rol desde BD (con ID)
    public static Role createFromDatabase(Integer id, String name, String description) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id must be a positive integer");
        }
        return new Role(id, name, description);
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id != null && id.equals(role.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

