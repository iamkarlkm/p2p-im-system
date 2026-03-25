/// 数字孪生虚拟会议空间 Dart 模型
/// 支持 3D 虚拟环境、实时协作、空间音频、虚拟化身、AR/VR 集成、场景模拟
/// 
/// @since 2026-03-23
/// @version 1.0.0
library;

import 'dart:convert';

// ==================== 枚举类型 ====================

/// 虚拟空间类型
enum VirtualSpaceType {
  office,
  conference,
  lounge,
  training,
  exhibition,
  custom
}

/// 空间状态
enum SpaceStatus {
  created,
  active,
  inactive,
  maintenance,
  archived
}

/// 访问控制模式
enum AccessControlMode {
  public,
  private,
  inviteOnly,
  passwordProtected
}

/// 环境风格
enum EnvironmentStyle {
  modern,
  industrial,
  nature,
  futuristic,
  minimalist,
  custom
}

/// 混响设置
enum ReverbSettings {
  none,
  smallRoom,
  mediumRoom,
  largeHall,
  custom
}

/// 化身自定义级别
enum AvatarCustomizationLevel {
  none,
  basic,
  advanced,
  full
}

/// 物理引擎
enum PhysicsEngine {
  unity,
  unreal,
  custom,
  basic
}

/// 照明系统
enum LightingSystem {
  dynamic,
  static,
  dayNightCycle,
  moodBased
}

/// 天气效果
enum WeatherEffect {
  rain,
  snow,
  fog,
  wind,
  lightning,
  clear
}

/// 协作工具类型
enum CollaborationTool {
  whiteboard,
  screenshare,
  modelViewer3d,
  videoPlayer,
  documentEditor,
  codeEditor,
  presentation,
  poll
}

/// 对象交互类型
enum ObjectInteractionType {
  pickup,
  move,
  rotate,
  scale,
  animate,
  collaborate
}

/// VR 设备类型
enum VrDeviceType {
  standalone,
  pcVr,
  console,
  mobile,
  ar
}

/// 化身性别
enum AvatarGender {
  male,
  female,
  nonBinary,
  custom
}

/// 化身种族
enum AvatarRace {
  human,
  robot,
  animal,
  fantasy,
  alien,
  custom
}

/// 化身身体类型
enum AvatarBodyType {
  slim,
  athletic,
  average,
  muscular,
  plusSize,
  custom
}

/// 化身服装风格
enum AvatarClothingStyle {
  casual,
  formal,
  business,
  sports,
  fantasy,
  sciFi,
  custom
}

/// 化身声音类型
enum AvatarVoiceType {
  masculine,
  feminine,
  neutral,
  robotic,
  custom
}

// ==================== 数据模型 ====================

/// 虚拟空间基础信息
class VirtualSpaceBase {
  final String spaceId;
  final String spaceName;
  final String? spaceDescription;
  final VirtualSpaceType spaceType;
  final int maxCapacity;
  final int currentParticipants;
  final SpaceStatus spaceStatus;
  final String? hostUserId;
  final AccessControlMode accessControlMode;
  final List<String>? spaceTags;
  final double? spaceRating;
  final int? ratingCount;
  final int? totalUsageMinutes;

  VirtualSpaceBase({
    required this.spaceId,
    required this.spaceName,
    this.spaceDescription,
    required this.spaceType,
    required this.maxCapacity,
    required this.currentParticipants,
    required this.spaceStatus,
    this.hostUserId,
    required this.accessControlMode,
    this.spaceTags,
    this.spaceRating,
    this.ratingCount,
    this.totalUsageMinutes,
  });

