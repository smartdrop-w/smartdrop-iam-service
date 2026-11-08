package com.smart.drop.profiles.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Detalle completo del perfil de usuario y sus preferencias de notificación")
public record ProfileResponse(
        @Schema(description = "Identificador único del perfil", example = "10")
        Integer profileId,

        @Schema(description = "Identificador del usuario IAM vinculado", example = "2")
        Integer userId,

        @Schema(description = "Nombre completo del usuario", example = "Valeria Brewer")
        String fullName,

        @Schema(description = "Correo electrónico de contacto", example = "valeria.brewer@artesanal.pe")
        String email,

        @Schema(description = "Idioma configurado", example = "es-PE")
        String language,

        @Schema(description = "Estado de notificaciones globales", example = "true")
        Boolean notificationsEnabled,

        @Schema(description = "Fecha de creación del perfil", example = "2026-11-13T23:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última modificación", example = "2026-11-13T23:35:00")
        LocalDateTime updatedAt,

        @Schema(description = "Preferencias avanzadas de canal y tema")
        PreferencesResponse preferences
) {
}
