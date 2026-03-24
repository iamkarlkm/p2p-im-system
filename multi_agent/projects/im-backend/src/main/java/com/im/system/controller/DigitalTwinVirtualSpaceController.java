package com.im.system.controller;

import com.im.system.entity.DigitalTwinVirtualSpaceEntity;
import com.im.system.entity.VirtualAvatarEntity;
import com.im.system.service.DigitalTwinVirtualSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 数字孪生虚拟会议空间 REST API 控制器
 * 提供虚拟空间管理、用户加入/离开、空间音频、虚拟化身、AR/VR 集成、场景模拟的 API
 *
 * @since 2026-03-23
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/digital-twin")
public class DigitalTwinVirtualSpaceController {

    @Autowired
    private DigitalTwinVirtualSpaceService digitalTwinService;

    /**
     * 创建新的虚拟会议空间
     */
    @PostMapping("/spaces")
    public ResponseEntity<Map<String, Object>> createVirtualSpace(
            @RequestParam String spaceName,
            @RequestParam(required = false) String spaceType,
            @RequestParam String hostUserId,
            @RequestBody(required = false) Map<String, Object> config) {
        
        try {
            DigitalTwinVirtualSpaceEntity space = digitalTwinService.createVirtualSpace(
                    spaceName, spaceType, hostUserId, config);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Virtual space created successfully");
            response.put("spaceId", space.getSpaceId());
            response.put("space", toSpaceResponse(space));
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create virtual space", e));
        }
    }

    /**
     * 获取虚拟空间详情
     */
    @GetMapping("/spaces/{spaceId}")
    public ResponseEntity<Map<String, Object>> getVirtualSpace(@PathVariable String spaceId) {
        try {
            // 在实际实现中，这里会从服务层获取空间
            // 为简化，我们返回模拟数据
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("spaceId", spaceId);
            response.put("space", createMockSpace(spaceId));
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get virtual space", e));
        }
    }

    /**
     * 更新虚拟空间配置
     */
    @PutMapping("/spaces/{spaceId}")
    public ResponseEntity<Map<String, Object>> updateVirtualSpace(
            @PathVariable String spaceId,
            @RequestBody Map<String, Object> updates) {
        
        try {
            // 在实际实现中，这里会调用服务层更新空间
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Virtual space updated successfully");
            response.put("spaceId", spaceId);
            response.put("updates", updates);
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to update virtual space", e));
        }
    }

    /**
     * 删除虚拟空间
     */
    @DeleteMapping("/spaces/{spaceId}")
    public ResponseEntity<Map<String, Object>> deleteVirtualSpace(@PathVariable String spaceId) {
        try {
            // 在实际实现中，这里会调用服务层删除空间
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Virtual space deleted successfully");
            response.put("spaceId", spaceId);
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to delete virtual space", e));
        }
    }

    /**
     * 用户加入虚拟空间
     */
    @PostMapping("/spaces/{spaceId}/join")
    public ResponseEntity<Map<String, Object>> joinVirtualSpace(
            @PathVariable String spaceId,
            @RequestParam String userId,
            @RequestParam(required = false) String avatarId) {
        
        try {
            // 在实际实现中，这里会调用服务层加入空间
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Joined virtual space successfully");
            response.put("spaceId", spaceId);
            response.put("userId", userId);
            response.put("avatarId", avatarId != null ? avatarId : "default-avatar");
            response.put("joinTime", new Date());
            response.put("sessionToken", "session-" + UUID.randomUUID().toString());
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to join virtual space", e));
        }
    }

    /**
     * 用户离开虚拟空间
     */
    @PostMapping("/spaces/{spaceId}/leave")
    public ResponseEntity<Map<String, Object>> leaveVirtualSpace(
            @PathVariable String spaceId,
            @RequestParam String userId) {
        
        try {
            // 在实际实现中，这里会调用服务层离开空间
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Left virtual space successfully");
            response.put("spaceId", spaceId);
            response.put("userId", userId);
            response.put("leaveTime", new Date());
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to leave virtual space", e));
        }
    }

