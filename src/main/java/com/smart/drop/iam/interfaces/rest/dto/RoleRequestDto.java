package com.smart.drop.iam.interfaces.rest.dto;

/**
 * Record DTO para solicitudes de creación/actualización de roles.
 */
public record RoleRequestDto(
        String name,
        String description
) {
}

