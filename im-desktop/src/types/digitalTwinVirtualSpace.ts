/**
 * 数字孪生虚拟会议空间 TypeScript 类型定义
 * 支持 3D 虚拟环境、实时协作、空间音频、虚拟化身、AR/VR 集成、场景模拟
 * 
 * @since 2026-03-23
 * @version 1.0.0
 */

// ==================== 枚举类型 ====================

/** 虚拟空间类型 */
export enum VirtualSpaceType {
  OFFICE = 'OFFICE',
  CONFERENCE = 'CONFERENCE',
  LOUNGE = 'LOUNGE',
  TRAINING = 'TRAINING',
  EXHIBITION = 'EXHIBITION',
  CUSTOM = 'CUSTOM'
}

/** 空间状态 */
export enum SpaceStatus {
  CREATED = 'CREATED',
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  MAINTENANCE = 'MAINTENANCE',
  ARCHIVED = 'ARCHIVED'
}

/** 访问控制模式 */
export enum AccessControlMode {
  PUBLIC = 'PUBLIC',
  PRIVATE = 'PRIVATE',
  INVITE_ONLY = 'INVITE_ONLY',
  PASSWORD_PROTECTED = 'PASSWORD_PROTECTED'
}

/** 环境风格 */
export enum EnvironmentStyle {
  MODERN = 'MODERN',
  INDUSTRIAL = 'INDUSTRIAL',
  NATURE = 'NATURE',
  FUTURISTIC = 'FUTURISTIC',
  MINIMALIST = 'MINIMALIST',
  CUSTOM = 'CUSTOM'
}

/** 混响设置 */
export enum ReverbSettings {
  NONE = 'NONE',
  SMALL_ROOM = 'SMALL_ROOM',
  MEDIUM_ROOM = 'MEDIUM_ROOM',
  LARGE_HALL = 'LARGE_HALL',
  CUSTOM = 'CUSTOM'
}

/** 化身自定义级别 */
export enum AvatarCustomizationLevel {
  NONE = 'NONE',
  BASIC = 'BASIC',
  ADVANCED = 'ADVANCED',
  FULL = 'FULL'
}

/** 物理引擎 */
export enum PhysicsEngine {
  UNITY = 'UNITY',
  UNREAL = 'UNREAL',
  CUSTOM = 'CUSTOM',
  BASIC = 'BASIC'
}

/** 照明系统 */
export enum LightingSystem {
  DYNAMIC = 'DYNAMIC',
  STATIC = 'STATIC',
  DAY_NIGHT_CYCLE = 'DAY_NIGHT_CYCLE',
  MOOD_BASED = 'MOOD_BASED'
}

/** 天气效果 */
export enum WeatherEffect {
  RAIN = 'RAIN',
  SNOW = 'SNOW',
  FOG = 'FOG',
  WIND = 'WIND',
  LIGHTNING = 'LIGHTNING',
  CLEAR = 'CLEAR'
}

/** 协作工具类型 */
export enum CollaborationTool {
  WHITEBOARD = 'WHITEBOARD',
  SCREENSHARE = 'SCREENSHARE',
  MODEL_VIEWER_3D = 'MODEL_VIEWER_3D',
  VIDEO_PLAYER = 'VIDEO_PLAYER',
  DOCUMENT_EDITOR = 'DOCUMENT_EDITOR',
  CODE_EDITOR = 'CODE_EDITOR',
  PRESENTATION = 'PRESENTATION',
  POLL = 'POLL'
}

/** 对象交互类型 */
export enum ObjectInteractionType {
  PICKUP = 'PICKUP',
  MOVE = 'MOVE',
  ROTATE = 'ROTATE',
  SCALE = 'SCALE',
  ANIMATE = 'ANIMATE',
  COLLABORATE = 'COLLABORATE'
}

/** VR 设备类型 */
export enum VrDeviceType {
  STANDALONE = 'STANDALONE',
  PC_VR = 'PC_VR',
  CONSOLE = 'CONSOLE',
  MOBILE = 'MOBILE',
  AR = 'AR'
}

