package com.smart.drop.profiles.interfaces.rest;

import com.smart.drop.profiles.application.services.PreferencesService;
import com.smart.drop.profiles.domain.model.entities.Preferences;
import com.smart.drop.profiles.interfaces.rest.dto.PreferencesResponse;
import com.smart.drop.profiles.interfaces.rest.dto.PreferencesUpsertRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para configuración de preferencias de usuario (canales de alerta, notificaciones y tema visual).
 */
@Tag(name = "User Preferences", description = "Endpoints para configuración personalizada de canales de alerta, notificaciones y tema visual de la UI")
@RestController
@RequestMapping({"/api/v1/profiles/preferences", "/api/v1/preferences"})
public class PreferencesController {

    private final PreferencesService preferencesService;

    public PreferencesController(PreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    @Operation(summary = "Obtener preferencias por profileId", description = "Recupera las preferencias configuradas para un perfil de usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preferencias encontradas", content = @Content(schema = @Schema(implementation = PreferencesResponse.class))),
            @ApiResponse(responseCode = "404", description = "Preferencias no encontradas")
    })
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<PreferencesResponse> getByProfileId(@PathVariable Integer profileId) {
        return preferencesService.getByProfileId(profileId)
                .map(preferences -> ResponseEntity.ok(toResponse(preferences)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear o actualizar preferencias", description = "Registra o actualiza en una sola operación las preferencias asociadas a un perfil.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preferencias guardadas exitosamente", content = @Content(schema = @Schema(implementation = PreferencesResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de preferencias inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/profile/{profileId}")
    public ResponseEntity<PreferencesResponse> upsertByProfileId(
            @PathVariable Integer profileId,
            @Valid @RequestBody PreferencesUpsertRequest request
    ) {
        Preferences updated = preferencesService.saveOrUpdate(
                profileId,
                request.alertChannel(),
                request.notificationEnabled(),
                request.themeMode()
        );
        return ResponseEntity.ok(toResponse(updated));
    }

    private PreferencesResponse toResponse(Preferences preferences) {
        return new PreferencesResponse(
                preferences.preferenceId(),
                preferences.profileId(),
                preferences.alertChannel(),
                preferences.notificationEnabled(),
                preferences.themeMode(),
                preferences.updatedAt()
        );
    }
}
