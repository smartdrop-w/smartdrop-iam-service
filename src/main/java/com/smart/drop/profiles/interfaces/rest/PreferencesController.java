package com.smart.drop.profiles.interfaces.rest;

import com.smart.drop.profiles.application.services.PreferencesService;
import com.smart.drop.profiles.domain.model.entities.Preferences;
import com.smart.drop.profiles.interfaces.rest.dto.PreferencesResponse;
import com.smart.drop.profiles.interfaces.rest.dto.PreferencesUpsertRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/profiles/preferences", "/api/v1/preferences"})
public class PreferencesController {

    private final PreferencesService preferencesService;

    public PreferencesController(PreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<PreferencesResponse> getByProfileId(@PathVariable Integer profileId) {
        return preferencesService.getByProfileId(profileId)
                .map(preferences -> ResponseEntity.ok(toResponse(preferences)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

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