// ==================== 接口类型 ====================

/** 虚拟空间基础信息 */
export interface VirtualSpaceBase {
  spaceId: string;
  spaceName: string;
  spaceDescription?: string;
  spaceType: VirtualSpaceType;
  maxCapacity: number;
  currentParticipants: number;
  spaceStatus: SpaceStatus;
  hostUserId?: string;
  accessControlMode: AccessControlMode;
  spaceTags?: string[];
  spaceRating?: number;
  ratingCount?: number;
  totalUsageMinutes?: number;
}

/** 虚拟空间详细配置 */
export interface VirtualSpaceConfig {
  spaceDimensions?: string; // "100x100x50"
  environmentStyle?: EnvironmentStyle;
  backgroundMusicEnabled?: boolean;
  spatialAudioEnabled: boolean;
  reverbSettings?: ReverbSettings;
  virtualAvatarEnabled: boolean;
  avatarCustomizationLevel?: AvatarCustomizationLevel;
  arVrIntegrationEnabled?: boolean;
  supportedVrDevices?: string[];
  sceneSimulationEnabled?: boolean;
  availableScenes?: string[];
  interactiveObjectsEnabled?: boolean;
  objectInteractionTypes?: ObjectInteractionType[];
  collaborationToolsEnabled: boolean;
  availableTools?: CollaborationTool[];
  realTimePhysicsEnabled?: boolean;
  physicsEngine?: PhysicsEngine;
  lightingSystem?: LightingSystem;
  weatherEffectsEnabled?: boolean;
  availableWeatherEffects?: WeatherEffect[];
  accessPassword?: string;
  metadataJson?: string;
  customConfigJson?: string;
}

/** 完整的虚拟空间信息 */
export interface VirtualSpaceInfo extends VirtualSpaceBase, VirtualSpaceConfig {
  createdAt: Date;
  updatedAt: Date;
  lastActivityTime?: Date;
}

/** 虚拟化身性别 */
export enum AvatarGender {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
  NON_BINARY = 'NON_BINARY',
  CUSTOM = 'CUSTOM'
}

/** 虚拟化身种族 */
export enum AvatarRace {
  HUMAN = 'HUMAN',
  ROBOT = 'ROBOT',
  ANIMAL = 'ANIMAL',
  FANTASY = 'FANTASY',
  ALIEN = 'ALIEN',
  CUSTOM = 'CUSTOM'
}

/** 化身身体类型 */
export enum AvatarBodyType {
  SLIM = 'SLIM',
  ATHLETIC = 'ATHLETIC',
  AVERAGE = 'AVERAGE',
  MUSCULAR = 'MUSCULAR',
  PLUS_SIZE = 'PLUS_SIZE',
  CUSTOM = 'CUSTOM'
}

/** 化身服装风格 */
export enum AvatarClothingStyle {
  CASUAL = 'CASUAL',
  FORMAL = 'FORMAL',
  BUSINESS = 'BUSINESS',
  SPORTS = 'SPORTS',
  FANTASY = 'FANTASY',
  SCI_FI = 'SCI_FI',
  CUSTOM = 'CUSTOM'
}

/** 化身声音类型 */
export enum AvatarVoiceType {
  MASCULINE = 'MASCULINE',
  FEMININE = 'FEMININE',
  NEUTRAL = 'NEUTRAL',
  ROBOTIC = 'ROBOTIC',
  CUSTOM = 'CUSTOM'
}

/** 化身装备项 */
export interface AvatarEquipmentItem {
  slot: string; // "HEAD", "CHEST", "HANDS", etc.
  itemId: string;
  itemName: string;
  itemType: string;
  itemUrl?: string;
  itemProperties?: Record<string, any>;
}

