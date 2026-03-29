import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;

/// 主题模式枚举
enum ThemeModeType { light, dark, system, custom }

/// 暗黑模式配置模型
class DarkModeConfig {
  final int? id;
  final String? userId;
  final ThemeModeType themeMode;
  final bool isActive;
  final String? configName;
  final String? primaryColor;
  final String? backgroundColor;
  final String? textColor;
  final String? secondaryTextColor;
  final String? accentColor;
  final String? controlColor;
  final String? borderColor;
  final String? hoverColor;
  final bool? useSystemColors;
  final double? opacityLevel;
  final double? fontScaleFactor;
  final bool? highContrast;
  final bool? reduceMotion;
  final bool? nightProtection;
  final DateTime? createdAt;
  final DateTime? updatedAt;
  final DateTime? lastSyncedAt;
  final String? deviceId;
  final int? configVersion;
  final bool? autoSwitchEnabled;
  final String? autoSwitchStart;
  final String? autoSwitchEnd;

  DarkModeConfig({
    this.id,
    this.userId,
    required this.themeMode,
    this.isActive = true,
    this.configName,
    this.primaryColor,
    this.backgroundColor,
    this.textColor,
    this.secondaryTextColor,
    this.accentColor,
    this.controlColor,
    this.borderColor,
    this.hoverColor,
    this.useSystemColors,
    this.opacityLevel,
    this.fontScaleFactor,
    this.highContrast,
    this.reduceMotion,
    this.nightProtection,
    this.createdAt,
    this.updatedAt,
    this.lastSyncedAt,
    this.deviceId,
    this.configVersion,
    this.autoSwitchEnabled,
    this.autoSwitchStart,
    this.autoSwitchEnd,
  });

  factory DarkModeConfig.fromJson(Map<String, dynamic> json) {
    return DarkModeConfig(
      id: json['id'],
      userId: json['userId'],
      themeMode: _parseThemeMode(json['themeMode']),
      isActive: json['isActive'] ?? true,
      configName: json['configName'],
      primaryColor: json['primaryColor'],
      backgroundColor: json['backgroundColor'],
      textColor: json['textColor'],
      secondaryTextColor: json['secondaryTextColor'],
      accentColor: json['accentColor'],
      controlColor: json['controlColor'],
      borderColor: json['borderColor'],
      hoverColor: json['hoverColor'],
      useSystemColors: json['useSystemColors'],
      opacityLevel: json['opacityLevel']?.toDouble(),
      fontScaleFactor: json['fontScaleFactor']?.toDouble(),
      highContrast: json['highContrast'],
      reduceMotion: json['reduceMotion'],
      nightProtection: json['nightProtection'],
      createdAt: json['createdAt'] != null ? DateTime.parse(json['createdAt']) : null,
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt']) : null,
      lastSyncedAt: json['lastSyncedAt'] != null ? DateTime.parse(json['lastSyncedAt']) : null,
      deviceId: json['deviceId'],
      configVersion: json['configVersion'],
      autoSwitchEnabled: json['autoSwitchEnabled'],
      autoSwitchStart: json['autoSwitchStart'],
      autoSwitchEnd: json['autoSwitchEnd'],
    );
  }

  static ThemeModeType _parseThemeMode(String? mode) {
    switch (mode) {
      case 'DARK':
        return ThemeModeType.dark;
      case 'SYSTEM':
        return ThemeModeType.system;
      case 'CUSTOM':
        return ThemeModeType.custom;
      default:
        return ThemeModeType.light;
    }
  }

  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      if (userId != null) 'userId': userId,
      'themeMode': themeMode.name.toUpperCase(),
      'isActive': isActive,
      if (configName != null) 'configName': configName,
      if (primaryColor != null) 'primaryColor': primaryColor,
      if (backgroundColor != null) 'backgroundColor': backgroundColor,
      if (textColor != null) 'textColor': textColor,
      if (secondaryTextColor != null) 'secondaryTextColor': secondaryTextColor,
      if (accentColor != null) 'accentColor': accentColor,
      if (controlColor != null) 'controlColor': controlColor,
      if (borderColor != null) 'borderColor': borderColor,
      if (hoverColor != null) 'hoverColor': hoverColor,
      if (useSystemColors != null) 'useSystemColors': useSystemColors,
      if (opacityLevel != null) 'opacityLevel': opacityLevel,
      if (fontScaleFactor != null) 'fontScaleFactor': fontScaleFactor,
      if (highContrast != null) 'highContrast': highContrast,
      if (reduceMotion != null) 'reduceMotion': reduceMotion,
      if (nightProtection != null) 'nightProtection': nightProtection,
      if (autoSwitchEnabled != null) 'autoSwitchEnabled': autoSwitchEnabled,
      if (autoSwitchStart != null) 'autoSwitchStart': autoSwitchStart,
      if (autoSwitchEnd != null) 'autoSwitchEnd': autoSwitchEnd,
    };
  }
}

