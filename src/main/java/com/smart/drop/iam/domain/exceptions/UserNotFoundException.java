package com.smart.drop.iam.domain.exceptions;

/**
 * Excepción lanzada cuando un usuario no es encontrado.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static UserNotFoundException withId(Integer userId) {
        return new UserNotFoundException("User not found with id: " + userId);
    }

    public static UserNotFoundException withEmail(String email) {
        return new UserNotFoundException("User not found with email: " + email);
    }
}

