package com.im.location.service;

import com.im.location.entity.GeofenceEntity;
import com.im.location.repository.GeofenceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GeofenceService {
    
    @Autowired
    private GeofenceRepository geofenceRepository;
    
    @Transactional
    public GeofenceEntity createGeofence(GeofenceEntity geofence) {
        return geofenceRepository.save(geofence);
    }
    
    @Transactional
    public GeofenceEntity updateGeofence(UUID id, GeofenceEntity updates) {
        Optional<GeofenceEntity> optional = geofenceRepository.findById(id);
        if (optional.isPresent()) {
            GeofenceEntity existing = optional.get();
            if (updates.getName() != null) existing.setName(updates.getName());
            if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
            if (updates.getLatitude() != null) existing.setLatitude(updates.getLatitude());
            if (updates.getLongitude() != null) existing.setLongitude(updates.getLongitude());
            if (updates.getRadiusMeters() != null) existing.setRadiusMeters(updates.getRadiusMeters());
            if (updates.getNotificationMessage() != null) existing.setNotificationMessage(updates.getNotificationMessage());
            if (updates.getPriority() != null) existing.setPriority(updates.getPriority());
            return geofenceRepository.save(existing);
        }
        return null;
    }
    
    @Transactional
    public void deleteGeofence(UUID id) {
        geofenceRepository.deleteById(id);
    }
    
    public List<GeofenceEntity> getUserGeofences(UUID userId) {
        return geofenceRepository.findByUserOrdered(userId);
    }
    
    public List<GeofenceEntity> getActiveGeofences(UUID userId) {
        return geofenceRepository.findActiveGeofences(userId, LocalDateTime.now());
    }
    
    public Optional<GeofenceEntity> getGeofenceById(UUID id) {
        return geofenceRepository.findById(id);
    }
    
    @Transactional
    public void deactivateGeofence(UUID id) {
        Optional<GeofenceEntity> optional = geofenceRepository.findById(id);
        if (optional.isPresent()) {
            GeofenceEntity geofence = optional.get();
            geofence.setIsActive(false);
            geofenceRepository.save(geofence);
        }
    }
    
    @Transactional
    public void activateGeofence(UUID id) {
        Optional<GeofenceEntity> optional = geofenceRepository.findById(id);
        if (optional.isPresent()) {
            GeofenceEntity geofence = optional.get();
            geofence.setIsActive(true);
            geofenceRepository.save(geofence);
        }
    }
    
    @Transactional
    public void triggerGeofence(UUID id) {
        geofenceRepository.incrementTriggerCount(id, LocalDateTime.now());
    }
    
    public long countActiveGeofences(UUID userId) {
        return geofenceRepository.countActiveGeofences(userId);
    }
    
    @Transactional
    public void deleteAllUserGeofences(UUID userId) {
        geofenceRepository.deleteByUserId(userId);
    }
}