/// 默认主题颜色
class DefaultThemeColors {
  static const Map<ThemeModeType, Map<String, String>> colors = {
    ThemeModeType.light: {
      'primary': '#2196F3',
      'background': '#FFFFFF',
      'text': '#212121',
      'textSecondary': '#757575',
      'accent': '#FF4081',
      'control': '#E0E0E0',
      'border': '#BDBDBD',
      'hover': '#F5F5F5',
    },
    ThemeModeType.dark: {
      'primary': '#90CAF9',
      'background': '#121212',
      'text': '#E0E0E0',
      'textSecondary': '#AAAAAA',
      'accent': '#FF80AB',
      'control': '#424242',
      'border': '#616161',
      'hover': '#2A2A2A',
    },
    ThemeModeType.custom: {
      'primary': '#4CAF50',
      'background': '#FAFAFA',
      'text': '#263238',
      'textSecondary': '#78909C',
      'accent': '#FF9800',
      'control': '#CFD8DC',
      'border': '#B0BEC5',
      'hover': '#ECEFF1',
    },
  };
}

/// 暗黑模式服务类
class DarkModeService extends ChangeNotifier {
  static final DarkModeService _instance = DarkModeService._internal();
  factory DarkModeService() => _instance;
  DarkModeService._internal();

  static DarkModeService get instance => _instance;

  final String _apiBaseUrl = '/api/v1/dark-mode';
  
  DarkModeConfig? _currentConfig;
  String? _userId;
  String? _deviceId;
  ThemeModeType _currentThemeMode = ThemeModeType.system;
  
  // Getter
  DarkModeConfig? get currentConfig => _currentConfig;
  String? get userId => _userId;
  ThemeModeType get currentThemeMode => _currentThemeMode;
  bool get isDarkMode => _currentThemeMode == ThemeModeType.dark;

  /// 初始化服务
  Future<void> initialize() async {
    // 加载设备 ID
    final prefs = await SharedPreferences.getInstance();
    _deviceId = prefs.getString('darkmode_device_id');
    if (_deviceId == null) {
      _deviceId = 'mobile_${DateTime.now().millisecondsSinceEpoch}';
      await prefs.setString('darkmode_device_id', _deviceId!);
    }

    // 加载用户 ID
    _userId = prefs.getString('current_user_id');

    // 加载保存的主题
    final savedTheme = prefs.getString('darkmode_theme');
    if (savedTheme != null) {
      _currentThemeMode = _parseThemeMode(savedTheme);
    }

    // 加载保存的配置
    final savedConfig = prefs.getString('darkmode_config');
    if (savedConfig != null) {
      try {
        _currentConfig = DarkModeConfig.fromJson(json.decode(savedConfig));
      } catch (e) {
        debugPrint('解析配置失败：$e');
      }
    }

    // 如果没有配置，创建默认配置
    if (_currentConfig == null && _userId != null) {
      await createDefaultConfig();
    }

    // 监听系统主题变化
    if (_currentThemeMode == ThemeModeType.system) {
      // 会在 build 时处理系统主题
    }

    notifyListeners();
  }

  ThemeModeType _parseThemeMode(String mode) {
    switch (mode.toLowerCase()) {
      case 'dark':
        return ThemeModeType.dark;
      case 'system':
        return ThemeModeType.system;
      case 'custom':
        return ThemeModeType.custom;
      default:
        return ThemeModeType.light;
    }
  }

  /// 设置用户 ID
  Future<void> setUserId(String? userId) async {
    _userId = userId;
    final prefs = await SharedPreferences.getInstance();
    if (userId != null) {
      await prefs.setString('current_user_id', userId);
    } else {
      await prefs.remove('current_user_id');
    }
    notifyListeners();
  }