/** 虚拟化身信息 */
export interface VirtualAvatarInfo {
  avatarId: string;
  userId: string;
  avatarName: string;
  avatarGender?: AvatarGender;
  avatarRace?: AvatarRace;
  avatarSkinTone?: string;
  avatarHairStyle?: string;
  avatarHairColor?: string;
  avatarEyeColor?: string;
  avatarHeight?: number; // 米
  avatarWeight?: number; // 千克
  avatarBodyType?: AvatarBodyType;
  avatarClothingStyle?: AvatarClothingStyle;
  avatarPrimaryColor?: string;
  avatarSecondaryColor?: string;
  avatarAccessories?: string[];
  avatarFacialFeatures?: string[];
  avatarAnimationSet?: string[];
  avatarExpressionSet?: string[];
  avatarVoiceType?: AvatarVoiceType;
  avatarVoicePitch?: number; // 0.5-2.0
  avatarVoiceSpeed?: number; // 0.5-2.0
  avatarGestureSet?: string[];
  avatarEquipmentSlots?: string[];
  avatarEquippedItems?: AvatarEquipmentItem[];
  avatarArVrCompatible: boolean;
  supportedVrPlatforms?: string[];
  avatarRigType?: string;
  avatarBoneCount?: number;
  avatarTextureResolution?: string;
  avatarPolygonCount?: number;
  avatarLodLevels?: number;
  avatarPhysicsEnabled?: boolean;
  avatarClothSimulation?: boolean;
  avatarHairSimulation?: boolean;
  avatarCollisionEnabled?: boolean;
  avatarCustomScripts?: string;
  avatarAiBehaviorProfile?: string;
  avatarVisibilityRange?: number;
  avatarShadowEnabled?: boolean;
  avatarReflectionEnabled?: boolean;
  avatarLightingQuality?: string;
  avatarStatus: string;
  avatarIsPublic?: boolean;
  avatarUsageCount?: number;
  avatarLastUsed?: Date;
  avatarCreationSource?: string;
  avatarRating?: number;
  avatarRatingCount?: number;
  createdAt: Date;
  updatedAt: Date;
  metadataJson?: string;
  customizationDataJson?: string;
}

/** 空间音频配置 */
export interface SpatialAudioConfig {
  reverbSettings: ReverbSettings;
  backgroundMusicEnabled: boolean;
  backgroundMusicUrl?: string;
  backgroundMusicVolume?: number; // 0.0-1.0
  voiceChatEnabled: boolean;
  voiceChatVolume?: number; // 0.0-1.0
  spatializationEnabled: boolean;
  hrtfEnabled: boolean;
  maxDistance: number; // 米
  rolloffFactor: number;
}

/** AR/VR 配置 */
export interface ArVrConfig {
  enabled: boolean;
  supportedDevices: VrDeviceType[];
  physicsEngine: PhysicsEngine;
  realTimePhysicsEnabled: boolean;
  handTrackingEnabled: boolean;
  eyeTrackingEnabled: boolean;
  hapticFeedbackEnabled: boolean;
  maxRenderDistance: number; // 米
  antiAliasingEnabled: boolean;
  shadowQuality: string; // "LOW", "MEDIUM", "HIGH", "ULTRA"
  textureQuality: string;
}

/** 场景配置 */
export interface SceneConfig {
  sceneId: string;
  sceneName: string;
  sceneType: string;
  sceneUrl: string;
  thumbnailUrl?: string;
  environmentMapUrl?: string;
  lightingPreset: string;
  weatherPreset?: WeatherEffect;
  timeOfDay?: string; // "MORNING", "DAY", "EVENING", "NIGHT"
  fogDensity?: number; // 0.0-1.0
  windSpeed?: number; // m/s
  ambientSounds?: string[];
  interactiveObjects?: SceneObject[];
}

/** 场景对象 */
export interface SceneObject {
  objectId: string;
  objectName: string;
  objectType: string;
  position: { x: number; y: number; z: number };
  rotation: { x: number; y: number; z: number; w?: number };
  scale: { x: number; y: number; z: number };
  modelUrl: string;
  collisionEnabled: boolean;
  physicsEnabled: boolean;
  interactive: boolean;
  interactionType?: ObjectInteractionType;
  interactionScript?: string;
}

