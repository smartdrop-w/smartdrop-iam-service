package com.smart.drop.iam.domain.model.repositories;

import com.smart.drop.iam.domain.model.Role;
import java.util.Optional;
import java.util.List;

/**
 * Interfaz de repositorio para la entidad Role.
 * Define los métodos para persistir y recuperar roles del dominio.
 */
public interface RoleRepository {

    /**
     * Guarda un rol en la persistencia.
     *
     * @param role el rol a guardar
     * @return el rol guardado con ID asignado
     */
    Role save(Role role);

    /**
     * Obtiene un rol por su ID.
     *
     * @param id el ID del rol
     * @return Optional con el rol si existe
     */
    Optional<Role> findById(Integer id);

    /**
     * Obtiene un rol por su nombre.
     *
     * @param name el nombre del rol
     * @return Optional con el rol si existe
     */
    Optional<Role> findByName(String name);

    /**
     * Obtiene todos los roles.
     *
     * @return lista de todos los roles
     */
    List<Role> findAll();

    /**
     * Actualiza un rol existente.
     *
     * @param role el rol a actualizar
     * @return el rol actualizado
     */
    Role update(Role role);

    /**
     * Elimina un rol por su ID.
     *
     * @param id el ID del rol a eliminar
     * @return true si fue eliminado, false si no existe
     */
    boolean deleteById(Integer id);

    /**
     * Cuenta el total de roles.
     *
     * @return el número total de roles
     */
    long count();
}

