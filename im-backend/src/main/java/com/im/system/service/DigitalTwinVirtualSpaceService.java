package com.im.system.service;

import com.im.system.entity.DigitalTwinVirtualSpaceEntity;
import com.im.system.entity.VirtualAvatarEntity;
import com.im.system.repository.DigitalTwinVirtualSpaceRepository;
import com.im.system.repository.VirtualAvatarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数字孪生虚拟会议空间服务
 * 支持虚拟空间管理、用户加入/离开、空间音频处理、虚拟化身管理、AR/VR 集成、场景模拟
 *
 * @since 2026-03-23
 * @version 1.0.0
 */
@Service
public class DigitalTwinVirtualSpaceService {

    @Autowired
    private DigitalTwinVirtualSpaceRepository spaceRepository;

    @Autowired
    private VirtualAvatarRepository avatarRepository;

    // 空间管理
    @Transactional
    public DigitalTwinVirtualSpaceEntity createVirtualSpace(String spaceName, String spaceType, 
                                                           String hostUserId, Map<String, Object> config) {
        DigitalTwinVirtualSpaceEntity space = new DigitalTwinVirtualSpaceEntity();
        space.setSpaceName(spaceName);
        space.setSpaceType(spaceType != null ? spaceType : "CONFERENCE");
        space.setHostUserId(hostUserId);
        space.setSpaceStatus("CREATED");
        
        // 应用配置
        applyConfigToSpace(space, config);
        
        // 设置默认值
        if (space.getMaxCapacity() == null) {
            space.setMaxCapacity(50);
        }
        if (space.getSpatialAudioEnabled() == null) {
            space.setSpatialAudioEnabled(true);
        }
        if (space.getVirtualAvatarEnabled() == null) {
            space.setVirtualAvatarEnabled(true);
        }
        if (space.getCollaborationToolsEnabled() == null) {
            space.setCollaborationToolsEnabled(true);
        }
        
        return spaceRepository.save(space);
    }

    @Transactional
    public DigitalTwinVirtualSpaceEntity updateVirtualSpace(String spaceId, Map<String, Object> updates) {
        DigitalTwinVirtualSpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Virtual space not found: " + spaceId));
        
        // 应用更新
        applyUpdatesToSpace(space, updates);
        space.setUpdatedAt(LocalDateTime.now());
        
        return spaceRepository.save(space);
    }

    @Transactional
    public void deleteVirtualSpace(String spaceId) {
        DigitalTwinVirtualSpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Virtual space not found: " + spaceId));
        
        space.setSpaceStatus("ARCHIVED");
        space.setUpdatedAt(LocalDateTime.now());
        spaceRepository.save(space);
    }

    // 用户加入/离开空间
    @Transactional
    public DigitalTwinVirtualSpaceEntity joinVirtualSpace(String spaceId, String userId, String avatarId) {
        DigitalTwinVirtualSpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Virtual space not found: " + spaceId));
        
        // 检查空间状态
        if (!space.canJoin()) {
            throw new RuntimeException("Cannot join space. Status: " + space.getSpaceStatus() + 
                                     ", Current participants: " + space.getCurrentParticipants() + 
                                     ", Max capacity: " + space.getMaxCapacity());
        }
        
        // 检查密码（如果设置了）
        if ("PASSWORD_PROTECTED".equals(space.getAccessControlMode())) {
            // 在实际实现中，这里会验证密码
        }
        
        // 获取或创建虚拟化身
        VirtualAvatarEntity avatar = null;
        if (avatarId != null) {
            avatar = avatarRepository.findById(avatarId)
                    .orElseThrow(() -> new RuntimeException("Avatar not found: " + avatarId));
            
            // 验证化身是否属于用户
            if (!userId.equals(avatar.getUserId())) {
                throw new RuntimeException("Avatar does not belong to user");
            }
        } else {
            // 为用户创建默认化身
            avatar = createDefaultAvatar(userId);
        }
        
        // 增加参与者计数
        space.incrementParticipants();
        space.setLastActivityTime(LocalDateTime.now());
        
        // 更新化身使用计数
        avatar.incrementUsageCount();
        avatarRepository.save(avatar);
        
        // 更新空间状态
        if ("CREATED".equals(space.getSpaceStatus())) {
            space.setSpaceStatus("ACTIVE");
        }
        
        return spaceRepository.save(space);
    }

