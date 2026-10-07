package com.smart.drop.iam.interfaces.rest.dto;

/**
 * Record DTO para solicitudes de asignación de roles.
 */
public record AssignRoleRequestDto(
        Integer userId,
        String roleName
) {
}

