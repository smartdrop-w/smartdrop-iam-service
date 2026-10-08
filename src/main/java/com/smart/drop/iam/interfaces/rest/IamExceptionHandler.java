package com.smart.drop.iam.interfaces.rest;

import com.smart.drop.iam.domain.exceptions.RoleAlreadyExistsException;
import com.smart.drop.iam.domain.exceptions.RoleNotFoundException;
import com.smart.drop.iam.domain.exceptions.UserAlreadyExistsException;
import com.smart.drop.iam.domain.exceptions.UserAlreadyHasRoleException;
import com.smart.drop.iam.domain.exceptions.UserNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class IamExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleUserNotFound(UserNotFoundException ex) {
        return ErrorResponse.create(ex, HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()), ex.getMessage());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleRoleNotFound(RoleNotFoundException ex) {
        return ErrorResponse.create(ex, HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()), ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ErrorResponse.create(ex, HttpStatusCode.valueOf(HttpStatus.CONFLICT.value()), ex.getMessage());
    }

    @ExceptionHandler(RoleAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse handleRoleAlreadyExists(RoleAlreadyExistsException ex) {
        return ErrorResponse.create(ex, HttpStatusCode.valueOf(HttpStatus.CONFLICT.value()), ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyHasRoleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse handleUserAlreadyHasRole(UserAlreadyHasRoleException ex) {
        return ErrorResponse.create(ex, HttpStatusCode.valueOf(HttpStatus.CONFLICT.value()), ex.getMessage());
    }

}
