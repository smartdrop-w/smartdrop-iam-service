package com.smart.drop.iam.domain.model.repositories;

import com.smart.drop.iam.domain.model.User;
import java.util.Optional;
import java.util.List;

/**
 * Interfaz de repositorio para la entidad User.
 * Define los métodos para persistir y recuperar usuarios del dominio.
 */
public interface UserRepository {

    /**
     * Guarda un usuario en la persistencia.
     *
     * @param user el usuario a guardar
     * @return el usuario guardado con ID asignado
     */
    User save(User user);

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id el ID del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findById(Integer id);

    /**
     * Obtiene un usuario por su email.
     *
     * @param email el email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Obtiene todos los usuarios.
     *
     * @return lista de todos los usuarios
     */
    List<User> findAll();

    /**
     * Actualiza un usuario existente.
     *
     * @param user el usuario a actualizar
     * @return el usuario actualizado
     */
    User update(User user);

    /**
     * Elimina un usuario por su ID.
     *
     * @param id el ID del usuario a eliminar
     * @return true si fue eliminado, false si no existe
     */
    boolean deleteById(Integer id);

    /**
     * Cuenta el total de usuarios.
     *
     * @return el número total de usuarios
     */
    long count();
}

