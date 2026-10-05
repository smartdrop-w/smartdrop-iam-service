package com.smart.drop.iam.application.services;

import com.smart.drop.iam.domain.model.Role;
import com.smart.drop.iam.domain.model.repositories.RoleRepository;
import com.smart.drop.iam.domain.exceptions.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para gestión de roles.
 * Orquesta la lógica de negocio de roles.
 */
@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Crea un nuevo rol.
     *
     * @param name el nombre del rol
     * @param description la descripción del rol
     * @return el rol creado y persistido
     */
    public Role createRole(String name, String description) {
        // Valida que el rol no exista
        if (roleRepository.findByName(name).isPresent()) {
            throw RoleAlreadyExistsException.withName(name);
        }

        // Crea la entidad de dominio
        Role role = Role.create(name, description);

        // Persiste y retorna
        return roleRepository.save(role);
    }

    /**
     * Obtiene un rol por ID.
     *
     * @param roleId el ID del rol
     * @return Optional con el rol si existe
     */
    public Optional<Role> getRole(Integer roleId) {
        return roleRepository.findById(roleId);
    }

    /**
     * Obtiene un rol por nombre.
     *
     * @param name el nombre del rol
     * @return Optional con el rol si existe
     */
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    /**
     * Obtiene todos los roles.
     *
     * @return lista de todos los roles
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Actualiza un rol existente.
     *
     * @param roleId el ID del rol a actualizar
     * @param name el nuevo nombre (o null si no cambia)
     * @param description la nueva descripción (o null si no cambia)
     * @return el rol actualizado
     */
    public Role updateRole(Integer roleId, String name, String description) {
        // Obtiene el rol
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> RoleNotFoundException.withId(roleId));

        // Si el nombre cambió, valida que no exista otro rol con ese nombre
        if (name != null && !name.isBlank() && !name.equals(role.getName())) {
            if (roleRepository.findByName(name).isPresent()) {
                throw RoleAlreadyExistsException.withName(name);
            }
        }

        // Crea el rol actualizado
        String newName = (name != null && !name.isBlank()) ? name : role.getName();
        String newDescription = description != null ? description : role.getDescription();
        Role updatedRole = Role.createFromDatabase(roleId, newName, newDescription);

        // Persiste
        return roleRepository.update(updatedRole);
    }

    /**
     * Elimina un rol por ID.
     *
     * @param roleId el ID del rol a eliminar
     * @return true si fue eliminado, false si no existe
     */
    public boolean deleteRole(Integer roleId) {
        return roleRepository.deleteById(roleId);
    }

    /**
     * Obtiene el total de roles.
     *
     * @return el número total de roles
     */
    public long getTotalRoles() {
        return roleRepository.count();
    }
}

