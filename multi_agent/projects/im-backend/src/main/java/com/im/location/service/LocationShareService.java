package com.im.location.service;

import com.im.location.entity.LocationShareEntity;
import com.im.location.repository.LocationShareRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LocationShareService {
    
    @Autowired
    private LocationShareRepository locationShareRepository;
    
    @Transactional
    public LocationShareEntity startLocationShare(LocationShareEntity share) {
        share.setIsActive(true);
        share.setCreatedAt(LocalDateTime.now());
        if (share.getDurationMinutes() != null) {
            share.setExpiresAt(LocalDateTime.now().plusMinutes(share.getDurationMinutes()));
        }
        return locationShareRepository.save(share);
    }
    
    @Transactional
    public LocationShareEntity updateLocation(UUID id, Double latitude, Double longitude) {
        locationShareRepository.updateLocation(id, latitude, longitude, LocalDateTime.now());
        return locationShareRepository.findById(id).orElse(null);
    }
    
    @Transactional
    public void stopLocationShare(UUID id) {
        Optional<LocationShareEntity> optionalShare = locationShareRepository.findById(id);
        if (optionalShare.isPresent()) {
            LocationShareEntity share = optionalShare.get();
            share.setIsActive(false);
            share.setEndedAt(LocalDateTime.now());
            locationShareRepository.save(share);
        }
    }
    
    public List<LocationShareEntity> getActiveSharesByUser(UUID userId) {
        return locationShareRepository.findActiveSharesByUser(userId, LocalDateTime.now());
    }
    
    public List<LocationShareEntity> getActiveSharesForRecipient(UUID recipientId) {
        return locationShareRepository.findActiveSharesForRecipient(recipientId, LocalDateTime.now());
    }
    
    public List<LocationShareEntity> getRealtimeShares(UUID userId) {
        return locationShareRepository.findRealtimeSharesByUser(userId);
    }
    
    public Optional<LocationShareEntity> getShareById(UUID id) {
        return locationShareRepository.findById(id);
    }
    
    @Transactional
    public void stopAllUserShares(UUID userId) {
        locationShareRepository.deactivateAllUserShares(userId, LocalDateTime.now());
    }
    
    @Transactional
    public void cleanupExpiredShares() {
        locationShareRepository.deactivateExpiredShares(LocalDateTime.now());
    }
    
    @Transactional
    public void cleanupStaleShares(int staleMinutes) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(staleMinutes);
        locationShareRepository.deactivateExpiredShares(threshold);
    }
    
    public long countActiveShares(UUID userId) {
        return locationShareRepository.countActiveSharesByUser(userId);
    }
}