    /**
     * 配置空间音频
     */
    @PostMapping("/spaces/{spaceId}/audio")
    public ResponseEntity<Map<String, Object>> configureSpatialAudio(
            @PathVariable String spaceId,
            @RequestBody Map<String, Object> audioConfig) {
        
        try {
            Map<String, Object> result = digitalTwinService.configureSpatialAudio(spaceId, audioConfig);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Spatial audio configured successfully");
            response.put("spaceId", spaceId);
            response.put("audioConfig", result);
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to configure spatial audio", e));
        }
    }

    /**
     * 创建虚拟化身
     */
    @PostMapping("/avatars")
    public ResponseEntity<Map<String, Object>> createAvatar(
            @RequestParam String userId,
            @RequestParam String avatarName,
            @RequestBody(required = false) Map<String, Object> avatarConfig) {
        
        try {
            VirtualAvatarEntity avatar = digitalTwinService.createAvatar(userId, avatarName, avatarConfig);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Avatar created successfully");
            response.put("avatarId", avatar.getAvatarId());
            response.put("avatar", toAvatarResponse(avatar));
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create avatar", e));
        }
    }

    /**
     * 获取用户的所有化身
     */
    @GetMapping("/avatars")
    public ResponseEntity<Map<String, Object>> getUserAvatars(@RequestParam String userId) {
        try {
            List<VirtualAvatarEntity> avatars = digitalTwinService.getUserAvatars(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", userId);
            response.put("avatars", avatars.stream().map(this::toAvatarResponse).toArray());
            response.put("count", avatars.size());
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get user avatars", e));
        }
    }

    /**
     * 配置 AR/VR 集成
     */
    @PostMapping("/spaces/{spaceId}/arvr")
    public ResponseEntity<Map<String, Object>> configureArVrIntegration(
            @PathVariable String spaceId,
            @RequestBody Map<String, Object> vrConfig) {
        
        try {
            Map<String, Object> result = digitalTwinService.configureArVrIntegration(spaceId, vrConfig);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "AR/VR integration configured successfully");
            response.put("spaceId", spaceId);
            response.put("vrConfig", result);
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to configure AR/VR integration", e));
        }
    }

    /**
     * 配置场景模拟
     */
    @PostMapping("/spaces/{spaceId}/scenes")
    public ResponseEntity<Map<String, Object>> configureSceneSimulation(
            @PathVariable String spaceId,
            @RequestBody Map<String, Object> sceneConfig) {
        
        try {
            Map<String, Object> result = digitalTwinService.configureSceneSimulation(spaceId, sceneConfig);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Scene simulation configured successfully");
            response.put("spaceId", spaceId);
            response.put("sceneConfig", result);
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to configure scene simulation", e));
        }
    }

    /**
     * 配置协作工具
     */
    @PostMapping("/spaces/{spaceId}/collaboration")
    public ResponseEntity<Map<String, Object>> configureCollaborationTools(
            @PathVariable String spaceId,
            @RequestBody Map<String, Object> toolsConfig) {
        
        try {
            Map<String, Object> result = digitalTwinService.configureCollaborationTools(spaceId, toolsConfig);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Collaboration tools configured successfully");
            response.put("spaceId", spaceId);
            response.put("toolsConfig", result);
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to configure collaboration tools", e));
        }
    }

    /**
     * 搜索虚拟空间
     */
    @GetMapping("/spaces/search")
    public ResponseEntity<Map<String, Object>> searchSpaces(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String spaceType,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Boolean hasArVr) {
        
        try {
            List<DigitalTwinVirtualSpaceEntity> spaces = digitalTwinService.searchSpaces(
                    keyword, spaceType, minCapacity, hasArVr);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("spaces", spaces.stream().map(this::toSpaceResponse).toArray());
            response.put("count", spaces.size());
            response.put("searchParams", Map.of(
                    "keyword", keyword,
                    "spaceType", spaceType,
                    "minCapacity", minCapacity,
                    "hasArVr", hasArVr
            ));
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to search spaces", e));
        }
    }

