package com.smart.drop.iam.domain.exceptions;

/**
 * Excepción lanzada cuando se intenta asignar un rol a un usuario que ya lo tiene.
 */
public class UserAlreadyHasRoleException extends RuntimeException {

    public UserAlreadyHasRoleException(String message) {
        super(message);
    }

    public UserAlreadyHasRoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public static UserAlreadyHasRoleException forUserAndRole(Integer userId, String roleName) {
        return new UserAlreadyHasRoleException("User " + userId + " already has role: " + roleName);
    }
}

