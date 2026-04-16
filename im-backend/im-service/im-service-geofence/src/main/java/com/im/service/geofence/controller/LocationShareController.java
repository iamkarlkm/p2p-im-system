package com.im.service.geofence.controller;

import com.im.service.geofence.dto.LocationShareRequest;
import com.im.service.geofence.dto.LocationShareResponse;
import com.im.service.geofence.service.LocationShareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 位置分享REST控制器
 * 对接移动端 LocationService 的位置分享API
 * 
 * 路径前缀: /api/v1/location (匹配移动端期望)
 * 
 * @author IM Development Team
 * @since 2026-04-12
 */
@RestController
@RequestMapping("/api/v1/location")
@Validated
public class LocationShareController {

    private static final Logger logger = LoggerFactory.getLogger(LocationShareController.class);

    private final LocationShareService locationShareService;

    public LocationShareController(LocationShareService locationShareService) {
        this.locationShareService = locationShareService;
    }

    // ==================== 位置分享 ====================

    /**
     * 发起位置分享
     * POST /api/v1/location/share/start
     */
    @PostMapping("/share/start")
    public ResponseEntity<Map<String, Object>> startLocationShare(@RequestBody @Validated LocationShareRequest request) {
        logger.info("Starting location share for user: {}", request.getUserId());
        try {
            LocationShareResponse response = locationShareService.startLocationShare(request);
            return ResponseEntity.ok(successResponse(response));
        } catch (Exception e) {
            logger.error("Failed to start location share", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 更新分享位置
     * PUT /api/v1/location/share/{id}/update
     */
    @PutMapping("/share/{id}/update")
    public ResponseEntity<Map<String, Object>> updateLocation(
            @PathVariable String id,
            @RequestBody Map<String, Double> location) {
        logger.info("Updating location for share: {}", id);
        try {
            Double latitude = location.get("latitude");
            Double longitude = location.get("longitude");
            
            if (latitude == null || longitude == null) {
                return ResponseEntity.badRequest().body(errorResponse("Latitude and longitude are required"));
            }
            
            LocationShareResponse response = locationShareService.updateLocation(id, latitude, longitude);
            return ResponseEntity.ok(successResponse(response));
        } catch (Exception e) {
            logger.error("Failed to update location", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 停止位置分享
     * POST /api/v1/location/share/{id}/stop
     */
    @PostMapping("/share/{id}/stop")
    public ResponseEntity<Map<String, Object>> stopLocationShare(@PathVariable String id) {
        logger.info("Stopping location share: {}", id);
        try {
            boolean success = locationShareService.stopLocationShare(id);
            return ResponseEntity.ok(successResponse(success));
        } catch (Exception e) {
            logger.error("Failed to stop location share", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 获取用户的所有激活分享
     * GET /api/v1/location/share/user/{userId}/active
     */
    @GetMapping("/share/user/{userId}/active")
    public ResponseEntity<Map<String, Object>> getActiveShares(@PathVariable String userId) {
        logger.info("Getting active shares for user: {}", userId);
        try {
            List<LocationShareResponse> shares = locationShareService.getActiveShares(userId);
            return ResponseEntity.ok(successResponse(shares));
        } catch (Exception e) {
            logger.error("Failed to get active shares", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 获取接收者的所有激活分享
     * GET /api/v1/location/share/recipient/{recipientId}/active
     */
    @GetMapping("/share/recipient/{recipientId}/active")
    public ResponseEntity<Map<String, Object>> getActiveSharesForRecipient(@PathVariable String recipientId) {
        logger.info("Getting active shares for recipient: {}", recipientId);
        try {
            List<LocationShareResponse> shares = locationShareService.getActiveSharesForRecipient(recipientId);
            return ResponseEntity.ok(successResponse(shares));
        } catch (Exception e) {
            logger.error("Failed to get active shares for recipient", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 停止用户所有分享
     * POST /api/v1/location/share/user/{userId}/stop-all
     */
    @PostMapping("/share/user/{userId}/stop-all")
    public ResponseEntity<Map<String, Object>> stopAllShares(@PathVariable String userId) {
        logger.info("Stopping all shares for user: {}", userId);
        try {
            boolean success = locationShareService.stopAllShares(userId);
            return ResponseEntity.ok(successResponse(success));
        } catch (Exception e) {
            logger.error("Failed to stop all shares", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 获取分享详情
     * GET /api/v1/location/share/{id}
     */
    @GetMapping("/share/{id}")
    public ResponseEntity<Map<String, Object>> getShareById(@PathVariable String id) {
        try {
            LocationShareResponse response = locationShareService.getShareById(id);
            if (response == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(successResponse(response));
        } catch (Exception e) {
            logger.error("Failed to get share", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    // ==================== 辅助方法 ====================

    private Map<String, Object> successResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("success", true);
        response.put("data", data);
        return response;
    }

    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
