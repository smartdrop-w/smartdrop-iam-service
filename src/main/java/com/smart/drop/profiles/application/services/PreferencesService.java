package com.smart.drop.profiles.application.services;

import com.smart.drop.profiles.domain.model.entities.Preferences;
import com.smart.drop.profiles.domain.model.repositories.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PreferencesService {

    private final ProfileRepository profileRepository;

    public PreferencesService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Preferences> getByProfileId(Integer profileId) {
        return profileRepository.findPreferencesByProfileId(profileId);
    }

    public Preferences saveOrUpdate(Integer profileId, String alertChannel, Boolean notificationEnabled, String themeMode) {
        Preferences current = profileRepository.findPreferencesByProfileId(profileId).orElse(null);

        Preferences target = current == null
                ? Preferences.create(profileId, alertChannel, notificationEnabled, themeMode)
                : current.withUpdatedValues(alertChannel, notificationEnabled, themeMode);

        return profileRepository.saveOrUpdatePreferences(profileId, target);
    }
}


