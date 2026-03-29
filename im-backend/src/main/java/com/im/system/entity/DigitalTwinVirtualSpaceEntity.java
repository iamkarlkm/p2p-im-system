package com.im.system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 数字孪生虚拟会议空间实体
 * 支持 3D 虚拟环境、实时协作、空间音频、虚拟化身、AR/VR 集成、场景模拟
 *
 * @since 2026-03-23
 * @version 1.0.0
 */
@Entity
@Table(name = "digital_twin_virtual_space")
public class DigitalTwinVirtualSpaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "space_id", nullable = false, length = 36)
    private String spaceId;

    @Column(name = "space_name", nullable = false, length = 255)
    private String spaceName;

    @Column(name = "space_description", length = 1000)
    private String spaceDescription;

    @Column(name = "space_type", nullable = false, length = 50)
    private String spaceType; // OFFICE, CONFERENCE, LOUNGE, TRAINING, EXHIBITION, CUSTOM

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "current_participants", nullable = false)
    private Integer currentParticipants;

    @Column(name = "space_dimensions", length = 100)
    private String spaceDimensions; // "100x100x50" (width x length x height)

    @Column(name = "environment_style", length = 100)
    private String environmentStyle; // MODERN, INDUSTRIAL, NATURE, FUTURISTIC, MINIMALIST, CUSTOM

    @Column(name = "background_music_enabled")
    private Boolean backgroundMusicEnabled;

    @Column(name = "spatial_audio_enabled", nullable = false)
    private Boolean spatialAudioEnabled;

    @Column(name = "reverb_settings", length = 50)
    private String reverbSettings; // NONE, SMALL_ROOM, MEDIUM_ROOM, LARGE_HALL, CUSTOM

    @Column(name = "virtual_avatar_enabled", nullable = false)
    private Boolean virtualAvatarEnabled;

    @Column(name = "avatar_customization_level", length = 50)
    private String avatarCustomizationLevel; // NONE, BASIC, ADVANCED, FULL

    @Column(name = "ar_vr_integration_enabled")
    private Boolean arVrIntegrationEnabled;

    @Column(name = "supported_vr_devices", length = 500)
    private String supportedVrDevices; // "Oculus Quest 2,HTC Vive,Valve Index,PlayStation VR"

    @Column(name = "scene_simulation_enabled")
    private Boolean sceneSimulationEnabled;

    @Column(name = "available_scenes", length = 1000)
    private String availableScenes; // "DAY,NIGHT,RAIN,SNOW,FOG,SUNSET,NEON,FUTURE_CITY"

    @Column(name = "interactive_objects_enabled")
    private Boolean interactiveObjectsEnabled;

    @Column(name = "object_interaction_types", length = 500)
    private String objectInteractionTypes; // "PICKUP,MOVE,ROTATE,SCALE,ANIMATE,COLLABORATE"

    @Column(name = "collaboration_tools_enabled", nullable = false)
    private Boolean collaborationToolsEnabled;

    @Column(name = "available_tools", length = 1000)
    private String availableTools; // "WHITEBOARD,SCREENSHARE,3D_MODEL_VIEWER,VIDEO_PLAYER,DOCUMENT_EDITOR"

    @Column(name = "real_time_physics_enabled")
    private Boolean realTimePhysicsEnabled;

    @Column(name = "physics_engine", length = 50)
    private String physicsEngine; // "UNITY,UNREAL,CUSTOM,BASIC"

    @Column(name = "lighting_system", length = 100)
    private String lightingSystem; // "DYNAMIC,STATIC,DAY_NIGHT_CYCLE,MOOD_BASED"

    @Column(name = "weather_effects_enabled")
    private Boolean weatherEffectsEnabled;

    @Column(name = "available_weather_effects", length = 500)
    private String availableWeatherEffects; // "RAIN,SNOW,FOG,WIND,LIGHTNING"

    @Column(name = "space_status", nullable = false, length = 20)
    private String spaceStatus; // CREATED, ACTIVE, INACTIVE, MAINTENANCE, ARCHIVED

    @Column(name = "host_user_id", length = 36)
    private String hostUserId;

    @Column(name = "access_control_mode", nullable = false, length = 30)
    private String accessControlMode; // PUBLIC, PRIVATE, INVITE_ONLY, PASSWORD_PROTECTED

    @Column(name = "access_password", length = 100)
    private String accessPassword;

    @Column(name = "space_tags", length = 500)
    private String spaceTags;

    @Column(name = "space_rating")
    private Double spaceRating;

    @Column(name = "rating_count")
    private Integer ratingCount;

    @Column(name = "total_usage_minutes")
    private Long totalUsageMinutes;

    @Column(name = "last_activity_time")
    private LocalDateTime lastActivityTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "custom_config_json", columnDefinition = "TEXT")
    private String customConfigJson;

    // 构造方法
    public DigitalTwinVirtualSpaceEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.currentParticipants = 0;
        this.maxCapacity = 50;
        this.spatialAudioEnabled = true;
        this.virtualAvatarEnabled = true;
        this.collaborationToolsEnabled = true;
        this.spaceStatus = "CREATED";
        this.accessControlMode = "PUBLIC";
        this.spaceRating = 0.0;
        this.ratingCount = 0;
        this.totalUsageMinutes = 0L;
    }

    // Getters and Setters
    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getSpaceDescription() {
        return spaceDescription;
    }

    public void setSpaceDescription(String spaceDescription) {
        this.spaceDescription = spaceDescription;
    }

    public String getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public String getSpaceDimensions() {
        return spaceDimensions;
    }

    public void setSpaceDimensions(String spaceDimensions) {
        this.spaceDimensions = spaceDimensions;
    }

    public String getEnvironmentStyle() {
        return environmentStyle;
    }

    public void setEnvironmentStyle(String environmentStyle) {
        this.environmentStyle = environmentStyle;
    }

    public Boolean getBackgroundMusicEnabled() {
        return backgroundMusicEnabled;
    }

    public void setBackgroundMusicEnabled(Boolean backgroundMusicEnabled) {
        this.backgroundMusicEnabled = backgroundMusicEnabled;
    }

    public Boolean getSpatialAudioEnabled() {
        return spatialAudioEnabled;
    }

    public void setSpatialAudioEnabled(Boolean spatialAudioEnabled) {
        this.spatialAudioEnabled = spatialAudioEnabled;
    }

    public String getReverbSettings() {
        return reverbSettings;
    }

    public void setReverbSettings(String reverbSettings) {
        this.reverbSettings = reverbSettings;
    }

    public Boolean getVirtualAvatarEnabled() {
        return virtualAvatarEnabled;
    }

    public void setVirtualAvatarEnabled(Boolean virtualAvatarEnabled) {
        this.virtualAvatarEnabled = virtualAvatarEnabled;
    }

    public String getAvatarCustomizationLevel() {
        return avatarCustomizationLevel;
    }

    public void setAvatarCustomizationLevel(String avatarCustomizationLevel) {
        this.avatarCustomizationLevel = avatarCustomizationLevel;
    }

    public Boolean getArVrIntegrationEnabled() {
        return arVrIntegrationEnabled;
    }

    public void setArVrIntegrationEnabled(Boolean arVrIntegrationEnabled) {
        this.arVrIntegrationEnabled = arVrIntegrationEnabled;
    }

    public String getSupportedVrDevices() {
        return supportedVrDevices;
    }

    public void setSupportedVrDevices(String supportedVrDevices) {
        this.supportedVrDevices = supportedVrDevices;
    }

    public Boolean getSceneSimulationEnabled() {
        return sceneSimulationEnabled;
    }

    public void setSceneSimulationEnabled(Boolean sceneSimulationEnabled) {
        this.sceneSimulationEnabled = sceneSimulationEnabled;
    }

    public String getAvailableScenes() {
        return availableScenes;
    }

    public void setAvailableScenes(String availableScenes) {
        this.availableScenes = availableScenes;
    }

    public Boolean getInteractiveObjectsEnabled() {
        return interactiveObjectsEnabled;
    }

    public void setInteractiveObjectsEnabled(Boolean interactiveObjectsEnabled) {
        this.interactiveObjectsEnabled = interactiveObjectsEnabled;
    }

    public String getObjectInteractionTypes() {
        return objectInteractionTypes;
    }

    public void setObjectInteractionTypes(String objectInteractionTypes) {
        this.objectInteractionTypes = objectInteractionTypes;
    }

    public Boolean getCollaborationToolsEnabled() {
        return collaborationToolsEnabled;
    }

    public void setCollaborationToolsEnabled(Boolean collaborationToolsEnabled) {
        this.collaborationToolsEnabled = collaborationToolsEnabled;
    }

    public String getAvailableTools() {
        return availableTools;
    }

    public void setAvailableTools(String availableTools) {
        this.availableTools = availableTools;
    }

    public Boolean getRealTimePhysicsEnabled() {
        return realTimePhysicsEnabled;
    }

    public void setRealTimePhysicsEnabled(Boolean realTimePhysicsEnabled) {
        this.realTimePhysicsEnabled = realTimePhysicsEnabled;
    }

    public String getPhysicsEngine() {
        return physicsEngine;
    }

    public void setPhysicsEngine(String physicsEngine) {
        this.physicsEngine = physicsEngine;
    }

    public String getLightingSystem() {
        return lightingSystem;
    }

    public void setLightingSystem(String lightingSystem) {
        this.lightingSystem = lightingSystem;
    }

    public Boolean getWeatherEffectsEnabled() {
        return weatherEffectsEnabled;
    }

    public void setWeatherEffectsEnabled(Boolean weatherEffectsEnabled) {
        this.weatherEffectsEnabled = weatherEffectsEnabled;
    }

    public String getAvailableWeatherEffects() {
        return availableWeatherEffects;
    }

    public void setAvailableWeatherEffects(String availableWeatherEffects) {
        this.availableWeatherEffects = availableWeatherEffects;
    }

    public String getSpaceStatus() {
        return spaceStatus;
    }

    public void setSpaceStatus(String spaceStatus) {
        this.spaceStatus = spaceStatus;
    }

    public String getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(String hostUserId) {
        this.hostUserId = hostUserId;
    }

    public String getAccessControlMode() {
        return accessControlMode;
    }

    public void setAccessControlMode(String accessControlMode) {
        this.accessControlMode = accessControlMode;
    }

    public String getAccessPassword() {
        return accessPassword;
    }

    public void setAccessPassword(String accessPassword) {
        this.accessPassword = accessPassword;
    }

    public String getSpaceTags() {
        return spaceTags;
    }

    public void setSpaceTags(String spaceTags) {
        this.spaceTags = spaceTags;
    }

    public Double getSpaceRating() {
        return spaceRating;
    }

    public void setSpaceRating(Double spaceRating) {
        this.spaceRating = spaceRating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Long getTotalUsageMinutes() {
        return totalUsageMinutes;
    }

    public void setTotalUsageMinutes(Long totalUsageMinutes) {
        this.totalUsageMinutes = totalUsageMinutes;
    }

    public LocalDateTime getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(LocalDateTime lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public String getCustomConfigJson() {
        return customConfigJson;
    }

    public void setCustomConfigJson(String customConfigJson) {
        this.customConfigJson = customConfigJson;
    }

    // 实用方法
    public boolean isFull() {
        return currentParticipants >= maxCapacity;
    }

    public boolean canJoin() {
        return !isFull() && !"INACTIVE".equals(spaceStatus) && !"MAINTENANCE".equals(spaceStatus);
    }

    public void incrementParticipants() {
        if (currentParticipants < maxCapacity) {
            currentParticipants++;
            this.lastActivityTime = LocalDateTime.now();
        }
    }

    public void decrementParticipants() {
        if (currentParticipants > 0) {
            currentParticipants--;
            this.lastActivityTime = LocalDateTime.now();
        }
    }

    public void addUsageMinutes(long minutes) {
        if (totalUsageMinutes == null) {
            totalUsageMinutes = 0L;
        }
        totalUsageMinutes += minutes;
    }

    public void updateRating(double newRating) {
        if (ratingCount == null) ratingCount = 0;
        if (spaceRating == null) spaceRating = 0.0;
        
        double totalRating = spaceRating * ratingCount + newRating;
        ratingCount++;
        spaceRating = totalRating / ratingCount;
    }

    public List<String> getAvailableScenesList() {
        if (availableScenes == null || availableScenes.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(availableScenes.split(","));
    }

    public List<String> getAvailableToolsList() {
        if (availableTools == null || availableTools.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(availableTools.split(","));
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "DigitalTwinVirtualSpaceEntity{" +
                "spaceId='" + spaceId + '\'' +
                ", spaceName='" + spaceName + '\'' +
                ", spaceType='" + spaceType + '\'' +
                ", currentParticipants=" + currentParticipants +
                ", maxCapacity=" + maxCapacity +
                ", spaceStatus='" + spaceStatus + '\'' +
                '}';
    }
}