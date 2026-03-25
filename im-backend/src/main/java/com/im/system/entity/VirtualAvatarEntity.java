package com.im.system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 虚拟化身实体
 * 支持用户虚拟化身创建、自定义、动作、表情、装备、AR/VR 集成
 *
 * @since 2026-03-23
 * @version 1.0.0
 */
@Entity
@Table(name = "virtual_avatar")
public class VirtualAvatarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "avatar_id", nullable = false, length = 36)
    private String avatarId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "avatar_name", nullable = false, length = 255)
    private String avatarName;

    @Column(name = "avatar_gender", length = 20)
    private String avatarGender; // MALE, FEMALE, NON_BINARY, CUSTOM

    @Column(name = "avatar_race", length = 50)
    private String avatarRace; // HUMAN, ROBOT, ANIMAL, FANTASY, ALIEN, CUSTOM

    @Column(name = "avatar_skin_tone", length = 50)
    private String avatarSkinTone; // FAIR, LIGHT, MEDIUM, DARK, DARK_BROWN, CUSTOM

    @Column(name = "avatar_hair_style", length = 100)
    private String avatarHairStyle;

    @Column(name = "avatar_hair_color", length = 50)
    private String avatarHairColor;

    @Column(name = "avatar_eye_color", length = 50)
    private String avatarEyeColor;

    @Column(name = "avatar_height")
    private Double avatarHeight; // 单位：米

    @Column(name = "avatar_weight")
    private Double avatarWeight; // 单位：千克

    @Column(name = "avatar_body_type", length = 50)
    private String avatarBodyType; // SLIM, ATHLETIC, AVERAGE, MUSCULAR, PLUS_SIZE, CUSTOM

    @Column(name = "avatar_clothing_style", length = 100)
    private String avatarClothingStyle; // CASUAL, FORMAL, BUSINESS, SPORTS, FANTASY, SCI_FI

    @Column(name = "avatar_primary_color", length = 20)
    private String avatarPrimaryColor; // HEX 颜色代码

    @Column(name = "avatar_secondary_color", length = 20)
    private String avatarSecondaryColor;

    @Column(name = "avatar_accessories", length = 500)
    private String avatarAccessories; // "GLASSES,HAT,BACKPACK,WATCH,SCARF"

    @Column(name = "avatar_facial_features", length = 500)
    private String avatarFacialFeatures; // "BEARD,MOUSTACHE,FREKLES,SCAR,TATTOO"

    @Column(name = "avatar_animation_set", length = 1000)
    private String avatarAnimationSet; // "WALK,RUN,JUMP,WAVE,DANCE,SIT,IDLE"

    @Column(name = "avatar_expression_set", length = 500)
    private String avatarExpressionSet; // "HAPPY,SAD,ANGRY,SURPRISED,NEUTRAL,WINK"

    @Column(name = "avatar_voice_type", length = 50)
    private String avatarVoiceType; // MASCULINE, FEMININE, NEUTRAL, ROBOTIC, CUSTOM

    @Column(name = "avatar_voice_pitch")
    private Double avatarVoicePitch; // 0.5 到 2.0

    @Column(name = "avatar_voice_speed")
    private Double avatarVoiceSpeed; // 0.5 到 2.0

    @Column(name = "avatar_gesture_set", length = 500)
    private String avatarGestureSet; // "POINT,THUMBS_UP,CLAP,SHAKE_HEAD,NOD"

    @Column(name = "avatar_equipment_slots", length = 1000)
    private String avatarEquipmentSlots; // "HEAD,CHEST,HANDS,LEGS,FEET,BACK"

    @Column(name = "avatar_equipped_items", length = 2000)
    private String avatarEquippedItems; // JSON 格式的装备项

    @Column(name = "avatar_ar_vr_compatible", nullable = false)
    private Boolean avatarArVrCompatible;

    @Column(name = "supported_vr_platforms", length = 500)
    private String supportedVrPlatforms; // "OCULUS,VIVE,INDEX,PSVR,WINDOWS_MR"

    @Column(name = "avatar_rig_type", length = 50)
    private String avatarRigType; // HUMAN, BIPED, QUADRUPED, CUSTOM

    @Column(name = "avatar_bone_count")
    private Integer avatarBoneCount;

    @Column(name = "avatar_texture_resolution", length = 50)
    private String avatarTextureResolution; // "512x512", "1024x1024", "2048x2048", "4096x4096"

    @Column(name = "avatar_polygon_count")
    private Integer avatarPolygonCount;

    @Column(name = "avatar_lod_levels")
    private Integer avatarLodLevels; // LOD 级别数量

    @Column(name = "avatar_physics_enabled")
    private Boolean avatarPhysicsEnabled;

    @Column(name = "avatar_cloth_simulation")
    private Boolean avatarClothSimulation;

    @Column(name = "avatar_hair_simulation")
    private Boolean avatarHairSimulation;

    @Column(name = "avatar_collision_enabled")
    private Boolean avatarCollisionEnabled;

    @Column(name = "avatar_custom_scripts", columnDefinition = "TEXT")
    private String avatarCustomScripts;

    @Column(name = "avatar_ai_behavior_profile", length = 100)
    private String avatarAiBehaviorProfile; // "FRIENDLY,PROFESSIONAL,PLAYFUL,SERIOUS,ENERGETIC"

    @Column(name = "avatar_visibility_range")
    private Double avatarVisibilityRange; // 单位：米

    @Column(name = "avatar_shadow_enabled")
    private Boolean avatarShadowEnabled;

    @Column(name = "avatar_reflection_enabled")
    private Boolean avatarReflectionEnabled;

    @Column(name = "avatar_lighting_quality", length = 50)
    private String avatarLightingQuality; // "LOW", "MEDIUM", "HIGH", "ULTRA"

    @Column(name = "avatar_status", nullable = false, length = 20)
    private String avatarStatus; // ACTIVE, INACTIVE, ARCHIVED, TEMPLATE

    @Column(name = "avatar_is_public")
    private Boolean avatarIsPublic;

    @Column(name = "avatar_usage_count")
    private Integer avatarUsageCount;

    @Column(name = "avatar_last_used")
    private LocalDateTime avatarLastUsed;

    @Column(name = "avatar_creation_source", length = 50)
    private String avatarCreationSource; // "USER_CREATED", "AI_GENERATED", "TEMPLATE", "IMPORTED"

    @Column(name = "avatar_rating")
    private Double avatarRating;

    @Column(name = "avatar_rating_count")
    private Integer avatarRatingCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "customization_data_json", columnDefinition = "TEXT")
    private String customizationDataJson;

    // 构造方法
    public VirtualAvatarEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.avatarStatus = "ACTIVE";
        this.avatarArVrCompatible = true;
        this.avatarIsPublic = false;
        this.avatarUsageCount = 0;
        this.avatarRating = 0.0;
        this.avatarRatingCount = 0;
        this.avatarHeight = 1.75;
        this.avatarWeight = 70.0;
        this.avatarVoicePitch = 1.0;
        this.avatarVoiceSpeed = 1.0;
        this.avatarPhysicsEnabled = true;
        this.avatarCollisionEnabled = true;
        this.avatarShadowEnabled = true;
        this.avatarLightingQuality = "MEDIUM";
        this.avatarVisibilityRange = 100.0;
    }

    // Getters and Setters
    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public String getAvatarGender() {
        return avatarGender;
    }

    public void setAvatarGender(String avatarGender) {
        this.avatarGender = avatarGender;
    }

    public String getAvatarRace() {
        return avatarRace;
    }

    public void setAvatarRace(String avatarRace) {
        this.avatarRace = avatarRace;
    }

    public String getAvatarSkinTone() {
        return avatarSkinTone;
    }

    public void setAvatarSkinTone(String avatarSkinTone) {
        this.avatarSkinTone = avatarSkinTone;
    }

    public String getAvatarHairStyle() {
        return avatarHairStyle;
    }

    public void setAvatarHairStyle(String avatarHairStyle) {
        this.avatarHairStyle = avatarHairStyle;
    }

    public String getAvatarHairColor() {
        return avatarHairColor;
    }

    public void setAvatarHairColor(String avatarHairColor) {
        this.avatarHairColor = avatarHairColor;
    }

    public String getAvatarEyeColor() {
        return avatarEyeColor;
    }

    public void setAvatarEyeColor(String avatarEyeColor) {
        this.avatarEyeColor = avatarEyeColor;
    }

    public Double getAvatarHeight() {
        return avatarHeight;
    }

    public void setAvatarHeight(Double avatarHeight) {
        this.avatarHeight = avatarHeight;
    }

    public Double getAvatarWeight() {
        return avatarWeight;
    }

    public void setAvatarWeight(Double avatarWeight) {
        this.avatarWeight = avatarWeight;
    }

    public String getAvatarBodyType() {
        return avatarBodyType;
    }

    public void setAvatarBodyType(String avatarBodyType) {
        this.avatarBodyType = avatarBodyType;
    }

    public String getAvatarClothingStyle() {
        return avatarClothingStyle;
    }

    public void setAvatarClothingStyle(String avatarClothingStyle) {
        this.avatarClothingStyle = avatarClothingStyle;
    }

    public String getAvatarPrimaryColor() {
        return avatarPrimaryColor;
    }

    public void setAvatarPrimaryColor(String avatarPrimaryColor) {
        this.avatarPrimaryColor = avatarPrimaryColor;
    }

    public String getAvatarSecondaryColor() {
        return avatarSecondaryColor;
    }

    public void setAvatarSecondaryColor(String avatarSecondaryColor) {
        this.avatarSecondaryColor = avatarSecondaryColor;
    }

    public String getAvatarAccessories() {
        return avatarAccessories;
    }

    public void setAvatarAccessories(String avatarAccessories) {
        this.avatarAccessories = avatarAccessories;
    }

    public String getAvatarFacialFeatures() {
        return avatarFacialFeatures;
    }

    public void setAvatarFacialFeatures(String avatarFacialFeatures) {
        this.avatarFacialFeatures = avatarFacialFeatures;
    }

    public String getAvatarAnimationSet() {
        return avatarAnimationSet;
    }

    public void setAvatarAnimationSet(String avatarAnimationSet) {
        this.avatarAnimationSet = avatarAnimationSet;
    }

    public String getAvatarExpressionSet() {
        return avatarExpressionSet;
    }

    public void setAvatarExpressionSet(String avatarExpressionSet) {
        this.avatarExpressionSet = avatarExpressionSet;
    }

    public String getAvatarVoiceType() {
        return avatarVoiceType;
    }

    public void setAvatarVoiceType(String avatarVoiceType) {
        this.avatarVoiceType = avatarVoiceType;
    }

    public Double getAvatarVoicePitch() {
        return avatarVoicePitch;
    }

    public void setAvatarVoicePitch(Double avatarVoicePitch) {
        this.avatarVoicePitch = avatarVoicePitch;
    }

    public Double getAvatarVoiceSpeed() {
        return avatarVoiceSpeed;
    }

    public void setAvatarVoiceSpeed(Double avatarVoiceSpeed) {
        this.avatarVoiceSpeed = avatarVoiceSpeed;
    }

    public String getAvatarGestureSet() {
        return avatarGestureSet;
    }

    public void setAvatarGestureSet(String avatarGestureSet) {
        this.avatarGestureSet = avatarGestureSet;
    }

    public String getAvatarEquipmentSlots() {
        return avatarEquipmentSlots;
    }

    public void setAvatarEquipmentSlots(String avatarEquipmentSlots) {
        this.avatarEquipmentSlots = avatarEquipmentSlots;
    }

    public String getAvatarEquippedItems() {
        return avatarEquippedItems;
    }

    public void setAvatarEquippedItems(String avatarEquippedItems) {
        this.avatarEquippedItems = avatarEquippedItems;
    }

    public Boolean getAvatarArVrCompatible() {
        return avatarArVrCompatible;
    }

    public void setAvatarArVrCompatible(Boolean avatarArVrCompatible) {
        this.avatarArVrCompatible = avatarArVrCompatible;
    }

    public String getSupportedVrPlatforms() {
        return supportedVrPlatforms;
    }

    public void setSupportedVrPlatforms(String supportedVrPlatforms) {
        this.supportedVrPlatforms = supportedVrPlatforms;
    }

    public String getAvatarRigType() {
        return avatarRigType;
    }

    public void setAvatarRigType(String avatarRigType) {
        this.avatarRigType = avatarRigType;
    }

    public Integer getAvatarBoneCount() {
        return avatarBoneCount;
    }

    public void setAvatarBoneCount(Integer avatarBoneCount) {
        this.avatarBoneCount = avatarBoneCount;
    }

    public String getAvatarTextureResolution() {
        return avatarTextureResolution;
    }

    public void setAvatarTextureResolution(String avatarTextureResolution) {
        this.avatarTextureResolution = avatarTextureResolution;
    }

    public Integer getAvatarPolygonCount() {
        return avatarPolygonCount;
    }

    public void setAvatarPolygonCount(Integer avatarPolygonCount) {
        this.avatarPolygonCount = avatarPolygonCount;
    }

    public Integer getAvatarLodLevels() {
        return avatarLodLevels;
    }

    public void setAvatarLodLevels(Integer avatarLodLevels) {
        this.avatarLodLevels = avatarLodLevels;
    }

    public Boolean getAvatarPhysicsEnabled() {
        return avatarPhysicsEnabled;
    }

    public void setAvatarPhysicsEnabled(Boolean avatarPhysicsEnabled) {
        this.avatarPhysicsEnabled = avatarPhysicsEnabled;
    }

    public Boolean getAvatarClothSimulation() {
        return avatarClothSimulation;
    }

    public void setAvatarClothSimulation(Boolean avatarClothSimulation) {
        this.avatarClothSimulation = avatarClothSimulation;
    }

    public Boolean getAvatarHairSimulation() {
        return avatarHairSimulation;
    }

    public void setAvatarHairSimulation(Boolean avatarHairSimulation) {
        this.avatarHairSimulation = avatarHairSimulation;
    }

    public Boolean getAvatarCollisionEnabled() {
        return avatarCollisionEnabled;
    }

    public void setAvatarCollisionEnabled(Boolean avatarCollisionEnabled) {
        this.avatarCollisionEnabled = avatarCollisionEnabled;
    }

    public String getAvatarCustomScripts() {
        return avatarCustomScripts;
    }

    public void setAvatarCustomScripts(String avatarCustomScripts) {
        this.avatarCustomScripts = avatarCustomScripts;
    }

    public String getAvatarAiBehaviorProfile() {
        return avatarAiBehaviorProfile;
    }

    public void setAvatarAiBehaviorProfile(String avatarAiBehaviorProfile) {
        this.avatarAiBehaviorProfile = avatarAiBehaviorProfile;
    }

    public Double getAvatarVisibilityRange() {
        return avatarVisibilityRange;
    }

    public void setAvatarVisibilityRange(Double avatarVisibilityRange) {
        this.avatarVisibilityRange = avatarVisibilityRange;
    }

    public Boolean getAvatarShadowEnabled() {
        return avatarShadowEnabled;
    }

    public void setAvatarShadowEnabled(Boolean avatarShadowEnabled) {
        this.avatarShadowEnabled = avatarShadowEnabled;
    }

    public Boolean getAvatarReflectionEnabled() {
        return avatarReflectionEnabled;
    }

    public void setAvatarReflectionEnabled(Boolean avatarReflectionEnabled) {
        this.avatarReflectionEnabled = avatarReflectionEnabled;
    }

    public String getAvatarLightingQuality() {
        return avatarLightingQuality;
    }

    public void setAvatarLightingQuality(String avatarLightingQuality) {
        this.avatarLightingQuality = avatarLightingQuality;
    }

    public String getAvatarStatus() {
        return avatarStatus;
    }

    public void setAvatarStatus(String avatarStatus) {
        this.avatarStatus = avatarStatus;
    }

    public Boolean getAvatarIsPublic() {
        return avatarIsPublic;
    }

    public void setAvatarIsPublic(Boolean avatarIsPublic) {
        this.avatarIsPublic = avatarIsPublic;
    }

    public Integer getAvatarUsageCount() {
        return avatarUsageCount;
    }

    public void setAvatarUsageCount(Integer avatarUsageCount) {
        this.avatarUsageCount = avatarUsageCount;
    }

    public LocalDateTime getAvatarLastUsed() {
        return avatarLastUsed;
    }

    public void setAvatarLastUsed(LocalDateTime avatarLastUsed) {
        this.avatarLastUsed = avatarLastUsed;
    }

    public String getAvatarCreationSource() {
        return avatarCreationSource;
    }

    public void setAvatarCreationSource(String avatarCreationSource) {
        this.avatarCreationSource = avatarCreationSource;
    }

    public Double getAvatarRating() {
        return avatarRating;
    }

    public void setAvatarRating(Double avatarRating) {
        this.avatarRating = avatarRating;
    }

    public Integer getAvatarRatingCount() {
        return avatarRatingCount;
    }

    public void setAvatarRatingCount(Integer avatarRatingCount) {
        this.avatarRatingCount = avatarRatingCount;
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

    public String getCustomizationDataJson() {
        return customizationDataJson;
    }

    public void setCustomizationDataJson(String customizationDataJson) {
        this.customizationDataJson = customizationDataJson;
    }

    // 实用方法
    public void incrementUsageCount() {
        if (avatarUsageCount == null) {
            avatarUsageCount = 0;
        }
        avatarUsageCount++;
        avatarLastUsed = LocalDateTime.now();
    }

    public void updateRating(double newRating) {
        if (avatarRatingCount == null) avatarRatingCount = 0;
        if (avatarRating == null) avatarRating = 0.0;
        
        double totalRating = avatarRating * avatarRatingCount + newRating;
        avatarRatingCount++;
        avatarRating = totalRating / avatarRatingCount;
    }

    public List<String> getAccessoriesList() {
        if (avatarAccessories == null || avatarAccessories.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(avatarAccessories.split(","));
    }

    public List<String> getAnimationSetList() {
        if (avatarAnimationSet == null || avatarAnimationSet.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(avatarAnimationSet.split(","));
    }

    public List<String> getExpressionSetList() {
        if (avatarExpressionSet == null || avatarExpressionSet.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(avatarExpressionSet.split(","));
    }

    public List<String> getGestureSetList() {
        if (avatarGestureSet == null || avatarGestureSet.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(avatarGestureSet.split(","));
    }

    public boolean isReadyForVr() {
        return avatarArVrCompatible != null && avatarArVrCompatible && 
               supportedVrPlatforms != null && !supportedVrPlatforms.trim().isEmpty();
    }

    public boolean isHighQuality() {
        return "HIGH".equals(avatarLightingQuality) || "ULTRA".equals(avatarLightingQuality);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "VirtualAvatarEntity{" +
                "avatarId='" + avatarId + '\'' +
                ", userId='" + userId + '\'' +
                ", avatarName='" + avatarName + '\'' +
                ", avatarGender='" + avatarGender + '\'' +
                ", avatarStatus='" + avatarStatus + '\'' +
                '}';
    }
}