  factory VirtualSpaceBase.fromJson(Map<String, dynamic> json) {
    return VirtualSpaceBase(
      spaceId: json['spaceId'] as String,
      spaceName: json['spaceName'] as String,
      spaceDescription: json['spaceDescription'] as String?,
      spaceType: VirtualSpaceType.values.firstWhere(
        (e) => e.name == json['spaceType'],
        orElse: () => VirtualSpaceType.custom,
      ),
      maxCapacity: json['maxCapacity'] as int,
      currentParticipants: json['currentParticipants'] as int,
      spaceStatus: SpaceStatus.values.firstWhere(
        (e) => e.name == json['spaceStatus'],
        orElse: () => SpaceStatus.archived,
      ),
      hostUserId: json['hostUserId'] as String?,
      accessControlMode: AccessControlMode.values.firstWhere(
        (e) => e.name == json['accessControlMode'],
        orElse: () => AccessControlMode.public,
      ),
      spaceTags: json['spaceTags'] != null 
          ? List<String>.from(json['spaceTags']) 
          : null,
      spaceRating: json['spaceRating'] as double?,
      ratingCount: json['ratingCount'] as int?,
      totalUsageMinutes: json['totalUsageMinutes'] as int?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'spaceId': spaceId,
      'spaceName': spaceName,
      'spaceDescription': spaceDescription,
      'spaceType': spaceType.name,
      'maxCapacity': maxCapacity,
      'currentParticipants': currentParticipants,
      'spaceStatus': spaceStatus.name,
      'hostUserId': hostUserId,
      'accessControlMode': accessControlMode.name,
      'spaceTags': spaceTags,
      'spaceRating': spaceRating,
      'ratingCount': ratingCount,
      'totalUsageMinutes': totalUsageMinutes,
    };
  }

  bool get isFull => currentParticipants >= maxCapacity;
  bool get canJoin => spaceStatus == SpaceStatus.active && !isFull;

  @override
  String toString() {
    return 'VirtualSpaceBase(spaceId: $spaceId, spaceName: $spaceName, status: $spaceStatus)';
  }
}

/// 虚拟空间详细配置
class VirtualSpaceConfig {
  final String? spaceDimensions;
  final EnvironmentStyle? environmentStyle;
  final bool? backgroundMusicEnabled;
  final bool spatialAudioEnabled;
  final ReverbSettings? reverbSettings;
  final bool virtualAvatarEnabled;
  final AvatarCustomizationLevel? avatarCustomizationLevel;
  final bool? arVrIntegrationEnabled;
  final List<String>? supportedVrDevices;
  final bool? sceneSimulationEnabled;
  final List<String>? availableScenes;
  final bool? interactiveObjectsEnabled;
  final List<ObjectInteractionType>? objectInteractionTypes;
  final bool collaborationToolsEnabled;
  final List<CollaborationTool>? availableTools;
  final bool? realTimePhysicsEnabled;
  final PhysicsEngine? physicsEngine;
  final LightingSystem? lightingSystem;
  final bool? weatherEffectsEnabled;
  final List<WeatherEffect>? availableWeatherEffects;
  final String? accessPassword;
  final String? metadataJson;
  final String? customConfigJson;

  VirtualSpaceConfig({
    this.spaceDimensions,
    this.environmentStyle,
    this.backgroundMusicEnabled,
    required this.spatialAudioEnabled,
    this.reverbSettings,
    required this.virtualAvatarEnabled,
    this.avatarCustomizationLevel,
    this.arVrIntegrationEnabled,
    this.supportedVrDevices,
    this.sceneSimulationEnabled,
    this.availableScenes,
    this.interactiveObjectsEnabled,
    this.objectInteractionTypes,
    required this.collaborationToolsEnabled,
    this.availableTools,
    this.realTimePhysicsEnabled,
    this.physicsEngine,
    this.lightingSystem,
    this.weatherEffectsEnabled,
    this.availableWeatherEffects,
    this.accessPassword,
    this.metadataJson,
    this.customConfigJson,
  });