/** 协作会话 */
export interface CollaborationSession {
  sessionId: string;
  spaceId: string;
  toolType: CollaborationTool;
  hostUserId: string;
  participantIds: string[];
  sessionState: Record<string, any>;
  createdAt: Date;
  lastActivity: Date;
  sessionStatus: 'ACTIVE' | 'PAUSED' | 'ENDED';
}

/** 用户空间位置 */
export interface UserSpacePosition {
  userId: string;
  avatarId?: string;
  position: { x: number; y: number; z: number };
  rotation: { x: number; y: number; z: number; w?: number };
  velocity?: { x: number; y: number; z: number };
  animationState?: string;
  expressionState?: string;
  voiceActive: boolean;
  lastUpdate: Date;
}

/** API 响应包装器 */
export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data?: T;
  error?: string;
  errorType?: string;
  timestamp: Date;
}

/** 分页响应 */
export interface PaginatedResponse<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

/** 空间统计信息 */
export interface SpaceStatistics {
  spaceId: string;
  spaceName: string;
  currentParticipants: number;
  maxCapacity: number;
  totalUsageMinutes: number;
  spaceRating: number;
  ratingCount: number;
  lastActivityTime: Date;
  createdAt: Date;
  spaceStatus: SpaceStatus;
  accessControlMode: AccessControlMode;
  usageRatePercentage: number;
  averageSessionDuration: number; // 分钟
  peakParticipants: number;
  peakTime: Date;
}

// ==================== 常量配置 ====================

/** 默认虚拟空间配置 */
export const DEFAULT_VIRTUAL_SPACE_CONFIG: VirtualSpaceConfig = {
  spatialAudioEnabled: true,
  virtualAvatarEnabled: true,
  collaborationToolsEnabled: true,
  maxCapacity: 50,
  environmentStyle: EnvironmentStyle.MODERN,
  reverbSettings: ReverbSettings.MEDIUM_ROOM,
  avatarCustomizationLevel: AvatarCustomizationLevel.BASIC,
  arVrIntegrationEnabled: false,
  sceneSimulationEnabled: true,
  interactiveObjectsEnabled: true,
  realTimePhysicsEnabled: false,
  lightingSystem: LightingSystem.DYNAMIC,
  weatherEffectsEnabled: false,
  availableScenes: ['DAY', 'NIGHT', 'FUTURE_CITY'],
  availableTools: [CollaborationTool.WHITEBOARD, CollaborationTool.SCREENSHARE],
  objectInteractionTypes: [ObjectInteractionType.MOVE, ObjectInteractionType.ROTATE]
};

/** 默认化身配置 */
export const DEFAULT_AVATAR_CONFIG: Partial<VirtualAvatarInfo> = {
  avatarGender: AvatarGender.NON_BINARY,
  avatarRace: AvatarRace.HUMAN,
  avatarSkinTone: 'MEDIUM',
  avatarHairStyle: 'SHORT',
  avatarHairColor: '#000000',
  avatarEyeColor: '#000000',
  avatarHeight: 1.75,
  avatarWeight: 70.0,
  avatarBodyType: AvatarBodyType.AVERAGE,
  avatarClothingStyle: AvatarClothingStyle.CASUAL,
  avatarPrimaryColor: '#3498db',
  avatarSecondaryColor: '#2c3e50',
  avatarArVrCompatible: true,
  avatarPhysicsEnabled: true,
  avatarCollisionEnabled: true,
  avatarShadowEnabled: true,
  avatarLightingQuality: 'MEDIUM',
  avatarVisibilityRange: 100.0,
  avatarVoicePitch: 1.0,
  avatarVoiceSpeed: 1.0,
  avatarStatus: 'ACTIVE',
  avatarIsPublic: false
};

