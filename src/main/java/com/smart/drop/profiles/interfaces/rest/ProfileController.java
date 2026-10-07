package com.smart.drop.profiles.interfaces.rest;

import com.smart.drop.profiles.application.services.ProfileService;
import com.smart.drop.profiles.domain.model.aggregates.Profile;
import com.smart.drop.profiles.domain.model.entities.Preferences;
import com.smart.drop.profiles.interfaces.rest.dto.CreateProfileRequest;
import com.smart.drop.profiles.interfaces.rest.dto.PreferencesResponse;
import com.smart.drop.profiles.interfaces.rest.dto.ProfileResponse;
import com.smart.drop.profiles.interfaces.rest.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

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

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileResponse> getById(@PathVariable Integer profileId) {
        return profileService.getProfileById(profileId)
                .map(profile -> ResponseEntity.ok(toResponse(profile)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProfileResponse>> getAll() {
        List<ProfileResponse> data = profileService.getAllProfiles().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ProfileResponse> getByUserId(@PathVariable Integer userId) {
        return profileService.getProfileByUserId(userId)
                .map(profile -> ResponseEntity.ok(toResponse(profile)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

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