  /// 获取设备 ID
  String? getDeviceId() => _deviceId;

  /// HTTP 请求辅助方法
  Future<Map<String, dynamic>> _request(
    String endpoint, {
    String method = 'GET',
    Map<String, dynamic>? params,
    Map<String, dynamic>? body,
  }) async {
    final uri = Uri.parse('$_apiBaseUrl$endpoint');
    
    final queryParams = <String, String>{};
    if (params != null) {
      params.forEach((key, value) {
        queryParams[key] = value.toString();
      });
    }
    
    final response = await http.Request(
      method,
      uri.replace(queryParameters: queryParams.isNotEmpty ? queryParams : null),
    );
    
    if (body != null && method != 'GET') {
      response.headers['Content-Type'] = 'application/json';
      response.body = json.encode(body);
    }

    final streamedResponse = await response.send();
    final responseBody = await streamedResponse.stream.bytesToString();
    
    if (streamedResponse.statusCode >= 200 && streamedResponse.statusCode < 300) {
      return json.decode(responseBody);
    } else {
      throw Exception('HTTP ${streamedResponse.statusCode}: $responseBody');
    }
  }

  /// 获取活跃配置
  Future<DarkModeConfig?> getActiveConfig() async {
    if (_userId == null) return null;

    try {
      final response = await _request('/active', params: {'userId': _userId!});
      if (response['success'] == true && response['data'] != null) {
        _currentConfig = DarkModeConfig.fromJson(response['data']);
        await _applyTheme(_currentConfig!);
        notifyListeners();
        return _currentConfig;
      }
    } catch (e) {
      debugPrint('获取活跃配置失败：$e');
    }
    return null;
  }

  /// 获取所有配置
  Future<List<DarkModeConfig>> getUserConfigs() async {
    if (_userId == null) return [];

    try {
      final response = await _request('/list', params: {'userId': _userId!});
      if (response['success'] == true && response['data'] != null) {
        final List<dynamic> data = response['data'];
        return data.map((item) => DarkModeConfig.fromJson(item)).toList();
      }
    } catch (e) {
      debugPrint('获取配置列表失败：$e');
    }
    return [];
  }

  /// 创建默认配置
  Future<DarkModeConfig?> createDefaultConfig() async {
    if (_userId == null) throw Exception('未设置用户 ID');

    try {
      final response = await _request(
        '/create-default',
        method: 'POST',
        params: {
          'userId': _userId!,
          'platform': 'MOBILE',
          if (_deviceId != null) 'deviceId': _deviceId!,
        },
      );
      
      if (response['success'] == true && response['data'] != null) {
        _currentConfig = DarkModeConfig.fromJson(response['data']);
        await _applyTheme(_currentConfig!);
        notifyListeners();
        return _currentConfig;
      }
    } catch (e) {
      debugPrint('创建默认配置失败：$e');
      rethrow;
    }
    return null;
  }

  /// 创建自定义配置
  Future<DarkModeConfig?> createCustomConfig({
    required ThemeModeType themeMode,
    Map<String, String>? customColors,
  }) async {
    if (_userId == null) throw Exception('未设置用户 ID');

    try {
      final response = await _request(
        '/create-custom',
        method: 'POST',
        params: {
          'userId': _userId!,
          'themeMode': themeMode.name.toUpperCase(),
          'platform': 'MOBILE',
          if (_deviceId != null) 'deviceId': _deviceId!,
        },
        body: customColors,
      );
      
      if (response['success'] == true && response['data'] != null) {
        return DarkModeConfig.fromJson(response['data']);
      }
    } catch (e) {
      debugPrint('创建自定义配置失败：$e');
      rethrow;
    }
    return null;
  }

  /// 切换主题模式
  Future<bool> switchThemeMode(ThemeModeType newThemeMode) async {
    if (_userId == null) throw Exception('未设置用户 ID');

    try {
      final response = await _request(
        '/switch-theme',
        method: 'PUT',
        params: {
          'userId': _userId!,
          'newThemeMode': newThemeMode.name.toUpperCase(),
        },
      );
      
      if (response['success'] == true && response['data'] != null) {
        _currentConfig = DarkModeConfig.fromJson(response['data']);
        _currentThemeMode = newThemeMode;
        await _applyTheme(_currentConfig!);
        
        final prefs = await SharedPreferences.getInstance();
        await prefs.setString('darkmode_theme', newThemeMode.name.toUpperCase());
        
        notifyListeners();
        return true;
      }
    } catch (e) {
      debugPrint('切换主题模式失败：$e');
      return false;
    }
    return false;
  }