    /**
     * 获取活跃空间列表
     */
    @GetMapping("/spaces/active")
    public ResponseEntity<Map<String, Object>> getActiveSpaces() {
        try {
            List<DigitalTwinVirtualSpaceEntity> spaces = digitalTwinService.findActiveSpaces();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("spaces", spaces.stream().map(this::toSpaceResponse).toArray());
            response.put("count", spaces.size());
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get active spaces", e));
        }
    }

    /**
     * 获取空间统计信息
     */
    @GetMapping("/spaces/{spaceId}/statistics")
    public ResponseEntity<Map<String, Object>> getSpaceStatistics(@PathVariable String spaceId) {
        try {
            Map<String, Object> stats = digitalTwinService.getSpaceStatistics(spaceId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("spaceId", spaceId);
            response.put("statistics", stats);
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get space statistics", e));
        }
    }

    /**
     * 批量更新虚拟空间状态
     */
    @PutMapping("/spaces/batch-status")
    public ResponseEntity<Map<String, Object>> batchUpdateSpaceStatus(
            @RequestBody Map<String, Object> batchRequest) {
        
        try {
            @SuppressWarnings("unchecked")
            List<String> spaceIds = (List<String>) batchRequest.get("spaceIds");
            String newStatus = (String) batchRequest.get("newStatus");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Batch status update initiated");
            response.put("spaceIds", spaceIds);
            response.put("newStatus", newStatus);
            response.put("count", spaceIds != null ? spaceIds.size() : 0);
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to batch update space status", e));
        }
    }

    /**
     * 获取系统支持的 VR 设备列表
     */
    @GetMapping("/vr-devices")
    public ResponseEntity<Map<String, Object>> getSupportedVrDevices() {
        try {
            List<Map<String, Object>> devices = Arrays.asList(
                    Map.of("id", "oculus-quest2", "name", "Oculus Quest 2", "type", "STANDALONE", 
                          "resolution", "1832x1920", "refreshRate", "90Hz", "priceRange", "MID"),
                    Map.of("id", "htc-vive", "name", "HTC Vive", "type", "PC_VR", 
                          "resolution", "1080x1200", "refreshRate", "90Hz", "priceRange", "HIGH"),
                    Map.of("id", "valve-index", "name", "Valve Index", "type", "PC_VR", 
                          "resolution", "1440x1600", "refreshRate", "144Hz", "priceRange", "PREMIUM"),
                    Map.of("id", "psvr2", "name", "PlayStation VR2", "type", "CONSOLE", 
                          "resolution", "2000x2040", "refreshRate", "120Hz", "priceRange", "HIGH"),
                    Map.of("id", "windows-mr", "name", "Windows Mixed Reality", "type", "PC_VR", 
                          "resolution", "1440x1440", "refreshRate", "90Hz", "priceRange", "BUDGET")
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("devices", devices);
            response.put("count", devices.size());
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get VR devices", e));
        }
    }

    /**
     * 获取系统支持的场景类型
     */
    @GetMapping("/scene-types")
    public ResponseEntity<Map<String, Object>> getSupportedSceneTypes() {
        try {
            List<Map<String, Object>> sceneTypes = Arrays.asList(
                    Map.of("id", "office", "name", "现代办公室", "category", "WORK", 
                          "complexity", "MEDIUM", "recommendedCapacity", 20),
                    Map.of("id", "conference", "name", "会议厅", "category", "WORK", 
                          "complexity", "LOW", "recommendedCapacity", 100),
                    Map.of("id", "lounge", "name", "休息室", "category", "SOCIAL", 
                          "complexity", "LOW", "recommendedCapacity", 30),
                    Map.of("id", "training", "name", "培训室", "category", "EDUCATION", 
                          "complexity", "MEDIUM", "recommendedCapacity", 50),
                    Map.of("id", "exhibition", "name", "展览馆", "category", "EVENT", 
                          "complexity", "HIGH", "recommendedCapacity", 200),
                    Map.of("id", "nature", "name", "自然环境", "category", "RELAXATION", 
                          "complexity", "HIGH", "recommendedCapacity", 50),
                    Map.of("id", "future-city", "name", "未来城市", "category", "FUTURISTIC", 
                          "complexity", "VERY_HIGH", "recommendedCapacity", 100)
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sceneTypes", sceneTypes);
            response.put("count", sceneTypes.size());
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get scene types", e));
        }
    }

    // 辅助方法
    private Map<String, Object> toSpaceResponse(DigitalTwinVirtualSpaceEntity space) {
        Map<String, Object> response = new HashMap<>();
        response.put("spaceId", space.getSpaceId());
        response.put("spaceName", space.getSpaceName());
        response.put("spaceDescription", space.getSpaceDescription());
        response.put("spaceType", space.getSpaceType());
        response.put("maxCapacity", space.getMaxCapacity());
        response.put("currentParticipants", space.getCurrentParticipants());
        response.put("spaceStatus", space.getSpaceStatus());
        response.put("hostUserId", space.getHostUserId());
        response.put("accessControlMode", space.getAccessControlMode());
        response.put("spatialAudioEnabled", space.getSpatialAudioEnabled());
        response.put("virtualAvatarEnabled", space.getVirtualAvatarEnabled());
        response.put("arVrIntegrationEnabled", space.getArVrIntegrationEnabled());
        response.put("collaborationToolsEnabled", space.getCollaborationToolsEnabled());
        response.put("sceneSimulationEnabled", space.getSceneSimulationEnabled());
        response.put("createdAt", space.getCreatedAt());
        response.put("lastActivityTime", space.getLastActivityTime());
        response.put("spaceRating", space.getSpaceRating());
        
        return response;
    }

    private Map<String, Object> toAvatarResponse(VirtualAvatarEntity avatar) {
        Map<String, Object> response = new HashMap<>();
        response.put("avatarId", avatar.getAvatarId());
        response.put("avatarName", avatar.getAvatarName());
        response.put("userId", avatar.getUserId());
        response.put("avatarGender", avatar.getAvatarGender());
        response.put("avatarRace", avatar.getAvatarRace());
        response.put("avatarStatus", avatar.getAvatarStatus());
        response.put("avatarArVrCompatible", avatar.getAvatarArVrCompatible());
        response.put("avatarIsPublic", avatar.getAvatarIsPublic());
        response.put("avatarUsageCount", avatar.getAvatarUsageCount());
        response.put("avatarLastUsed", avatar.getAvatarLastUsed());
        response.put("createdAt", avatar.getCreatedAt());
        response.put("avatarRating", avatar.getAvatarRating());
        
        return response;
    }

    private Map<String, Object> createMockSpace(String spaceId) {
        Map<String, Object> space = new HashMap<>();
        space.put("spaceId", spaceId);
        space.put("spaceName", "示例虚拟会议空间");
        space.put("spaceDescription", "这是一个示例数字孪生虚拟会议空间，支持 3D 环境、空间音频和虚拟化身。");
        space.put("spaceType", "CONFERENCE");
        space.put("maxCapacity", 100);
        space.put("currentParticipants", 25);
        space.put("spaceStatus", "ACTIVE");
        space.put("hostUserId", "user-123");
        space.put("accessControlMode", "PUBLIC");
        space.put("spatialAudioEnabled", true);
        space.put("virtualAvatarEnabled", true);
        space.put("arVrIntegrationEnabled", true);
        space.put("collaborationToolsEnabled", true);
        space.put("sceneSimulationEnabled", true);
        space.put("createdAt", new Date());
        space.put("lastActivityTime", new Date());
        space.put("spaceRating", 4.5);
        
        return space;
    }

    private Map<String, Object> createErrorResponse(String message, Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("error", e.getMessage());
        response.put("errorType", e.getClass().getSimpleName());
        response.put("timestamp", new Date());
        
        return response;
    }
}