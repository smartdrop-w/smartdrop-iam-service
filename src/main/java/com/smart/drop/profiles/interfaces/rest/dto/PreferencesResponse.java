package com.smart.drop.profiles.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Preferencias de alertas, notificaciones y apariencia de la aplicación")
public record PreferencesResponse(
        @Schema(description = "Identificador único de las preferencias", example = "5")
        Integer preferenceId,

        @Schema(description = "Identificador del perfil asociado", example = "10")
        Integer profileId,

        @Schema(description = "Canal prioritario de alertas críticas (EMAIL, SMS, PUSH)", example = "EMAIL")
        String alertChannel,

        @Schema(description = "Indicador de notificaciones operativas activas", example = "true")
        Boolean notificationEnabled,

        @Schema(description = "Tema visual de interfaz (LIGHT, DARK, SYSTEM)", example = "DARK")
        String themeMode,

        @Schema(description = "Fecha de última actualización de preferencias", example = "2026-11-13T23:35:00")
        LocalDateTime updatedAt
) {
}