  factory VirtualSpaceConfig.fromJson(Map<String, dynamic> json) {
    return VirtualSpaceConfig(
      spaceDimensions: json['spaceDimensions'] as String?,
      environmentStyle: json['environmentStyle'] != null
          ? EnvironmentStyle.values.firstWhere(
              (e) => e.name == json['environmentStyle'],
              orElse: () => EnvironmentStyle.modern,
            )
          : null,
      backgroundMusicEnabled: json['backgroundMusicEnabled'] as bool?,
      spatialAudioEnabled: json['spatialAudioEnabled'] as bool? ?? true,
      reverbSettings: json['reverbSettings'] != null
          ? ReverbSettings.values.firstWhere(
              (e) => e.name == json['reverbSettings'],
              orElse: () => ReverbSettings.mediumRoom,
            )
          : null,
      virtualAvatarEnabled: json['virtualAvatarEnabled'] as bool? ?? true,
      avatarCustomizationLevel: json['avatarCustomizationLevel'] != null
          ? AvatarCustomizationLevel.values.firstWhere(
              (e) => e.name == json['avatarCustomizationLevel'],
              orElse: () => AvatarCustomizationLevel.basic,
            )
          : null,
      arVrIntegrationEnabled: json['arVrIntegrationEnabled'] as bool?,
      supportedVrDevices: json['supportedVrDevices'] != null
          ? List<String>.from(json['supportedVrDevices'])
          : null,
      sceneSimulationEnabled: json['sceneSimulationEnabled'] as bool?,
      availableScenes: json['availableScenes'] != null
          ? List<String>.from(json['availableScenes'])
          : null,
      interactiveObjectsEnabled: json['interactiveObjectsEnabled'] as bool?,
      objectInteractionTypes: json['objectInteractionTypes'] != null
          ? (json['objectInteractionTypes'] as List)
              .map((e) => ObjectInteractionType.values.firstWhere(
                    (t) => t.name == e,
                    orElse: () => ObjectInteractionType.move,
                  ))
              .toList()
          : null,
      collaborationToolsEnabled: json['collaborationToolsEnabled'] as bool? ?? true,
      availableTools: json['availableTools'] != null
          ? (json['availableTools'] as List)
              .map((e) => CollaborationTool.values.firstWhere(
                    (t) => t.name == e,
                    orElse: () => CollaborationTool.whiteboard,
                  ))
              .toList()
          : null,
      realTimePhysicsEnabled: json['realTimePhysicsEnabled'] as bool?,
      physicsEngine: json['physicsEngine'] != null
          ? PhysicsEngine.values.firstWhere(
              (e) => e.name == json['physicsEngine'],
              orElse: () => PhysicsEngine.basic,
            )
          : null,
      lightingSystem: json['lightingSystem'] != null
          ? LightingSystem.values.firstWhere(
              (e) => e.name == json['lightingSystem'],
              orElse: () => LightingSystem.dynamic,
            )
          : null,
      weatherEffectsEnabled: json['weatherEffectsEnabled'] as bool?,
      availableWeatherEffects: json['availableWeatherEffects'] != null
          ? (json['availableWeatherEffects'] as List)
              .map((e) => WeatherEffect.values.firstWhere(
                    (t) => t.name == e,
                    orElse: () => WeatherEffect.clear,
                  ))
              .toList()
          : null,
      accessPassword: json['accessPassword'] as String?,
      metadataJson: json['metadataJson'] as String?,
      customConfigJson: json['customConfigJson'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'spaceDimensions': spaceDimensions,
      'environmentStyle': environmentStyle?.name,
      'backgroundMusicEnabled': backgroundMusicEnabled,
      'spatialAudioEnabled': spatialAudioEnabled,
      'reverbSettings': reverbSettings?.name,
      'virtualAvatarEnabled': virtualAvatarEnabled,
      'avatarCustomizationLevel': avatarCustomizationLevel?.name,
      'arVrIntegrationEnabled': arVrIntegrationEnabled,
      'supportedVrDevices': supportedVrDevices,
      'sceneSimulationEnabled': sceneSimulationEnabled,
      'availableScenes': availableScenes,
      'interactiveObjectsEnabled': interactiveObjectsEnabled,
      'objectInteractionTypes': objectInteractionTypes?.map((e) => e.name).toList(),
      'collaborationToolsEnabled': collaborationToolsEnabled,
      'availableTools': availableTools?.map((e) => e.name).toList(),
      'realTimePhysicsEnabled': realTimePhysicsEnabled,
      'physicsEngine': physicsEngine?.name,
      'lightingSystem': lightingSystem?.name,
      'weatherEffectsEnabled': weatherEffectsEnabled,
      'availableWeatherEffects': availableWeatherEffects?.map((e) => e.name).toList(),
      'accessPassword': accessPassword,
      'metadataJson': metadataJson,
      'customConfigJson': customConfigJson,
    };
  }
}

/// 完整的虚拟空间信息
class VirtualSpaceInfo extends VirtualSpaceBase {
  final DateTime createdAt;
  final DateTime updatedAt;
  final DateTime? lastActivityTime;
  final VirtualSpaceConfig config;

