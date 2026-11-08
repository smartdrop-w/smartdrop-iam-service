package com.smart.drop.profiles.interfaces.rest;

import com.smart.drop.profiles.application.services.ProfileService;
import com.smart.drop.profiles.domain.model.aggregates.Profile;
import com.smart.drop.profiles.domain.model.entities.Preferences;
import com.smart.drop.profiles.interfaces.rest.dto.CreateProfileRequest;
import com.smart.drop.profiles.interfaces.rest.dto.PreferencesResponse;
import com.smart.drop.profiles.interfaces.rest.dto.ProfileResponse;
import com.smart.drop.profiles.interfaces.rest.dto.UpdateProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para administración de perfiles personales y empresariales de clientes SmartDrop.
 */
@Tag(name = "User Profiles", description = "Gestión de datos de perfil de usuario, idiomas y vinculación con preferencias")
@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Operation(summary = "Crear perfil de usuario", description = "Crea un nuevo perfil asociado a un usuario existente de IAM.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil creado exitosamente", content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de perfil inválidos o userId ya asociado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<ProfileResponse> create(@Valid @RequestBody CreateProfileRequest request) {
        Profile created = profileService.createProfile(
                request.userId(),
                request.fullName(),
                request.email(),
                request.language(),
                request.notificationsEnabled()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @Operation(summary = "Obtener perfil por ID", description = "Recupera la información completa del perfil por su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado", content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileResponse> getById(@PathVariable Integer profileId) {
        return profileService.getProfileById(profileId)
                .map(profile -> ResponseEntity.ok(toResponse(profile)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar todos los perfiles", description = "Retorna la colección completa de perfiles registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de perfiles obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<ProfileResponse>> getAll() {
        List<ProfileResponse> data = profileService.getAllProfiles().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(data);
    }

    @Operation(summary = "Obtener perfil por userId", description = "Recupera el perfil asociado a una cuenta de IAM específica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado", content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ProfileResponse> getByUserId(@PathVariable Integer userId) {
        return profileService.getProfileByUserId(userId)
                .map(profile -> ResponseEntity.ok(toResponse(profile)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar perfil de usuario", description = "Modifica los datos personales o configuración de notificaciones del perfil.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente", content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @PutMapping("/{profileId}")
    public ResponseEntity<ProfileResponse> update(
            @PathVariable Integer profileId,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        Profile updated = profileService.updateProfile(
                profileId,
                request.fullName(),
                request.email(),
                request.language(),
                request.notificationsEnabled()
        );
        return ResponseEntity.ok(toResponse(updated));
    }

    @Operation(summary = "Eliminar perfil", description = "Elimina permanentemente el registro de perfil.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Perfil eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> delete(@PathVariable Integer profileId) {
        profileService.deleteProfile(profileId);
        return ResponseEntity.noContent().build();
    }

    private ProfileResponse toResponse(Profile profile) {
        return new ProfileResponse(
                profile.profileId(),
                profile.userId(),
                profile.fullName(),
                profile.email(),
                profile.language(),
                profile.notificationsEnabled(),
                profile.createdAt(),
                profile.updatedAt(),
                toPreferencesResponse(profile.preferences())
        );
    }

    private PreferencesResponse toPreferencesResponse(Preferences preferences) {
        if (preferences == null) {
            return null;
        }
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