/** 支持的 VR 设备列表 */
export const SUPPORTED_VR_DEVICES = [
  { id: 'oculus-quest2', name: 'Oculus Quest 2', type: VrDeviceType.STANDALONE },
  { id: 'htc-vive', name: 'HTC Vive', type: VrDeviceType.PC_VR },
  { id: 'valve-index', name: 'Valve Index', type: VrDeviceType.PC_VR },
  { id: 'psvr2', name: 'PlayStation VR2', type: VrDeviceType.CONSOLE },
  { id: 'windows-mr', name: 'Windows Mixed Reality', type: VrDeviceType.PC_VR },
  { id: 'meta-quest-pro', name: 'Meta Quest Pro', type: VrDeviceType.STANDALONE }
];

/** 支持的场景类型 */
export const SUPPORTED_SCENE_TYPES = [
  { id: 'office', name: '现代办公室', category: 'WORK', complexity: 'MEDIUM', recommendedCapacity: 20 },
  { id: 'conference', name: '会议厅', category: 'WORK', complexity: 'LOW', recommendedCapacity: 100 },
  { id: 'lounge', name: '休息室', category: 'SOCIAL', complexity: 'LOW', recommendedCapacity: 30 },
  { id: 'training', name: '培训室', category: 'EDUCATION', complexity: 'MEDIUM', recommendedCapacity: 50 },
  { id: 'exhibition', name: '展览馆', category: 'EVENT', complexity: 'HIGH', recommendedCapacity: 200 },
  { id: 'nature', name: '自然环境', category: 'RELAXATION', complexity: 'HIGH', recommendedCapacity: 50 },
  { id: 'future-city', name: '未来城市', category: 'FUTURISTIC', complexity: 'VERY_HIGH', recommendedCapacity: 100 }
];

// ==================== 工具函数 ====================

/** 检查空间是否可加入 */
export function canJoinSpace(space: VirtualSpaceInfo): boolean {
  return space.spaceStatus === SpaceStatus.ACTIVE && 
         space.currentParticipants < space.maxCapacity;
}

/** 计算空间使用率 */
export function calculateUsageRate(space: VirtualSpaceInfo): number {
  if (space.maxCapacity <= 0) return 0;
  return (space.currentParticipants / space.maxCapacity) * 100;
}

/** 检查化身是否 VR 就绪 */
export function isAvatarVrReady(avatar: VirtualAvatarInfo): boolean {
  return avatar.avatarArVrCompatible && 
         avatar.supportedVrPlatforms && 
         avatar.supportedVrPlatforms.length > 0;
}

/** 格式化空间维度字符串 */
export function formatSpaceDimensions(dimensions: string): { width: number; length: number; height: number } | null {
  if (!dimensions) return null;
  
  const parts = dimensions.split('x').map(part => parseFloat(part.trim()));
  if (parts.length !== 3 || parts.some(isNaN)) {
    return null;
  }
  
  return {
    width: parts[0],
    length: parts[1],
    height: parts[2]
  };
}

/** 生成默认空间名称 */
export function generateDefaultSpaceName(spaceType: VirtualSpaceType): string {
  const typeNames: Record<VirtualSpaceType, string> = {
    [VirtualSpaceType.OFFICE]: '虚拟办公室',
    [VirtualSpaceType.CONFERENCE]: '虚拟会议',
    [VirtualSpaceType.LOUNGE]: '虚拟休息室',
    [VirtualSpaceType.TRAINING]: '虚拟培训室',
    [VirtualSpaceType.EXHIBITION]: '虚拟展览馆',
    [VirtualSpaceType.CUSTOM]: '自定义虚拟空间'
  };
  
  const date = new Date();
  const dateStr = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
  
  return `${typeNames[spaceType]} ${dateStr}`;
}

/** 验证空间配置 */
export function validateSpaceConfig(config: VirtualSpaceConfig): string[] {
  const errors: string[] = [];
  
  if (config.maxCapacity < 1 || config.maxCapacity > 1000) {
    errors.push('最大容量必须在 1 到 1000 之间');
  }
  
  if (config.spatialAudioEnabled && !config.reverbSettings) {
    errors.push('启用空间音频时必须指定混响设置');
  }
  
  if (config.virtualAvatarEnabled && !config.avatarCustomizationLevel) {
    errors.push('启用虚拟化身时必须指定自定义级别');
  }
  
  if (config.arVrIntegrationEnabled && (!config.supportedVrDevices || config.supportedVrDevices.length === 0)) {
    errors.push('启用 AR/VR 集成时必须指定支持的设备');
  }
  
  return errors;
}