  VirtualSpaceInfo({
    required String spaceId,
    required String spaceName,
    String? spaceDescription,
    required VirtualSpaceType spaceType,
    required int maxCapacity,
    required int currentParticipants,
    required SpaceStatus spaceStatus,
    String? hostUserId,
    required AccessControlMode accessControlMode,
    List<String>? spaceTags,
    double? spaceRating,
    int? ratingCount,
    int? totalUsageMinutes,
    required this.createdAt,
    required this.updatedAt,
    this.lastActivityTime,
    required this.config,
  }) : super(
          spaceId: spaceId,
          spaceName: spaceName,
          spaceDescription: spaceDescription,
          spaceType: spaceType,
          maxCapacity: maxCapacity,
          currentParticipants: currentParticipants,
          spaceStatus: spaceStatus,
          hostUserId: hostUserId,
          accessControlMode: accessControlMode,
          spaceTags: spaceTags,
          spaceRating: spaceRating,
          ratingCount: ratingCount,
          totalUsageMinutes: totalUsageMinutes,
        );

  factory VirtualSpaceInfo.fromJson(Map<String, dynamic> json) {
    return VirtualSpaceInfo(
      spaceId: json['spaceId'] as String,
      spaceName: json['spaceName'] as String,
      spaceDescription: json['spaceDescription'] as String?,
      spaceType: VirtualSpaceType.values.firstWhere(
        (e) => e.name == json['spaceType'],
        orElse: () => VirtualSpaceType.custom,
      ),
      maxCapacity: json['maxCapacity'] as int,
      currentParticipants: json['currentParticipants'] as int,
      spaceStatus: SpaceStatus.values.firstWhere(
        (e) => e.name == json['spaceStatus'],
        orElse: () => SpaceStatus.archived,
      ),
      hostUserId: json['hostUserId'] as String?,
      accessControlMode: AccessControlMode.values.firstWhere(
        (e) => e.name == json['accessControlMode'],
        orElse: () => AccessControlMode.public,
      ),
      spaceTags: json['spaceTags'] != null 
          ? List<String>.from(json['spaceTags']) 
          : null,
      spaceRating: json['spaceRating'] as double?,
      ratingCount: json['ratingCount'] as int?,
      totalUsageMinutes: json['totalUsageMinutes'] as int?,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
      lastActivityTime: json['lastActivityTime'] != null
          ? DateTime.parse(json['lastActivityTime'] as String)
          : null,
      config: VirtualSpaceConfig.fromJson(json['config'] as Map<String, dynamic>? ?? {}),
    );
  }

  @override
  Map<String, dynamic> toJson() {
    return {
      ...super.toJson(),
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'lastActivityTime': lastActivityTime?.toIso8601String(),
      'config': config.toJson(),
    };
  }

