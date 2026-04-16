package com.im.service.geofence.service.impl;

import com.im.service.geofence.dto.LocationShareRequest;
import com.im.service.geofence.dto.LocationShareResponse;
import com.im.service.geofence.entity.LocationShare;
import com.im.service.geofence.repository.LocationShareRepository;
import com.im.service.geofence.service.LocationShareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 位置分享服务实现
 */
@Service
public class LocationShareServiceImpl implements LocationShareService {

    private static final Logger logger = LoggerFactory.getLogger(LocationShareServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final LocationShareRepository shareRepository;

    public LocationShareServiceImpl(LocationShareRepository shareRepository) {
        this.shareRepository = shareRepository;
    }

    @Override
    @Transactional
    public LocationShareResponse startLocationShare(LocationShareRequest request) {
        LocationShare share = new LocationShare();
        share.setShareId(generateShareId());
        share.setUserId(request.getUserId());
        share.setRecipientId(request.getRecipientId());
        share.setLatitude(request.getLatitude());
        share.setLongitude(request.getLongitude());
        share.setAccuracy(request.getAccuracy());
        share.setAddress(request.getAddress());
        share.setShareType(request.getShareType() != null ? request.getShareType() : "REALTIME");
        share.setIsActive(true);

        // 设置过期时间
        if (request.getDurationMinutes() != null && request.getDurationMinutes() > 0) {
            share.setDurationMinutes(request.getDurationMinutes());
            share.setExpiresAt(LocalDateTime.now().plusMinutes(request.getDurationMinutes()));
        } else {
            // 默认24小时
            share.setDurationMinutes(1440);
            share.setExpiresAt(LocalDateTime.now().plusDays(1));
        }

        share.setCreateTime(LocalDateTime.now());
        share.setUpdateTime(LocalDateTime.now());

        share = shareRepository.save(share);
        logger.info("Started location share: {} for user: {}", share.getShareId(), share.getUserId());

        return toResponse(share);
    }

    @Override
    @Transactional
    public LocationShareResponse updateLocation(String shareId, Double latitude, Double longitude) {
        LocationShare share = shareRepository.findByShareId(shareId);
        
        if (share == null) {
            throw new RuntimeException("Location share not found: " + shareId);
        }

        if (!share.getIsActive()) {
            throw new RuntimeException("Location share is not active: " + shareId);
        }

        if (share.getExpiresAt() != null && LocalDateTime.now().isAfter(share.getExpiresAt())) {
            throw new RuntimeException("Location share has expired: " + shareId);
        }

        share.setLatitude(latitude);
        share.setLongitude(longitude);
        share.setUpdateTime(LocalDateTime.now());

        share = shareRepository.save(share);
        logger.info("Updated location for share: {}", shareId);

        return toResponse(share);
    }

    @Override
    @Transactional
    public boolean stopLocationShare(String shareId) {
        LocationShare share = shareRepository.findByShareId(shareId);
        
        if (share == null) {
            return false;
        }

        share.setIsActive(false);
        share.setUpdateTime(LocalDateTime.now());
        shareRepository.save(share);
        
        logger.info("Stopped location share: {}", shareId);
        return true;
    }

    @Override
    public List<LocationShareResponse> getActiveShares(String userId) {
        return shareRepository.findByUserIdAndIsActiveTrue(userId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<LocationShareResponse> getActiveSharesForRecipient(String recipientId) {
        return shareRepository.findByRecipientIdAndIsActiveTrue(recipientId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean stopAllShares(String userId) {
        int count = shareRepository.deactivateAllByUserId(userId);
        logger.info("Stopped {} location shares for user: {}", count, userId);
        return count > 0;
    }

    @Override
    public LocationShareResponse getShareById(String shareId) {
        return shareRepository.findByShareId(shareId) != null 
            ? toResponse(shareRepository.findByShareId(shareId)) 
            : null;
    }

    @Override
    @Transactional
    public void cleanupExpiredShares() {
        List<LocationShare> expiredShares = shareRepository.findExpiredShares();
        for (LocationShare share : expiredShares) {
            share.setIsActive(false);
            shareRepository.save(share);
        }
        if (!expiredShares.isEmpty()) {
            logger.info("Cleaned up {} expired location shares", expiredShares.size());
        }
    }

    private LocationShareResponse toResponse(LocationShare share) {
        LocationShareResponse response = new LocationShareResponse();
        response.setId(share.getId() != null ? share.getId().toString() : null);
        response.setShareId(share.getShareId());
        response.setUserId(share.getUserId());
        response.setRecipientId(share.getRecipientId());
        response.setLatitude(share.getLatitude());
        response.setLongitude(share.getLongitude());
        response.setAccuracy(share.getAccuracy());
        response.setAddress(share.getAddress());
        response.setShareType(share.getShareType());
        response.setIsActive(share.getIsActive());
        response.setDurationMinutes(share.getDurationMinutes());
        
        if (share.getExpiresAt() != null) {
            response.setExpiresAt(share.getExpiresAt().format(FORMATTER));
        }
        if (share.getCreateTime() != null) {
            response.setCreateTime(share.getCreateTime().format(FORMATTER));
        }
        
        return response;
    }

    private String generateShareId() {
        return "SHR_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
