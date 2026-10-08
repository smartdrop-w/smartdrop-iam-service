package com.smart.drop.iam.domain.exceptions;

/**
 * Excepción lanzada cuando se intenta crear un rol que ya existe.
 */
public class RoleAlreadyExistsException extends RuntimeException {

    public RoleAlreadyExistsException(String message) {
        super(message);
    }

    public RoleAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public static RoleAlreadyExistsException withName(String name) {
        return new RoleAlreadyExistsException("Role already exists with name: " + name);
    }
}