  @override
  String toString() {
    return 'VirtualSpaceInfo(spaceId: $spaceId, spaceName: $spaceName, status: $spaceStatus, participants: $currentParticipants/$maxCapacity)';
  }
}

/// 虚拟化身信息
class VirtualAvatarInfo {
  final String avatarId;
  final String userId;
  final String avatarName;
  final AvatarGender? avatarGender;
  final AvatarRace? avatarRace;
  final String? avatarSkinTone;
  final String? avatarHairStyle;
  final String? avatarHairColor;
  final String? avatarEyeColor;
  final double? avatarHeight;
  final double? avatarWeight;
  final AvatarBodyType? avatarBodyType;
  final AvatarClothingStyle? avatarClothingStyle;
  final String? avatarPrimaryColor;
  final String? avatarSecondaryColor;
  final List<String>? avatarAccessories;
  final List<String>? avatarFacialFeatures;
  final List<String>? avatarAnimationSet;
  final List<String>? avatarExpressionSet;
  final AvatarVoiceType? avatarVoiceType;
  final double? avatarVoicePitch;
  final double? avatarVoiceSpeed;
  final List<String>? avatarGestureSet;
  final List<String>? avatarEquipmentSlots;
  final bool avatarArVrCompatible;
  final List<String>? supportedVrPlatforms;
  final String? avatarRigType;
  final int? avatarBoneCount;
  final String? avatarTextureResolution;
  final int? avatarPolygonCount;
  final int? avatarLodLevels;
  final bool? avatarPhysicsEnabled;
  final bool? avatarClothSimulation;
  final bool? avatarHairSimulation;
  final bool? avatarCollisionEnabled;
  final String? avatarCustomScripts;
  final String? avatarAiBehaviorProfile;
  final double? avatarVisibilityRange;
  final bool? avatarShadowEnabled;
  final bool? avatarReflectionEnabled;
  final String? avatarLightingQuality;
  final String avatarStatus;
  final bool? avatarIsPublic;
  final int? avatarUsageCount;
  final DateTime? avatarLastUsed;
  final String? avatarCreationSource;
  final double? avatarRating;
  final int? avatarRatingCount;
  final DateTime createdAt;
  final DateTime updatedAt;

  VirtualAvatarInfo({
    required this.avatarId,
    required this.userId,
    required this.avatarName,
    this.avatarGender,
    this.avatarRace,
    this.avatarSkinTone,
    this.avatarHairStyle,
    this.avatarHairColor,
    this.avatarEyeColor,
    this.avatarHeight,
    this.avatarWeight,
    this.avatarBodyType,
    this.avatarClothingStyle,
    this.avatarPrimaryColor,
    this.avatarSecondaryColor,
    this.avatarAccessories,
    this.avatarFacialFeatures,
    this.avatarAnimationSet,
    this.avatarExpressionSet,
    this.avatarVoiceType,
    this.avatarVoicePitch,
    this.avatarVoiceSpeed,
    this.avatarGestureSet,
    this.avatarEquipmentSlots,
    required this.avatarArVrCompatible,
    this.supportedVrPlatforms,
    this.avatarRigType,
    this.avatarBoneCount,
    this.avatarTextureResolution,
    this.avatarPolygonCount,
    this.avatarLodLevels,
    this.avatarPhysicsEnabled,
    this.avatarClothSimulation,
    this.avatarHairSimulation,
    this.avatarCollisionEnabled,
    this.avatarCustomScripts,
    this.avatarAiBehaviorProfile,
    this.avatarVisibilityRange,
    this.avatarShadowEnabled,
    this.avatarReflectionEnabled,
    this.avatarLightingQuality,
    required this.avatarStatus,
    this.avatarIsPublic,
    this.avatarUsageCount,
    this.avatarLastUsed,
    this.avatarCreationSource,
    this.avatarRating,
    this.avatarRatingCount,
    required this.createdAt,
    required this.updatedAt,
  });

