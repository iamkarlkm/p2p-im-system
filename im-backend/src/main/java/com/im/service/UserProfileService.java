package com.im.service;

import com.im.dto.UserProfileDTO;
import com.im.dto.UserProfileRequest;
import com.im.entity.UserProfileEntity;
import com.im.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户资料服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Transactional
    public UserProfileDTO createOrUpdateProfile(String userId, UserProfileRequest request) {
        UserProfileEntity entity = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> UserProfileEntity.builder().userId(userId).build());

        if (request.getNickname() != null) entity.setNickname(request.getNickname());
        if (request.getRealName() != null) entity.setRealName(request.getRealName());
        if (request.getAvatarUrl() != null) entity.setAvatarUrl(request.getAvatarUrl());
        if (request.getAvatarThumbnailUrl() != null) entity.setAvatarThumbnailUrl(request.getAvatarThumbnailUrl());
        if (request.getBio() != null) entity.setBio(request.getBio());
        if (request.getGender() != null) entity.setGender(request.getGender());
        if (request.getBirthday() != null) entity.setBirthday(request.getBirthday());
        if (request.getCountry() != null) entity.setCountry(request.getCountry());
        if (request.getProvince() != null) entity.setProvince(request.getProvince());
        if (request.getCity() != null) entity.setCity(request.getCity());
        if (request.getLanguage() != null) entity.setLanguage(request.getLanguage());
        if (request.getTimezone() != null) entity.setTimezone(request.getTimezone());
        if (request.getWebsite() != null) entity.setWebsite(request.getWebsite());
        if (request.getEmail() != null) entity.setEmail(request.getEmail());
        if (request.getPhone() != null) entity.setPhone(request.getPhone());
        if (request.getOnlineStatusVisibility() != null) entity.setOnlineStatusVisibility(request.getOnlineStatusVisibility());
        if (request.getLastSeenVisibility() != null) entity.setLastSeenVisibility(request.getLastSeenVisibility());
        if (request.getAvatarVisibility() != null) entity.setAvatarVisibility(request.getAvatarVisibility());
        if (request.getProfileVisibility() != null) entity.setProfileVisibility(request.getProfileVisibility());
        if (request.getSearchableBy() != null) entity.setSearchableBy(request.getSearchableBy());
        if (request.getFriendRequestPolicy() != null) entity.setFriendRequestPolicy(request.getFriendRequestPolicy());
        if (request.getReadReceiptEnabled() != null) entity.setReadReceiptEnabled(request.getReadReceiptEnabled());
        if (request.getShowOnlineStatus() != null) entity.setShowOnlineStatus(request.getShowOnlineStatus());
        if (request.getShowTypingStatus() != null) entity.setShowTypingStatus(request.getShowTypingStatus());

        entity = userProfileRepository.save(entity);
        log.info("用户资料更新: userId={}", userId);
        return UserProfileDTO.fromEntity(entity);
    }

    public UserProfileDTO getProfile(String userId) {
        UserProfileEntity entity = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("用户资料不存在: " + userId));
        return UserProfileDTO.fromEntity(entity);
    }

    public UserProfileDTO getPublicProfile(String userId) {
        UserProfileEntity entity = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("用户资料不存在: " + userId));
        return UserProfileDTO.publicProfile(entity);
    }

    public List<UserProfileDTO> getProfilesByUserIds(List<String> userIds) {
        return userProfileRepository.findByUserIds(userIds).stream()
                .map(UserProfileDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<String> searchProfiles(String keyword) {
        return userProfileRepository.searchByKeyword(keyword);
    }

    @Transactional
    public void deleteProfile(String userId) {
        userProfileRepository.deleteByUserId(userId);
        log.info("用户资料删除: userId={}", userId);
    }

    public boolean hasProfile(String userId) {
        return userProfileRepository.existsByUserId(userId);
    }
}
