import 'package:im_mobile/services/navigation/navigation_service.dart';

/// 导航配置
/// 
/// 包含导航系统的全局配置参数
class NavigationConfig {
  /// 默认导航模式
  static NavigationMode defaultMode = NavigationMode.driving;
  
  /// 默认导航服务商
  static NavigationProvider defaultProvider = NavigationProvider.amap;
  
  /// 是否开启语音播报
  static bool voiceEnabled = true;
  
  /// 语音播报音量（0.0-1.0）
  static double voiceVolume = 0.8;
  
  /// 是否自动重新规划路线
  static bool autoRecalculate = true;
  
  /// 偏离路线触发重新规划的距离（米）
  static double deviationThreshold = 100.0;
  
  /// 位置更新间隔（毫秒）
  static int locationUpdateInterval = 1000;
  
  /// 是否开启后台定位
  static bool backgroundLocationEnabled = true;
  
  /// 是否开启离线地图优先
  static bool offlineMapPriority = false;
  
  /// 地图缩放级别
  static double defaultZoomLevel = 15.0;
  
  /// 导航模式时的缩放级别
  static double navigationZoomLevel = 18.0;
  
  /// 是否显示实时路况
  static bool showTraffic = true;
  
  /// 是否显示3D建筑
  static bool show3DBuildings = true;
  
  /// 路线颜色
  static String routeColor = '#4A90E2';
  
  /// 备选路线颜色
  static String alternativeRouteColor = '#999999';
  
  /// 已行驶路线颜色
  static String passedRouteColor = '#B0D4F1';
  
  /// 路线线宽
  static double routeWidth = 12.0;
  
  /// 是否开启省电模式
  static bool powerSaveMode = false;
  
  /// 屏幕常亮
  static bool keepScreenOn = true;
  
  /// 夜间模式
  static NightModeType nightMode = NightModeType.auto;
  
  /// 白天模式开始时间
  static int dayModeStartHour = 6;
  
  /// 夜间模式开始时间
  static int nightModeStartHour = 19;
  
  /// 高德地图API密钥
  static String? amapApiKey;
  
  /// 腾讯地图API密钥
  static String? tencentMapApiKey;
  
  /// 百度地图API密钥
  static String? baiduMapApiKey;
  
  /// API请求超时时间（秒）
  static int apiTimeout = 10;
  
  /// 最大重试次数
  static int maxRetries = 3;
  
  /// 路线缓存有效期（分钟）
  static int routeCacheDuration = 30;
  
  /// 最大缓存路线数
  static int maxCachedRoutes = 50;

  /// 加载配置
  static Future<void> load() async {
    // 从本地存储加载配置
    // 实际实现需要从SharedPreferences或类似存储加载
  }

  /// 保存配置
  static Future<void> save() async {
    // 保存到本地存储
  }

  /// 重置为默认配置
  static void reset() {
    defaultMode = NavigationMode.driving;
    defaultProvider = NavigationProvider.amap;
    voiceEnabled = true;
    voiceVolume = 0.8;
    autoRecalculate = true;
    deviationThreshold = 100.0;
    locationUpdateInterval = 1000;
    backgroundLocationEnabled = true;
    offlineMapPriority = false;
    defaultZoomLevel = 15.0;
    navigationZoomLevel = 18.0;
    showTraffic = true;
    show3DBuildings = true;
    routeColor = '#4A90E2';
    alternativeRouteColor = '#999999';
    passedRouteColor = '#B0D4F1';
    routeWidth = 12.0;
    powerSaveMode = false;
    keepScreenOn = true;
    nightMode = NightModeType.auto;
    dayModeStartHour = 6;
    nightModeStartHour = 19;
    apiTimeout = 10;
    maxRetries = 3;
    routeCacheDuration = 30;
    maxCachedRoutes = 50;
  }

  /// 检查当前是否为夜间模式
  static bool get isNightMode {
    switch (nightMode) {
      case NightModeType.always:
        return true;
      case NightModeType.never:
        return false;
      case NightModeType.auto:
        final hour = DateTime.now().hour;
        return hour < dayModeStartHour || hour >= nightModeStartHour;
    }
  }

  /// 获取地图样式
  static String get mapStyle {
    return isNightMode ? 'night' : 'day';
  }

  /// 获取当前API密钥
  static String? getCurrentApiKey(NavigationProvider provider) {
    switch (provider) {
      case NavigationProvider.amap:
        return amapApiKey;
      case NavigationProvider.tencent:
        return tencentMapApiKey;
      case NavigationProvider.baidu:
        return baiduMapApiKey;
      case NavigationProvider.system:
        return null;
    }
  }
}

/// 夜间模式类型
enum NightModeType {
  auto,    // 自动
  always,  // 总是开启
  never,   // 从不开启
}

/// 导航语音配置
class NavigationVoiceConfig {
  /// 播报类型
  final VoiceType type;
  
  /// 播报时机
  final List<VoiceTrigger> triggers;
  
  /// 详细程度
  final VoiceDetailLevel detailLevel;
  
  /// 是否打断音乐
  final bool duckAudio;

  const NavigationVoiceConfig({
    this.type = VoiceType.standard,
    this.triggers = const [
      VoiceTrigger.turn,
      VoiceTrigger.deviation,
      VoiceTrigger.arrival,
    ],
    this.detailLevel = VoiceDetailLevel.normal,
    this.duckAudio = true,
  });

  static const defaultConfig = NavigationVoiceConfig();
}

/// 语音类型
enum VoiceType {
  standard,   // 标准
  gentle,     // 温柔
  energetic,  // 活力
  concise,    // 简洁
}

/// 语音触发时机
enum VoiceTrigger {
  turn,       // 转向时
  deviation,  // 偏离路线
  camera,     // 经过摄像头
  congestion, // 路况变化
  speedLimit, // 超速提醒
  arrival,    // 到达终点
  waypoint,   // 经过途经点
}

/// 语音详细程度
enum VoiceDetailLevel {
  minimal,  // 极简（只报转向）
  normal,   // 正常
  detailed, // 详细（包含路名）
}

/// 导航界面配置
class NavigationUIConfig {
  /// 显示模式
  final DisplayMode displayMode;
  
  /// 地图朝向
  final MapOrientation orientation;
  
  /// 是否显示指南针
  final bool showCompass;
  
  /// 是否显示比例尺
  final bool showScale;
  
  /// 是否显示缩放按钮
  final bool showZoomControls;
  
  /// 是否显示路况按钮
  final bool showTrafficButton;
  
  /// 面板透明度
  final double panelOpacity;

  const NavigationUIConfig({
    this.displayMode = DisplayMode.mapFirst,
    this.orientation = MapOrientation.directionUp,
    this.showCompass = true,
    this.showScale = true,
    this.showZoomControls = false,
    this.showTrafficButton = true,
    this.panelOpacity = 0.9,
  });

  static const defaultConfig = NavigationUIConfig();
}

/// 显示模式
enum DisplayMode {
  mapFirst,    // 地图优先
  listFirst,   // 列表优先
  split,       // 分屏
}

/// 地图朝向
enum MapOrientation {
  northUp,      // 正北朝上
  directionUp,  // 行进方向朝上
}