  factory VirtualAvatarInfo.fromJson(Map<String, dynamic> json) {
    return VirtualAvatarInfo(
      avatarId: json['avatarId'] as String,
      userId: json['userId'] as String,
      avatarName: json['avatarName'] as String,
      avatarGender: json['avatarGender'] != null
          ? AvatarGender.values.firstWhere(
              (e) => e.name == json['avatarGender'],
              orElse: () => AvatarGender.nonBinary,
            )
          : null,
      avatarRace: json['avatarRace'] != null
          ? AvatarRace.values.firstWhere(
              (e) => e.name == json['avatarRace'],
              orElse: () => AvatarRace.human,
            )
          : null,
      avatarSkinTone: json['avatarSkinTone'] as String?,
      avatarHairStyle: json['avatarHairStyle'] as String?,
      avatarHairColor: json['avatarHairColor'] as String?,
      avatarEyeColor: json['avatarEyeColor'] as String?,
      avatarHeight: json['avatarHeight'] as double?,
      avatarWeight: json['avatarWeight'] as double?,
      avatarBodyType: json['avatarBodyType'] != null
          ? AvatarBodyType.values.firstWhere(
              (e) => e.name == json['avatarBodyType'],
              orElse: () => AvatarBodyType.average,
            )
          : null,
      avatarClothingStyle: json['avatarClothingStyle'] != null
          ? AvatarClothingStyle.values.firstWhere(
              (e) => e.name == json['avatarClothingStyle'],
              orElse: () => AvatarClothingStyle.casual,
            )
          : null,
      avatarPrimaryColor: json['avatarPrimaryColor'] as String?,
      avatarSecondaryColor: json['avatarSecondaryColor'] as String?,
      avatarAccessories: json['avatarAccessories'] != null
          ? List<String>.from(json['avatarAccessories'])
          : null,
      avatarFacialFeatures: json['avatarFacialFeatures'] != null
          ? List<String>.from(json['avatarFacialFeatures'])
          : null,
      avatarAnimationSet: json['avatarAnimationSet'] != null
          ? List<String>.from(json['avatarAnimationSet'])
          : null,
      avatarExpressionSet: json['avatarExpressionSet'] != null
          ? List<String>.from(json['avatarExpressionSet'])
          : null,
      avatarVoiceType: json['avatarVoiceType'] != null
          ? AvatarVoiceType.values.firstWhere(
              (e) => e.name == json['avatarVoiceType'],
              orElse: () => AvatarVoiceType.neutral,
            )
          : null,
      avatarVoicePitch: json['avatarVoicePitch'] as double?,
      avatarVoiceSpeed: json['avatarVoiceSpeed'] as double?,
      avatarGestureSet: json['avatarGestureSet'] != null
          ? List<String>.from(json['avatarGestureSet'])
          : null,
      avatarEquipmentSlots: json['avatarEquipmentSlots'] != null
          ? List<String>.from(json['avatarEquipmentSlots'])
          : null,
      avatarArVrCompatible: json['avatarArVrCompatible'] as bool? ?? true,
      supportedVrPlatforms: json['supportedVrPlatforms'] != null
          ? List<String>.from(json['supportedVrPlatforms'])
          : null,
      avatarRigType: json['avatarRigType'] as String?,
      avatarBoneCount: json['avatarBoneCount'] as int?,
      avatarTextureResolution: json['avatarTextureResolution'] as String?,
      avatarPolygonCount: json['avatarPolygonCount'] as int?,
      avatarLodLevels: json['avatarLodLevels'] as int?,
      avatarPhysicsEnabled: json['avatarPhysicsEnabled'] as bool?,
      avatarClothSimulation: json['avatarClothSimulation'] as bool?,
      avatarHairSimulation: json['avatarHairSimulation'] as bool?,
      avatarCollisionEnabled: json['avatarCollisionEnabled'] as bool?,
      avatarCustomScripts: json['avatarCustomScripts'] as String?,
      avatarAiBehaviorProfile: json['avatarAiBehaviorProfile'] as String?,
      avatarVisibilityRange: json['avatarVisibilityRange'] as double?,
      avatarShadowEnabled: json['avatarShadowEnabled'] as bool?,
      avatarReflectionEnabled: json['avatarReflectionEnabled'] as bool?,
      avatarLightingQuality: json['avatarLightingQuality'] as String?,
      avatarStatus: json['avatarStatus'] as String? ?? 'ACTIVE',
      avatarIsPublic: json['avatarIsPublic'] as bool?,
      avatarUsageCount: json['avatarUsageCount'] as int?,
      avatarLastUsed: json['avatarLastUsed'] != null
          ? DateTime.parse(json['avatarLastUsed'] as String)
          : null,
      avatarCreationSource: json['avatarCreationSource'] as String?,
      avatarRating: json['avatarRating'] as double?,
      avatarRatingCount: json['avatarRatingCount'] as int?,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'avatarId': avatarId,
      'userId': userId,
      'avatarName': avatarName,
      'avatarGender': avatarGender?.name,
      'avatarRace': avatarRace?.name,
      'avatarSkinTone': avatarSkinTone,
      'avatarHairStyle': avatarHairStyle,
      'avatarHairColor': avatarHairColor,
      'avatarEyeColor': avatarEyeColor,
      'avatarHeight': avatarHeight,
      'avatarWeight': avatarWeight,
      'avatarBodyType': avatarBodyType?.name,
      'avatarClothingStyle': avatarClothingStyle?.name,
      'avatarPrimaryColor': avatarPrimaryColor,
      'avatarSecondaryColor': avatarSecondaryColor,
      'avatarAccessories': avatarAccessories,
      'avatarFacialFeatures': avatarFacialFeatures,
      'avatarAnimationSet': avatarAnimationSet,
      'avatarExpressionSet': avatarExpressionSet,
      'avatarVoiceType': avatarVoiceType?.name,
      'avatarVoicePitch': avatarVoicePitch,
      'avatarVoiceSpeed': avatarVoiceSpeed,
      'avatarGestureSet': avatarGestureSet,
      'avatarEquipmentSlots': avatarEquipmentSlots,
      'avatarArVrCompatible': avatarArVrCompatible,
      'supportedVrPlatforms': supportedVrPlatforms,
      'avatarRigType': avatarRigType,
      'avatarBoneCount': avatarBoneCount,
      'avatarTextureResolution': avatarTextureResolution,
      'avatarPolygonCount': avatarPolygonCount,
      'avatarLodLevels': avatarLodLevels,
      'avatarPhysicsEnabled': avatarPhysicsEnabled,
      'avatarClothSimulation': avatarClothSimulation,
      'avatarHairSimulation': avatarHairSimulation,
      'avatarCollisionEnabled': avatarCollisionEnabled,
      'avatarCustomScripts': avatarCustomScripts,
      'avatarAiBehaviorProfile': avatarAiBehaviorProfile,
      'avatarVisibilityRange': avatarVisibilityRange,
      'avatarShadowEnabled': avatarShadowEnabled,
      'avatarReflectionEnabled': avatarReflectionEnabled,
      'avatarLightingQuality': avatarLightingQuality,
      'avatarStatus': avatarStatus,
      'avatarIsPublic': avatarIsPublic,
      'avatarUsageCount': avatarUsageCount,
      'avatarLastUsed': avatarLastUsed?.toIso8601String(),
      'avatarCreationSource': avatarCreationSource,
      'avatarRating': avatarRating,
      'avatarRatingCount': avatarRatingCount,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
    };
  }

