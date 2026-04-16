package com.im.service.geofence.controller;

import com.im.service.geofence.dto.*;
import com.im.service.geofence.entity.GeofenceEvent;
import com.im.service.geofence.service.GeofenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地理围栏REST控制器
 * 对接移动端 GeofenceService 的所有API调用
 * 
 * 路径前缀: /api/v1/geofencing (匹配移动端期望)
 * 
 * @author IM Development Team
 * @since 2026-04-12
 */
@RestController
@RequestMapping("/api/v1/geofencing")
@Validated
public class GeofenceController {

    private static final Logger logger = LoggerFactory.getLogger(GeofenceController.class);

    private final GeofenceService geofenceService;

    public GeofenceController(GeofenceService geofenceService) {
        this.geofenceService = geofenceService;
    }

    // ==================== 围栏CRUD ====================

    /**
     * 创建地理围栏
     * POST /api/v1/geofencing/geofences
     */
    @PostMapping("/geofences")
    public ResponseEntity<Map<String, Object>> createGeofence(@RequestBody @Validated CreateGeofenceRequest request) {
        logger.info("Creating geofence: {}", request.getName());
        try {
            GeofenceResponse response = geofenceService.createGeofence(request);
            return ResponseEntity.ok(successResponse(response));
        } catch (Exception e) {
            logger.error("Failed to create geofence", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 更新地理围栏
     * PUT /api/v1/geofencing/geofences/{id}
     */
    @PutMapping("/geofences/{id}")
    public ResponseEntity<Map<String, Object>> updateGeofence(
            @PathVariable String id,
            @RequestBody @Validated CreateGeofenceRequest request) {
        logger.info("Updating geofence: {}", id);
        try {
            GeofenceResponse response = geofenceService.updateGeofence(id, request);
            return ResponseEntity.ok(successResponse(response));
        } catch (Exception e) {
            logger.error("Failed to update geofence", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 删除地理围栏
     * DELETE /api/v1/geofencing/geofences/{id}
     */
    @DeleteMapping("/geofences/{id}")
    public ResponseEntity<Map<String, Object>> deleteGeofence(@PathVariable String id) {
        logger.info("Deleting geofence: {}", id);
        try {
            boolean success = geofenceService.deleteGeofence(id);
            return ResponseEntity.ok(successResponse(success));
        } catch (Exception e) {
            logger.error("Failed to delete geofence", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 获取围栏详情
     * GET /api/v1/geofencing/geofences/{id}
     */
    @GetMapping("/geofences/{id}")
    public ResponseEntity<Map<String, Object>> getGeofenceDetail(@PathVariable String id) {
        logger.info("Getting geofence detail: {}", id);
        try {
            GeofenceResponse response = geofenceService.getGeofenceById(id);
            if (response == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(successResponse(response));
        } catch (Exception e) {
            logger.error("Failed to get geofence", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    // ==================== 用户围栏 ====================

    /**
     * 获取用户的所有围栏
     * GET /api/v1/geofencing/geofences/user/{userId}
     */
    @GetMapping("/geofences/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserGeofences(@PathVariable String userId) {
        logger.info("Getting user geofences: {}", userId);
        try {
            // 这里假设 userId 关联到某个 merchant，实际实现可能需要调整
            List<GeofenceResponse> geofences = geofenceService.getAllActiveGeofences();
            return ResponseEntity.ok(successResponse(geofences));
        } catch (Exception e) {
            logger.error("Failed to get user geofences", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 获取用户的激活围栏
     * GET /api/v1/geofencing/geofences/user/{userId}/active
     */
    @GetMapping("/geofences/user/{userId}/active")
    public ResponseEntity<Map<String, Object>> getUserActiveGeofences(@PathVariable String userId) {
        logger.info("Getting user active geofences: {}", userId);
        try {
            List<GeofenceResponse> geofences = geofenceService.getAllActiveGeofences();
            return ResponseEntity.ok(successResponse(geofences));
        } catch (Exception e) {
            logger.error("Failed to get user active geofences", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    // ==================== 商户/POI围栏 ====================

    /**
     * 获取商户围栏列表
     * GET /api/v1/geofencing/merchants/{merchantId}/geofences
     */
    @GetMapping("/merchants/{merchantId}/geofences")
    public ResponseEntity<Map<String, Object>> getMerchantGeofences(@PathVariable String merchantId) {
        logger.info("Getting merchant geofences: {}", merchantId);
        try {
            List<GeofenceResponse> geofences = geofenceService.getGeofencesByMerchant(merchantId);
            return ResponseEntity.ok(successResponse(geofences));
        } catch (Exception e) {
            logger.error("Failed to get merchant geofences", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 获取POI围栏列表
     * GET /api/v1/geofencing/pois/{poiId}/geofences
     */
    @GetMapping("/pois/{poiId}/geofences")
    public ResponseEntity<Map<String, Object>> getPoiGeofences(@PathVariable String poiId) {
        logger.info("Getting POI geofences: {}", poiId);
        try {
            List<GeofenceResponse> geofences = geofenceService.getGeofencesByPoi(poiId);
            return ResponseEntity.ok(successResponse(geofences));
        } catch (Exception e) {
            logger.error("Failed to get POI geofences", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 获取围栏层级树
     * GET /api/v1/geofencing/merchants/{merchantId}/geofence-tree
     */
    @GetMapping("/merchants/{merchantId}/geofence-tree")
    public ResponseEntity<Map<String, Object>> getGeofenceTree(@PathVariable String merchantId) {
        logger.info("Getting geofence tree for merchant: {}", merchantId);
        try {
            List<GeofenceResponse> tree = geofenceService.getGeofenceTree(merchantId);
            return ResponseEntity.ok(successResponse(tree));
        } catch (Exception e) {
            logger.error("Failed to get geofence tree", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    // ==================== 围栏操作 ====================

    /**
     * 启用围栏
     * POST /api/v1/geofencing/geofences/{id}/enable
     */
    @PostMapping("/geofences/{id}/enable")
    public ResponseEntity<Map<String, Object>> enableGeofence(@PathVariable String id) {
        logger.info("Enabling geofence: {}", id);
        try {
            GeofenceResponse response = geofenceService.enableGeofence(id);
            return ResponseEntity.ok(successResponse(response));
        } catch (Exception e) {
            logger.error("Failed to enable geofence", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 禁用围栏
     * POST /api/v1/geofencing/geofences/{id}/disable
     */
    @PostMapping("/geofences/{id}/disable")
    public ResponseEntity<Map<String, Object>> disableGeofence(@PathVariable String id) {
        logger.info("Disabling geofence: {}", id);
        try {
            GeofenceResponse response = geofenceService.disableGeofence(id);
            return ResponseEntity.ok(successResponse(response));
        } catch (Exception e) {
            logger.error("Failed to disable geofence", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 复制围栏
     * POST /api/v1/geofencing/geofences/{id}/copy?targetPoiId=xxx
     */
    @PostMapping("/geofences/{id}/copy")
    public ResponseEntity<Map<String, Object>> copyGeofence(
            @PathVariable String id,
            @RequestParam String targetPoiId) {
        logger.info("Copying geofence: {} to POI: {}", id, targetPoiId);
        try {
            String newGeofenceId = geofenceService.copyGeofence(id, targetPoiId);
            return ResponseEntity.ok(successResponse(newGeofenceId));
        } catch (Exception e) {
            logger.error("Failed to copy geofence", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    // ==================== 位置查询 ====================

    /**
     * 查询附近围栏
     * GET /api/v1/geofencing/geofences/nearby?longitude=xxx&latitude=xxx&radius=xxx
     */
    @GetMapping("/geofences/nearby")
    public ResponseEntity<Map<String, Object>> findNearbyGeofences(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "1000") Integer radius) {
        logger.info("Finding nearby geofences: ({}, {}), radius: {}m", longitude, latitude, radius);
        try {
            List<GeofenceResponse> geofences = geofenceService.findNearbyGeofences(longitude, latitude, radius);
            return ResponseEntity.ok(successResponse(geofences));
        } catch (Exception e) {
            logger.error("Failed to find nearby geofences", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 上报位置
     * POST /api/v1/geofencing/location/report
     */
    @PostMapping("/location/report")
    public ResponseEntity<Map<String, Object>> reportLocation(@RequestBody @Validated LocationReportRequest request) {
        logger.info("Reporting location: user={}, ({}, {})", request.getUserId(), request.getLongitude(), request.getLatitude());
        try {
            LocationReportResponse response = geofenceService.reportLocation(request);
            return ResponseEntity.ok(successResponse(response));
        } catch (Exception e) {
            logger.error("Failed to report location", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 检查点是否在围栏内
     * GET /api/v1/geofencing/geofences/{id}/contains?longitude=xxx&latitude=xxx
     */
    @GetMapping("/geofences/{id}/contains")
    public ResponseEntity<Map<String, Object>> isPointInGeofence(
            @PathVariable String id,
            @RequestParam Double longitude,
            @RequestParam Double latitude) {
        try {
            boolean inside = geofenceService.isPointInGeofence(id, longitude, latitude);
            return ResponseEntity.ok(successResponse(inside));
        } catch (Exception e) {
            logger.error("Failed to check point in geofence", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * 查询点所在的所有围栏
     * GET /api/v1/geofencing/geofences/contains-point?longitude=xxx&latitude=xxx
     */
    @GetMapping("/geofences/contains-point")
    public ResponseEntity<Map<String, Object>> findGeofencesByPoint(
            @RequestParam Double longitude,
            @RequestParam Double latitude) {
        try {
            List<String> geofenceIds = geofenceService.findGeofencesByPoint(longitude, latitude);
            return ResponseEntity.ok(successResponse(geofenceIds));
        } catch (Exception e) {
            logger.error("Failed to find geofences by point", e);
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    // ==================== 事件查询 ====================

    /**
     * 获取围栏的事件记录
     * GET /api/v1/geofencing/geofences/{id}/events
     */
    @GetMapping("/geofences/{id}/events")
    public ResponseEntity<Map<String, Object>> getGeofenceEvents(
            @PathVariable String id,
            @RequestParam(defaultValue = "50") Integer limit) {
        try {
            List<GeofenceEvent> events = geofenceService.getGeofenceEvents(id, limit);
            return ResponseEntity.ok(successResponse(events));
        } catch (Exception e) {
            logger.error("Failed to get geofence events", e);
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