  /// 激活指定配置
  Future<bool> activateConfig(int configId) async {
    if (_userId == null) throw Exception('未设置用户 ID');

    try {
      final response = await _request(
        '/activate',
        method: 'PUT',
        params: {
          'userId': _userId!,
          'configId': configId,
        },
      );
      
      if (response['success'] == true) {
        await getActiveConfig();
        return true;
      }
    } catch (e) {
      debugPrint('激活配置失败：$e');
    }
    return false;
  }

  /// 更新自定义颜色
  Future<bool> updateCustomColors(Map<String, String> colors) async {
    if (_userId == null) throw Exception('未设置用户 ID');

    try {
      final response = await _request(
        '/update-colors',
        method: 'PUT',
        params: {'userId': _userId!},
        body: colors,
      );
      
      if (response['success'] == true && response['data'] != null) {
        _currentConfig = DarkModeConfig.fromJson(response['data']);
        _currentThemeMode = ThemeModeType.custom;
        await _applyTheme(_currentConfig!);
        notifyListeners();
        return true;
      }
    } catch (e) {
      debugPrint('更新颜色失败：$e');
      return false;
    }
    return false;
  }

  /// 切换高对比度模式
  Future<bool> toggleHighContrast(bool enabled) async {
    if (_userId == null) return false;

    try {
      final response = await _request(
        '/toggle-high-contrast',
        method: 'PUT',
        params: {
          'userId': _userId!,
          'enabled': enabled,
        },
      );
      return response['success'] == true;
    } catch (e) {
      debugPrint('切换高对比度失败：$e');
      return false;
    }
  }

  /// 切换减少动画
  Future<bool> toggleReduceMotion(bool enabled) async {
    if (_userId == null) return false;

    try {
      final response = await _request(
        '/toggle-reduce-motion',
        method: 'PUT',
        params: {
          'userId': _userId!,
          'enabled': enabled,
        },
      );
      return response['success'] == true;
    } catch (e) {
      debugPrint('切换减少动画失败：$e');
      return false;
    }
  }

  /// 切换夜间保护
  Future<bool> toggleNightProtection(bool enabled) async {
    if (_userId == null) return false;

    try {
      final response = await _request(
        '/toggle-night-protection',
        method: 'PUT',
        params: {
          'userId': _userId!,
          'enabled': enabled,
        },
      );
      return response['success'] == true;
    } catch (e) {
      debugPrint('切换夜间保护失败：$e');
      return false;
    }
  }

  /// 配置自动切换
  Future<bool> configureAutoSwitch({
    required bool enabled,
    String? startTime,
    String? endTime,
  }) async {
    if (_userId == null) return false;

    try {
      final response = await _request(
        '/configure-auto-switch',
        method: 'PUT',
        params: {
          'userId': _userId!,
          'enabled': enabled,
          if (startTime != null) 'startTime': startTime,
          if (endTime != null) 'endTime': endTime,
        },
      );
      return response['success'] == true;
    } catch (e) {
      debugPrint('配置自动切换失败：$e');
      return false;
    }
  }

  /// 更新透明度
  Future<bool> updateOpacity(double opacityLevel) async {
    if (_userId == null) return false;

    try {
      final response = await _request(
        '/update-opacity',
        method: 'PUT',
        params: {
          'userId': _userId!,
          'opacityLevel': opacityLevel,
        },
      );
      return response['success'] == true;
    } catch (e) {
      debugPrint('更新透明度失败：$e');
      return false;
    }
  }

  /// 更新字体缩放
  Future<bool> updateFontScale(double fontScaleFactor) async {
    if (_userId == null) return false;

    try {
      final response = await _request(
        '/update-font-scale',
        method: 'PUT',
        params: {
          'userId': _userId!,
          'fontScaleFactor': fontScaleFactor,
        },
      );
      return response['success'] == true;
    } catch (e) {
      debugPrint('更新字体缩放失败：$e');
      return false;
    }
  }

