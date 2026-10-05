package com.smart.drop.profiles.application.services;

import com.smart.drop.profiles.domain.model.aggregates.Profile;
import com.smart.drop.profiles.domain.model.repositories.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile createProfile(Integer userId, String fullName, String email, String language, Boolean notificationsEnabled) {
        Profile profile = Profile.create(userId, fullName, email, language, notificationsEnabled);
        return profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public Optional<Profile> getProfileById(Integer profileId) {
        return profileRepository.findById(profileId);
    }

    @Transactional(readOnly = true)
    public Optional<Profile> getProfileByUserId(Integer userId) {
        return profileRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public Profile updateProfile(Integer profileId, String fullName, String email, String language, Boolean notificationsEnabled) {
        Profile current = getProfileById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + profileId));
        Profile updated = current.withUpdatedBasicData(fullName, email, language, notificationsEnabled);
        return profileRepository.update(updated);
    }

    public void deleteProfile(Integer profileId) {
        profileRepository.deleteById(profileId);
    }
}


