package org.example.userservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.example.userservice.dao.IdentityClient;
import org.example.userservice.dao.ProfileDao;
import org.example.userservice.dto.*;
import org.example.userservice.entity.ProfileEntity;
import org.example.userservice.util.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileDao profileDao;

    @Value("${idp.client-id}")
    private String clientId;

    @Value("${idp.client-secret}")
    private String clientSecret;

    public ProfileEntity createProfile(RegistrationDto registrationDto) {
        try {
            // Get userId from keycloak response
            ProfileEntity profile = new ProfileEntity();
            BeanUtils.copyProperties(registrationDto, profile);
            return profileDao.save(profile);
        } catch (FeignException e) {
            int status = e.status();
            switch (status) {
                case 400 -> throw new IllegalArgumentException("Invalid request data: " + e.getMessage());
                case 409 -> throw new IllegalStateException("User already exists: " + e.getMessage());
                case 500 -> throw new RuntimeException("Internal server error: " + e.getMessage());
                default -> throw new RuntimeException("Unexpected error occurred: " + e.getMessage());
            }
        }
    }

    public ProfileEntity getProfileById(String userId) {
        return profileDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for userId: " + userId));
    }

    public ProfileEntity getProfileByUserName(String userName) {
        return profileDao.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for userName: " + userName));
    }

    public ProfileEntity getCurrentUserProfile() {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new IllegalArgumentException("User name not found in request");
        }
        return profileDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for userId: " + userId));
    }

    public List<ProfileEntity> getAllProfiles() {
        return profileDao.findAll();
    }

    public ProfileListResponseDto getProfiles(Integer page, Integer size) {
        Page<ProfileEntity> profilePage = createPageable(page, size);
        ProfileListResponseDto response = new ProfileListResponseDto();
        response.setProfiles(profilePage.getContent());
        response.setPage(profilePage.getNumber());
        response.setSize(profilePage.getSize());
        response.setTotalElements(profilePage.getTotalElements());
        response.setTotalPages(profilePage.getTotalPages());
        return response;
    }

    private Page<ProfileEntity> createPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return profileDao.findAll(pageable);
    }

    public ProfileEntity updateProfile(ProfileRequestDto profile) {
        String userId = UserContext.getCurrentUserId();
        ProfileEntity existingProfile = profileDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for userId: " + userId));
        BeanUtils.copyProperties(profile, existingProfile);
        return profileDao.save(existingProfile);
    }
}