  /// 同步配置到设备
  Future<bool> syncToDevice(int configId, String deviceId) async {
    if (_userId == null) return false;

    try {
      final response = await _request(
        '/sync-to-device',
        method: 'POST',
        params: {
          'userId': _userId!,
          'configId': configId,
          'deviceId': deviceId,
        },
      );
      return response['success'] == true;
    } catch (e) {
      debugPrint('同步配置失败：$e');
      return false;
    }
  }

  /// 删除配置
  Future<bool> deleteConfig(int configId) async {
    if (_userId == null) return false;

    try {
      final response = await _request(
        '/delete',
        method: 'DELETE',
        params: {
          'userId': _userId!,
          'configId': configId,
        },
      );
      return response['success'] == true;
    } catch (e) {
      debugPrint('删除配置失败：$e');
      return false;
    }
  }

  /// 应用主题
  Future<void> _applyTheme(DarkModeConfig config) async {
    final prefs = await SharedPreferences.getInstance();
    
    // 保存配置到本地
    await prefs.setString('darkmode_config', json.encode(config.toJson()));
    
    // 根据主题模式设置
    if (config.themeMode == ThemeModeType.dark) {
      // 暗黑模式
    } else if (config.themeMode == ThemeModeType.light) {
      // 明亮模式
    }
    // system 模式会在 build 时自动处理
    
    // 应用特殊效果
    // 高对比度、减少动画等可以通过 Theme 配置实现
  }

  /// 获取 Flutter ThemeData
  ThemeData getThemeData(BuildContext context, bool isSystemDark) {
    ThemeModeType effectiveMode = _currentThemeMode;
    if (effectiveMode == ThemeModeType.system) {
      effectiveMode = isSystemDark ? ThemeModeType.dark : ThemeModeType.light;
    }

    final colors = DefaultThemeColors.colors[effectiveMode] ?? DefaultThemeColors.colors[ThemeModeType.light]!;

    return ThemeData(
      brightness: effectiveMode == ThemeModeType.dark ? Brightness.dark : Brightness.light,
      primaryColor: Color(int.parse(colors['primary']!.substring(1), radix: 16) + 0xFF000000),
      scaffoldBackgroundColor: Color(int.parse(colors['background']!.substring(1), radix: 16) + 0xFF000000),
      textTheme: TextTheme(
        bodyLarge: TextStyle(
          color: Color(int.parse(colors['text']!.substring(1), radix: 16) + 0xFF000000),
        ),
        bodyMedium: TextStyle(
          color: Color(int.parse(colors['textSecondary']!.substring(1), radix: 16) + 0xFF000000),
        ),
      ),
      colorScheme: ColorScheme.fromSeed(
        seedColor: Color(int.parse(colors['primary']!.substring(1), radix: 16) + 0xFF000000),
        brightness: effectiveMode == ThemeModeType.dark ? Brightness.dark : Brightness.light,
      ),
    );
  }

  /// 检查是否应该自动切换
  Future<bool> checkAutoSwitch() async {
    if (_userId == null) return false;

    try {
      final response = await _request(
        '/check-auto-switch',
        params: {'userId': _userId!},
      );
      
      if (response['success'] == true && response['data'] != null) {
        return response['data']['shouldSwitchToDark'] ?? false;
      }
    } catch (e) {
      debugPrint('检查自动切换失败：$e');
    }
    return false;
  }

  /// 获取统计信息
  Future<Map<String, dynamic>?> getStatistics() async {
    try {
      final response = await _request('/statistics');
      if (response['success'] == true) {
        return response['data'];
      }
    } catch (e) {
      debugPrint('获取统计信息失败：$e');
    }
    return null;
  }

  /// 导出配置
  Future<String?> exportConfig(int configId) async {
    if (_userId == null) return null;

    try {
      final response = await _request(
        '/export',
        params: {
          'userId': _userId!,
          'configId': configId,
        },
      );
      
      if (response['success'] == true) {
        return response['data'];
      }
    } catch (e) {
      debugPrint('导出配置失败：$e');
    }
    return null;
  }

  /// 清理配置
  Future<int> cleanup() async {
    try {
      final response = await _request('/cleanup', method: 'POST');
      if (response['success'] == true && response['data'] != null) {
        return response['data']['cleanedCount'] ?? 0;
      }
    } catch (e) {
      debugPrint('清理配置失败：$e');
    }
    return 0;
  }
}