    @Transactional
    public DigitalTwinVirtualSpaceEntity leaveVirtualSpace(String spaceId, String userId) {
        DigitalTwinVirtualSpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Virtual space not found: " + spaceId));
        
        // 减少参与者计数
        space.decrementParticipants();
        space.setLastActivityTime(LocalDateTime.now());
        
        // 如果空间为空，标记为不活跃
        if (space.getCurrentParticipants() <= 0) {
            space.setSpaceStatus("INACTIVE");
        }
        
        return spaceRepository.save(space);
    }

    // 空间音频处理
    public Map<String, Object> configureSpatialAudio(String spaceId, Map<String, Object> audioConfig) {
        DigitalTwinVirtualSpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Virtual space not found: " + spaceId));
        
        // 配置空间音频
        space.setSpatialAudioEnabled(true);
        
        if (audioConfig.containsKey("reverbSettings")) {
            space.setReverbSettings(audioConfig.get("reverbSettings").toString());
        }
        
        if (audioConfig.containsKey("backgroundMusicEnabled")) {
            space.setBackgroundMusicEnabled(Boolean.parseBoolean(audioConfig.get("backgroundMusicEnabled").toString()));
        }
        
        spaceRepository.save(space);
        
        Map<String, Object> result = new HashMap<>();
        result.put("spaceId", spaceId);
        result.put("spatialAudioEnabled", space.getSpatialAudioEnabled());
        result.put("reverbSettings", space.getReverbSettings());
        result.put("backgroundMusicEnabled", space.getBackgroundMusicEnabled());
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    // 虚拟化身管理
    @Transactional
    public VirtualAvatarEntity createAvatar(String userId, String avatarName, Map<String, Object> avatarConfig) {
        VirtualAvatarEntity avatar = new VirtualAvatarEntity();
        avatar.setUserId(userId);
        avatar.setAvatarName(avatarName);
        avatar.setAvatarStatus("ACTIVE");
        
        // 应用配置
        applyConfigToAvatar(avatar, avatarConfig);
        
        return avatarRepository.save(avatar);
    }

    @Transactional
    public VirtualAvatarEntity updateAvatar(String avatarId, Map<String, Object> updates) {
        VirtualAvatarEntity avatar = avatarRepository.findById(avatarId)
                .orElseThrow(() -> new RuntimeException("Avatar not found: " + avatarId));
        
        // 应用更新
        applyUpdatesToAvatar(avatar, updates);
        avatar.setUpdatedAt(LocalDateTime.now());
        
        return avatarRepository.save(avatar);
    }

    public List<VirtualAvatarEntity> getUserAvatars(String userId) {
        return avatarRepository.findByUserId(userId);
    }

    // AR/VR 集成
    public Map<String, Object> configureArVrIntegration(String spaceId, Map<String, Object> vrConfig) {
        DigitalTwinVirtualSpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Virtual space not found: " + spaceId));
        
        space.setArVrIntegrationEnabled(true);
        
        if (vrConfig.containsKey("supportedVrDevices")) {
            space.setSupportedVrDevices(vrConfig.get("supportedVrDevices").toString());
        }
        
        if (vrConfig.containsKey("physicsEngine")) {
            space.setPhysicsEngine(vrConfig.get("physicsEngine").toString());
        }
        
        if (vrConfig.containsKey("realTimePhysicsEnabled")) {
            space.setRealTimePhysicsEnabled(Boolean.parseBoolean(vrConfig.get("realTimePhysicsEnabled").toString()));
        }
        
        spaceRepository.save(space);
        
        Map<String, Object> result = new HashMap<>();
        result.put("spaceId", spaceId);
        result.put("arVrIntegrationEnabled", space.getArVrIntegrationEnabled());
        result.put("supportedVrDevices", space.getSupportedVrDevices());
        result.put("physicsEngine", space.getPhysicsEngine());
        result.put("realTimePhysicsEnabled", space.getRealTimePhysicsEnabled());
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    // 场景模拟
    public Map<String, Object> configureSceneSimulation(String spaceId, Map<String, Object> sceneConfig) {
        DigitalTwinVirtualSpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Virtual space not found: " + spaceId));
        
        space.setSceneSimulationEnabled(true);
        
        if (sceneConfig.containsKey("availableScenes")) {
            space.setAvailableScenes(sceneConfig.get("availableScenes").toString());
        }
        
        if (sceneConfig.containsKey("lightingSystem")) {
            space.setLightingSystem(sceneConfig.get("lightingSystem").toString());
        }
        
        if (sceneConfig.containsKey("weatherEffectsEnabled")) {
            space.setWeatherEffectsEnabled(Boolean.parseBoolean(sceneConfig.get("weatherEffectsEnabled").toString()));
        }
        
        if (sceneConfig.containsKey("availableWeatherEffects")) {
            space.setAvailableWeatherEffects(sceneConfig.get("availableWeatherEffects").toString());
        }
        
        spaceRepository.save(space);
        
        Map<String, Object> result = new HashMap<>();
        result.put("spaceId", spaceId);
        result.put("sceneSimulationEnabled", space.getSceneSimulationEnabled());
        result.put("availableScenes", space.getAvailableScenes());
        result.put("lightingSystem", space.getLightingSystem());
        result.put("weatherEffectsEnabled", space.getWeatherEffectsEnabled());
        result.put("availableWeatherEffects", space.getAvailableWeatherEffects());
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    // 协作工具
    public Map<String, Object> configureCollaborationTools(String spaceId, Map<String, Object> toolsConfig) {
        DigitalTwinVirtualSpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Virtual space not found: " + spaceId));
        
        space.setCollaborationToolsEnabled(true);
        
        if (toolsConfig.containsKey("availableTools")) {
            space.setAvailableTools(toolsConfig.get("availableTools").toString());
        }
        
        if (toolsConfig.containsKey("interactiveObjectsEnabled")) {
            space.setInteractiveObjectsEnabled(Boolean.parseBoolean(toolsConfig.get("interactiveObjectsEnabled").toString()));
        }
        
        if (toolsConfig.containsKey("objectInteractionTypes")) {
            space.setObjectInteractionTypes(toolsConfig.get("objectInteractionTypes").toString());
        }
        
        spaceRepository.save(space);
        
        Map<String, Object> result = new HashMap<>();
        result.put("spaceId", spaceId);
        result.put("collaborationToolsEnabled", space.getCollaborationToolsEnabled());
        result.put("availableTools", space.getAvailableTools());
        result.put("interactiveObjectsEnabled", space.getInteractiveObjectsEnabled());
        result.put("objectInteractionTypes", space.getObjectInteractionTypes());
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    // 查询方法
    public List<DigitalTwinVirtualSpaceEntity> findActiveSpaces() {
        return spaceRepository.findBySpaceStatus("ACTIVE");
    }

    public List<DigitalTwinVirtualSpaceEntity> findSpacesByType(String spaceType) {
        return spaceRepository.findBySpaceType(spaceType);
    }

    public List<DigitalTwinVirtualSpaceEntity> findSpacesByHost(String hostUserId) {
        return spaceRepository.findByHostUserId(hostUserId);
    }

    public List<DigitalTwinVirtualSpaceEntity> searchSpaces(String keyword, String spaceType, 
                                                          Integer minCapacity, Boolean hasArVr) {
        // 简单实现 - 在实际应用中会使用更复杂的查询
        List<DigitalTwinVirtualSpaceEntity> allSpaces = spaceRepository.findAll();
        
        return allSpaces.stream()
                .filter(space -> {
                    boolean matches = true;
                    
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        matches = space.getSpaceName().toLowerCase().contains(keyword.toLowerCase()) ||
                                 (space.getSpaceDescription() != null && 
                                  space.getSpaceDescription().toLowerCase().contains(keyword.toLowerCase()));
                    }
                    
                    if (spaceType != null && !spaceType.trim().isEmpty()) {
                        matches = matches && spaceType.equals(space.getSpaceType());
                    }
                    
                    if (minCapacity != null) {
                        matches = matches && space.getMaxCapacity() >= minCapacity;
                    }
                    
                    if (hasArVr != null) {
                        matches = matches && 
                                 (hasArVr ? Boolean.TRUE.equals(space.getArVrIntegrationEnabled()) : 
                                            Boolean.FALSE.equals(space.getArVrIntegrationEnabled()));
                    }
                    
                    return matches;
                })
                .collect(Collectors.toList());
    }

    // 统计方法
    public Map<String, Object> getSpaceStatistics(String spaceId) {
        DigitalTwinVirtualSpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Virtual space not found: " + spaceId));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("spaceId", spaceId);
        stats.put("spaceName", space.getSpaceName());
        stats.put("currentParticipants", space.getCurrentParticipants());
        stats.put("maxCapacity", space.getMaxCapacity());
        stats.put("totalUsageMinutes", space.getTotalUsageMinutes());
        stats.put("spaceRating", space.getSpaceRating());
        stats.put("ratingCount", space.getRatingCount());
        stats.put("lastActivityTime", space.getLastActivityTime());
        stats.put("createdAt", space.getCreatedAt());
        stats.put("spaceStatus", space.getSpaceStatus());
        stats.put("accessControlMode", space.getAccessControlMode());
        
        // 计算使用率
        double usageRate = 0.0;
        if (space.getMaxCapacity() > 0) {
            usageRate = (double) space.getCurrentParticipants() / space.getMaxCapacity() * 100;
        }
        stats.put("usageRatePercentage", Math.round(usageRate * 100.0) / 100.0);
        
        return stats;
    }

    // 私有辅助方法
    private void applyConfigToSpace(DigitalTwinVirtualSpaceEntity space, Map<String, Object> config) {
        if (config == null) return;
        
        config.forEach((key, value) -> {
            switch (key) {
                case "spaceDescription":
                    space.setSpaceDescription(value.toString());
                    break;
                case "maxCapacity":
                    space.setMaxCapacity(Integer.parseInt(value.toString()));
                    break;
                case "spaceDimensions":
                    space.setSpaceDimensions(value.toString());
                    break;
                case "environmentStyle":
                    space.setEnvironmentStyle(value.toString());
                    break;
                case "backgroundMusicEnabled":
                    space.setBackgroundMusicEnabled(Boolean.parseBoolean(value.toString()));
                    break;
                case "spatialAudioEnabled":
                    space.setSpatialAudioEnabled(Boolean.parseBoolean(value.toString()));
                    break;
                case "reverbSettings":
                    space.setReverbSettings(value.toString());
                    break;
                case "virtualAvatarEnabled":
                    space.setVirtualAvatarEnabled(Boolean.parseBoolean(value.toString()));
                    break;
                case "avatarCustomizationLevel":
                    space.setAvatarCustomizationLevel(value.toString());
                    break;
                case "arVrIntegrationEnabled":
                    space.setArVrIntegrationEnabled(Boolean.parseBoolean(value.toString()));
                    break;
                case "supportedVrDevices":
                    space.setSupportedVrDevices(value.toString());
                    break;
                case "sceneSimulationEnabled":
                    space.setSceneSimulationEnabled(Boolean.parseBoolean(value.toString()));
                    break;
                case "availableScenes":
                    space.setAvailableScenes(value.toString());
                    break;
                case "interactiveObjectsEnabled":
                    space.setInteractiveObjectsEnabled(Boolean.parseBoolean(value.toString()));
                    break;
                case "objectInteractionTypes":
                    space.setObjectInteractionTypes(value.toString());
                    break;
                case "collaborationToolsEnabled":
                    space.setCollaborationToolsEnabled(Boolean.parseBoolean(value.toString()));
                    break;
                case "availableTools":
                    space.setAvailableTools(value.toString());
                    break;
                case "realTimePhysicsEnabled":
                    space.setRealTimePhysicsEnabled(Boolean.parseBoolean(value.toString()));
                    break;
                case "physicsEngine":
                    space.setPhysicsEngine(value.toString());
                    break;
                case "lightingSystem":
                    space.setLightingSystem(value.toString());
                    break;
                case "weatherEffectsEnabled":
                    space.setWeatherEffectsEnabled(Boolean.parseBoolean(value.toString()));
                    break;
                case "availableWeatherEffects":
                    space.setAvailableWeatherEffects(value.toString());
                    break;
                case "accessControlMode":
                    space.setAccessControlMode(value.toString());
                    break;
                case "accessPassword":
                    space.setAccessPassword(value.toString());
                    break;
                case "spaceTags":
                    space.setSpaceTags(value.toString());
                    break;
                case "customConfigJson":
                    space.setCustomConfigJson(value.toString());
                    break;
            }
        });
    }

    private void applyUpdatesToSpace(DigitalTwinVirtualSpaceEntity space, Map<String, Object> updates) {
        applyConfigToSpace(space, updates);
    }

    private void applyConfigToAvatar(VirtualAvatarEntity avatar, Map<String, Object> config) {
        if (config == null) return;
        
        config.forEach((key, value) -> {
            switch (key) {
                case "avatarGender":
                    avatar.setAvatarGender(value.toString());
                    break;
                case "avatarRace":
                    avatar.setAvatarRace(value.toString());
                    break;
                case "avatarSkinTone":
                    avatar.setAvatarSkinTone(value.toString());
                    break;
                case "avatarHairStyle":
                    avatar.setAvatarHairStyle(value.toString());
                    break;
                case "avatarHairColor":
                    avatar.setAvatarHairColor(value.toString());
                    break;
                case "avatarEyeColor":
                    avatar.setAvatarEyeColor(value.toString());
                    break;
                case "avatarHeight":
                    avatar.setAvatarHeight(Double.parseDouble(value.toString()));
                    break;
                case "avatarWeight":
                    avatar.setAvatarWeight(Double.parseDouble(value.toString()));
                    break;
                case "avatarBodyType":
                    avatar.setAvatarBodyType(value.toString());
                    break;
                case "avatarClothingStyle":
                    avatar.setAvatarClothingStyle(value.toString());
                    break;
                case "avatarPrimaryColor":
                    avatar.setAvatarPrimaryColor(value.toString());
                    break;
                case "avatarSecondaryColor":
                    avatar.setAvatarSecondaryColor(value.toString());
                    break;
                case "avatarAccessories":
                    avatar.setAvatarAccessories(value.toString());
                    break;
                case "avatarFacialFeatures":
                    avatar.setAvatarFacialFeatures(value.toString());
                    break;
                case "avatarAnimationSet":
                    avatar.setAvatarAnimationSet(value.toString());
                    break;
                case "avatarExpressionSet":
                    avatar.setAvatarExpressionSet(value.toString());
                    break;
                case "avatarVoiceType":
                    avatar.setAvatarVoiceType(value.toString());
                    break;
                case "avatarVoicePitch":
                    avatar.setAvatarVoicePitch(Double.parseDouble(value.toString()));
                    break;
                case "avatarVoiceSpeed":
                    avatar.setAvatarVoiceSpeed(Double.parseDouble(value.toString()));
                    break;
                case "avatarGestureSet":
                    avatar.setAvatarGestureSet(value.toString());
                    break;
                case "avatarArVrCompatible":
                    avatar.setAvatarArVrCompatible(Boolean.parseBoolean(value.toString()));
                    break;
                case "supportedVrPlatforms":
                    avatar.setSupportedVrPlatforms(value.toString());
                    break;
                case "avatarIsPublic":
                    avatar.setAvatarIsPublic(Boolean.parseBoolean(value.toString()));
                    break;
                case "avatarLightingQuality":
                    avatar.setAvatarLightingQuality(value.toString());
                    break;
                case "customizationDataJson":
                    avatar.setCustomizationDataJson(value.toString());
                    break;
            }
        });
    }

    private void applyUpdatesToAvatar(VirtualAvatarEntity avatar, Map<String, Object> updates) {
        applyConfigToAvatar(avatar, updates);
    }

    private VirtualAvatarEntity createDefaultAvatar(String userId) {
        VirtualAvatarEntity avatar = new VirtualAvatarEntity();
        avatar.setUserId(userId);
        avatar.setAvatarName("Default Avatar");
        avatar.setAvatarGender("NON_BINARY");
        avatar.setAvatarRace("HUMAN");
        avatar.setAvatarSkinTone("MEDIUM");
        avatar.setAvatarHairStyle("SHORT");
        avatar.setAvatarHairColor("#000000");
        avatar.setAvatarEyeColor("#000000");
        avatar.setAvatarHeight(1.75);
        avatar.setAvatarWeight(70.0);
        avatar.setAvatarBodyType("AVERAGE");
        avatar.setAvatarClothingStyle("CASUAL");
        avatar.setAvatarPrimaryColor("#3498db");
        avatar.setAvatarSecondaryColor("#2c3e50");
        avatar.setAvatarAccessories("GLASSES,WATCH");
        avatar.setAvatarAnimationSet("WALK,SIT,WAVE,IDLE");
        avatar.setAvatarExpressionSet("HAPPY,NEUTRAL");
        avatar.setAvatarVoiceType("NEUTRAL");
        avatar.setAvatarVoicePitch(1.0);
        avatar.setAvatarVoiceSpeed(1.0);
        avatar.setAvatarArVrCompatible(true);
        avatar.setAvatarIsPublic(false);
        avatar.setAvatarStatus("ACTIVE");
        
        return avatarRepository.save(avatar);
    }
}