/** 创建空间创建请求 */
export function createSpaceRequest(
  spaceName: string,
  spaceType: VirtualSpaceType,
  hostUserId: string,
  config?: Partial<VirtualSpaceConfig>
): Record<string, any> {
  return {
    spaceName,
    spaceType,
    hostUserId,
    config: {
      ...DEFAULT_VIRTUAL_SPACE_CONFIG,
      ...config
    }
  };
}

/** 创建化身创建请求 */
export function createAvatarRequest(
  userId: string,
  avatarName: string,
  config?: Partial<VirtualAvatarInfo>
): Record<string, any> {
  return {
    userId,
    avatarName,
    config: {
      ...DEFAULT_AVATAR_CONFIG,
      ...config
    }
  };
}

// ==================== 类型守卫 ====================

/** 检查是否为有效的空间状态 */
export function isValidSpaceStatus(status: string): status is SpaceStatus {
  return Object.values(SpaceStatus).includes(status as SpaceStatus);
}

/** 检查是否为有效的访问控制模式 */
export function isValidAccessControlMode(mode: string): mode is AccessControlMode {
  return Object.values(AccessControlMode).includes(mode as AccessControlMode);
}

/** 检查是否为有效的虚拟空间类型 */
export function isValidVirtualSpaceType(type: string): type is VirtualSpaceType {
  return Object.values(VirtualSpaceType).includes(type as VirtualSpaceType);
}

// ==================== 事件类型 ====================

/** 虚拟空间事件 */
export interface VirtualSpaceEvent {
  eventId: string;
  spaceId: string;
  eventType: VirtualSpaceEventType;
  userId?: string;
  avatarId?: string;
  data?: Record<string, any>;
  timestamp: Date;
}

/** 虚拟空间事件类型 */
export enum VirtualSpaceEventType {
  USER_JOINED = 'USER_JOINED',
  USER_LEFT = 'USER_LEFT',
  SPACE_CREATED = 'SPACE_CREATED',
  SPACE_UPDATED = 'SPACE_UPDATED',
  SPACE_DELETED = 'SPACE_DELETED',
  AVATAR_CHANGED = 'AVATAR_CHANGED',
  AUDIO_CONFIG_CHANGED = 'AUDIO_CONFIG_CHANGED',
  SCENE_CHANGED = 'SCENE_CHANGED',
  COLLABORATION_STARTED = 'COLLABORATION_STARTED',
  COLLABORATION_ENDED = 'COLLABORATION_ENDED',
  OBJECT_INTERACTED = 'OBJECT_INTERACTED',
  VR_MODE_TOGGLED = 'VR_MODE_TOGGLED'
}

export default {
  VirtualSpaceType,
  SpaceStatus,
  AccessControlMode,
  EnvironmentStyle,
  ReverbSettings,
  AvatarCustomizationLevel,
  PhysicsEngine,
  LightingSystem,
  WeatherEffect,
  CollaborationTool,
  ObjectInteractionType,
  VrDeviceType,
  AvatarGender,
  AvatarRace,
  AvatarBodyType,
  AvatarClothingStyle,
  AvatarVoiceType,
  DEFAULT_VIRTUAL_SPACE_CONFIG,
  DEFAULT_AVATAR_CONFIG,
  SUPPORTED_VR_DEVICES,
  SUPPORTED_SCENE_TYPES,
  canJoinSpace,
  calculateUsageRate,
  isAvatarVrReady,
  formatSpaceDimensions,
  generateDefaultSpaceName,
  validateSpaceConfig,
  createSpaceRequest,
  createAvatarRequest,
  isValidSpaceStatus,
  isValidAccessControlMode,
  isValidVirtualSpaceType
};