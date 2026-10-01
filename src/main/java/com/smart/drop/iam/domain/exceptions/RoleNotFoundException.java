package com.smart.drop.iam.domain.exceptions;

/**
 * Excepción lanzada cuando un rol no es encontrado.
 */
public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static RoleNotFoundException withId(Integer roleId) {
        return new RoleNotFoundException("Role not found with id: " + roleId);
    }

    public static RoleNotFoundException withName(String name) {
        return new RoleNotFoundException("Role not found with name: " + name);
    }
}

