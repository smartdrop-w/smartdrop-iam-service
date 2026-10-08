package com.smart.drop.profiles.infrastructure.persistence.jpa.adapters;

import com.smart.drop.profiles.domain.model.aggregates.Profile;
import com.smart.drop.profiles.domain.model.entities.Preferences;
import com.smart.drop.profiles.domain.model.repositories.ProfileRepository;
import com.smart.drop.profiles.infrastructure.persistence.jpa.entities.PreferencesEntity;
import com.smart.drop.profiles.infrastructure.persistence.jpa.entities.ProfileEntity;
import com.smart.drop.profiles.infrastructure.persistence.jpa.repositories.PreferencesJpaRepository;
import com.smart.drop.profiles.infrastructure.persistence.jpa.repositories.ProfileJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ProfileRepositoryAdapter implements ProfileRepository {

    private final ProfileJpaRepository profileJpaRepository;
    private final PreferencesJpaRepository preferencesJpaRepository;

    public ProfileRepositoryAdapter(ProfileJpaRepository profileJpaRepository,
                                    PreferencesJpaRepository preferencesJpaRepository) {
        this.profileJpaRepository = profileJpaRepository;
        this.preferencesJpaRepository = preferencesJpaRepository;
    }

    @Override
    public Profile save(Profile profile) {
        ProfileEntity entity = toProfileEntity(profile);
        LocalDateTime now = LocalDateTime.now();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setUpdatedAt(now);
        ProfileEntity saved = profileJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Profile update(Profile profile) {
        ProfileEntity existing = profileJpaRepository.findById(profile.profileId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + profile.profileId()));

        existing.setUserId(profile.userId());
        existing.setFullName(profile.fullName());
        existing.setEmail(profile.email());
        existing.setLanguage(profile.language());
        existing.setNotificationsEnabled(profile.notificationsEnabled());
        existing.setUpdatedAt(LocalDateTime.now());

        ProfileEntity updated = profileJpaRepository.save(existing);
        return toDomain(updated);
    }

    @Override
    public Optional<Profile> findById(Integer profileId) {
        return profileJpaRepository.findById(profileId).map(this::toDomain);
    }

    @Override
    public Optional<Profile> findByUserId(Integer userId) {
        return profileJpaRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public List<Profile> findAll() {
        return profileJpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Integer profileId) {
        profileJpaRepository.deleteById(profileId);
    }

    @Override
    public Optional<Preferences> findPreferencesByProfileId(Integer profileId) {
        return preferencesJpaRepository.findByProfile_ProfileId(profileId).map(this::toPreferencesDomain);
    }

    @Override
    public Preferences saveOrUpdatePreferences(Integer profileId, Preferences preferences) {
        ProfileEntity profileEntity = profileJpaRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + profileId));

        PreferencesEntity entity = preferencesJpaRepository.findByProfile_ProfileId(profileId)
                .orElseGet(PreferencesEntity::new);

        entity.setProfile(profileEntity);
        entity.setAlertChannel(preferences.alertChannel());
        entity.setNotificationEnabled(preferences.notificationEnabled());
        entity.setThemeMode(preferences.themeMode());
        entity.setUpdatedAt(LocalDateTime.now());

        PreferencesEntity saved = preferencesJpaRepository.save(entity);
        return toPreferencesDomain(saved);
    }

    private ProfileEntity toProfileEntity(Profile profile) {
        ProfileEntity entity = new ProfileEntity();
        entity.setProfileId(profile.profileId());
        entity.setUserId(profile.userId());
        entity.setFullName(profile.fullName());
        entity.setEmail(profile.email());
        entity.setLanguage(profile.language());
        entity.setNotificationsEnabled(profile.notificationsEnabled());
        entity.setCreatedAt(profile.createdAt());
        entity.setUpdatedAt(profile.updatedAt());
        return entity;
    }

    private Profile toDomain(ProfileEntity entity) {
        Preferences preferences = entity.getPreferences() == null ? null : toPreferencesDomain(entity.getPreferences());
        return new Profile(
                entity.getProfileId(),
                entity.getUserId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getLanguage(),
                entity.getNotificationsEnabled(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                preferences
        );
    }

    private Preferences toPreferencesDomain(PreferencesEntity entity) {
        Integer profileId = entity.getProfile() == null ? null : entity.getProfile().getProfileId();
        return new Preferences(
                entity.getPreferenceId(),
                profileId,
                entity.getAlertChannel(),
                entity.getNotificationEnabled(),
                entity.getThemeMode(),
                entity.getUpdatedAt()
        );
    }
}

