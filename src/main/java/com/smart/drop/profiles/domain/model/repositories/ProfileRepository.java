package com.smart.drop.profiles.domain.model.repositories;

import com.smart.drop.profiles.domain.model.aggregates.Profile;
import com.smart.drop.profiles.domain.model.entities.Preferences;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for the Profile aggregate and its Preferences.
 */
public interface ProfileRepository {

    Profile save(Profile profile);

    Profile update(Profile profile);

    Optional<Profile> findById(Integer profileId);

    Optional<Profile> findByUserId(Integer userId);

    List<Profile> findAll();

    void deleteById(Integer profileId);

    Optional<Preferences> findPreferencesByProfileId(Integer profileId);

    Preferences saveOrUpdatePreferences(Integer profileId, Preferences preferences);
}