  bool get isVrReady => avatarArVrCompatible && (supportedVrPlatforms?.isNotEmpty ?? false);

  @override
  String toString() {
    return 'VirtualAvatarInfo(avatarId: $avatarId, avatarName: $avatarName, status: $avatarStatus)';
  }
}

/// API 响应包装器
class ApiResponse<T> {
  final bool success;
  final String message;
  final T? data;
  final String? error;
  final String? errorType;
  final DateTime timestamp;

  ApiResponse({
    required this.success,
    required this.message,
    this.data,
    this.error,
    this.errorType,
    required this.timestamp,
  });

  factory ApiResponse.fromJson(Map<String, dynamic> json, T Function(Map<String, dynamic>)? fromJson) {
    return ApiResponse(
      success: json['success'] as bool,
      message: json['message'] as String,
      data: json['data'] != null && fromJson != null ? fromJson(json['data']) : null,
      error: json['error'] as String?,
      errorType: json['errorType'] as String?,
      timestamp: DateTime.parse(json['timestamp'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'success': success,
      'message': message,
      'data': data,
      'error': error,
      'errorType': errorType,
      'timestamp': timestamp.toIso8601String(),
    };
  }
}

/// 空间统计信息
class SpaceStatistics {
  final String spaceId;
  final String spaceName;
  final int currentParticipants;
  final int maxCapacity;
  final int totalUsageMinutes;
  final double spaceRating;
  final int ratingCount;
  final DateTime lastActivityTime;
  final DateTime createdAt;
  final SpaceStatus spaceStatus;
  final AccessControlMode accessControlMode;
  final double usageRatePercentage;

  SpaceStatistics({
    required this.spaceId,
    required this.spaceName,
    required this.currentParticipants,
    required this.maxCapacity,
    required this.totalUsageMinutes,
    required this.spaceRating,
    required this.ratingCount,
    required this.lastActivityTime,
    required this.createdAt,
    required this.spaceStatus,
    required this.accessControlMode,
    required this.usageRatePercentage,
  });

  factory SpaceStatistics.fromJson(Map<String, dynamic> json) {
    return SpaceStatistics(
      spaceId: json['spaceId'] as String,
      spaceName: json['spaceName'] as String,
      currentParticipants: json['currentParticipants'] as int,
      maxCapacity: json['maxCapacity'] as int,
      totalUsageMinutes: json['totalUsageMinutes'] as int,
      spaceRating: (json['spaceRating'] as num).toDouble(),
      ratingCount: json['ratingCount'] as int,
      lastActivityTime: DateTime.parse(json['lastActivityTime'] as String),
      createdAt: DateTime.parse(json['createdAt'] as String),
      spaceStatus: SpaceStatus.values.firstWhere(
        (e) => e.name == json['spaceStatus'],
        orElse: () => SpaceStatus.archived,
      ),
      accessControlMode: AccessControlMode.values.firstWhere(
        (e) => e.name == json['accessControlMode'],
        orElse: () => AccessControlMode.public,
      ),
      usageRatePercentage: (json['usageRatePercentage'] as num).toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'spaceId': spaceId,
      'spaceName': spaceName,
      'currentParticipants': currentParticipants,
      'maxCapacity': maxCapacity,
      'totalUsageMinutes': totalUsageMinutes,
      'spaceRating': spaceRating,
      'ratingCount': ratingCount,
      'lastActivityTime': lastActivityTime.toIso8601String(),
      'createdAt': createdAt.toIso8601String(),
      'spaceStatus': spaceStatus.name,
      'accessControlMode': accessControlMode.name,
      'usageRatePercentage': usageRatePercentage,
    };
  }
}

// ==================== 工具函数 ====================

/// 检查空间是否可加入
bool canJoinSpace(VirtualSpaceInfo space) {
  return space.canJoin;
}

/// 计算空间使用率
double calculateUsageRate(VirtualSpaceInfo space) {
  if (space.maxCapacity <= 0) return 0.0;
  return (space.currentParticipants / space.maxCapacity) * 100.0;
}

/// 检查化身是否 VR 就绪
bool isAvatarVrReady(VirtualAvatarInfo avatar) {
  return avatar.isVrReady;
}

/// 默认虚拟空间配置
Map<String, dynamic> get defaultVirtualSpaceConfig => {
      'spatialAudioEnabled': true,
      'virtualAvatarEnabled': true,
      'collaborationToolsEnabled': true,
      'maxCapacity': 50,
      'environmentStyle': 'modern',
      'reverbSettings': 'mediumRoom',
      'avatarCustomizationLevel': 'basic',
      'arVrIntegrationEnabled': false,
      'sceneSimulationEnabled': true,
      'interactiveObjectsEnabled': true,
      'realTimePhysicsEnabled': false,
      'lightingSystem': 'dynamic',
      'weatherEffectsEnabled': false,
    };

/// 默认化身配置
Map<String, dynamic> get defaultAvatarConfig => {
      'avatarGender': 'nonBinary',
      'avatarRace': 'human',
      'avatarSkinTone': 'MEDIUM',
      'avatarHairStyle': 'SHORT',
      'avatarHairColor': '#000000',
      'avatarEyeColor': '#000000',
      'avatarHeight': 1.75,
      'avatarWeight': 70.0,
      'avatarBodyType': 'average',
      'avatarClothingStyle': 'casual',
      'avatarPrimaryColor': '#3498db',
      'avatarSecondaryColor': '#2c3e50',
      'avatarArVrCompatible': true,
      'avatarPhysicsEnabled': true,
      'avatarCollisionEnabled': true,
      'avatarShadowEnabled': true,
      'avatarLightingQuality': 'MEDIUM',
      'avatarVisibilityRange': 100.0,
      'avatarVoicePitch': 1.0,
      'avatarVoiceSpeed': 1.0,
      'avatarStatus': 'ACTIVE',
      'avatarIsPublic': false,
    };