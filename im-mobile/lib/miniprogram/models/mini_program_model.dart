/// 小程序模型定义

/// 小程序清单模型
class MiniProgramManifest {
  final String appId;
  final String name;
  final String version;
  final String description;
  final String entryPath;
  final String codeBasePath;
  final List<String> pages;
  final String rootPage;
  final TabBarConfig? tabBar;
  final WindowConfig? window;
  final NetworkConfig? network;
  final PermissionConfig? permission;
  final Map<String, String>? dependencies;
  final CompileConfig? compile;
  final CloudConfig? cloud;
  final Map<String, PluginConfig>? plugins;
  final List<PreloadRule>? preloadRules;
  final List<SubPackage>? subPackages;
  final RuntimeConfig? runtime;
  
  MiniProgramManifest({
    required this.appId,
    required this.name,
    this.version = '1.0.0',
    this.description = '',
    this.entryPath = 'pages/index/index',
    this.codeBasePath = '',
    this.pages = const [],
    this.rootPage = 'pages/index/index',
    this.tabBar,
    this.window,
    this.network,
    this.permission,
    this.dependencies,
    this.compile,
    this.cloud,
    this.plugins,
    this.preloadRules,
    this.subPackages,
    this.runtime,
  });
  
  factory MiniProgramManifest.fromJson(Map<String, dynamic> json) {
    return MiniProgramManifest(
      appId: json['appId'] ?? '',
      name: json['name'] ?? '',
      version: json['version'] ?? '1.0.0',
      description: json['description'] ?? '',
      entryPath: json['entryPath'] ?? 'pages/index/index',
      codeBasePath: json['codeBasePath'] ?? '',
      pages: List<String>.from(json['pages'] ?? []),
      rootPage: json['rootPage'] ?? 'pages/index/index',
      tabBar: json['tabBar'] != null ? TabBarConfig.fromJson(json['tabBar']) : null,
      window: json['window'] != null ? WindowConfig.fromJson(json['window']) : null,
      network: json['network'] != null ? NetworkConfig.fromJson(json['network']) : null,
      permission: json['permission'] != null ? PermissionConfig.fromJson(json['permission']) : null,
      dependencies: Map<String, String>.from(json['dependencies'] ?? {}),
      compile: json['compile'] != null ? CompileConfig.fromJson(json['compile']) : null,
      runtime: json['runtime'] != null ? RuntimeConfig.fromJson(json['runtime']) : null,
    );
  }
  
  Map<String, dynamic> toJson() => {
    'appId': appId,
    'name': name,
    'version': version,
    'description': description,
    'entryPath': entryPath,
    'codeBasePath': codeBasePath,
    'pages': pages,
    'rootPage': rootPage,
    'tabBar': tabBar?.toJson(),
    'window': window?.toJson(),
    'network': network?.toJson(),
    'permission': permission?.toJson(),
    'dependencies': dependencies,
    'compile': compile?.toJson(),
    'runtime': runtime?.toJson(),
  };
}

/// TabBar配置
class TabBarConfig {
  String color = '#999999';
  String selectedColor = '#333333';
  String backgroundColor = '#ffffff';
  String borderStyle = 'black';
  List<TabItem> list = [];
  
  TabBarConfig({
    this.color = '#999999',
    this.selectedColor = '#333333',
    this.backgroundColor = '#ffffff',
    this.borderStyle = 'black',
    this.list = const [],
  });
  
  factory TabBarConfig.fromJson(Map<String, dynamic> json) {
    return TabBarConfig(
      color: json['color'] ?? '#999999',
      selectedColor: json['selectedColor'] ?? '#333333',
      backgroundColor: json['backgroundColor'] ?? '#ffffff',
      borderStyle: json['borderStyle'] ?? 'black',
      list: (json['list'] as List? ?? [])
        .map((e) => TabItem.fromJson(e))
        .toList(),
    );
  }
  
  Map<String, dynamic> toJson() => {
    'color': color,
    'selectedColor': selectedColor,
    'backgroundColor': backgroundColor,
    'borderStyle': borderStyle,
    'list': list.map((e) => e.toJson()).toList(),
  };
}

class TabItem {
  String pagePath = '';
  String text = '';
  String? iconPath;
  String? selectedIconPath;
  
  TabItem({
    this.pagePath = '',
    this.text = '',
    this.iconPath,
    this.selectedIconPath,
  });
  
  factory TabItem.fromJson(Map<String, dynamic> json) {
    return TabItem(
      pagePath: json['pagePath'] ?? '',
      text: json['text'] ?? '',
      iconPath: json['iconPath'],
      selectedIconPath: json['selectedIconPath'],
    );
  }
  
  Map<String, dynamic> toJson() => {
    'pagePath': pagePath,
    'text': text,
    'iconPath': iconPath,
    'selectedIconPath': selectedIconPath,
  };
}

/// 窗口配置
class WindowConfig {
  String navigationBarTitleText = '';
  String navigationBarTextStyle = 'black';
  String navigationBarBackgroundColor = '#ffffff';
  bool navigationStyle = false;
  String backgroundColor = '#ffffff';
  String backgroundTextStyle = 'dark';
  bool enablePullDownRefresh = false;
  int onReachBottomDistance = 50;
  
  WindowConfig({
    this.navigationBarTitleText = '',
    this.navigationBarTextStyle = 'black',
    this.navigationBarBackgroundColor = '#ffffff',
    this.navigationStyle = false,
    this.backgroundColor = '#ffffff',
    this.backgroundTextStyle = 'dark',
    this.enablePullDownRefresh = false,
    this.onReachBottomDistance = 50,
  });
  
  factory WindowConfig.fromJson(Map<String, dynamic> json) {
    return WindowConfig(
      navigationBarTitleText: json['navigationBarTitleText'] ?? '',
      navigationBarTextStyle: json['navigationBarTextStyle'] ?? 'black',
      navigationBarBackgroundColor: json['navigationBarBackgroundColor'] ?? '#ffffff',
      navigationStyle: json['navigationStyle'] ?? false,
      backgroundColor: json['backgroundColor'] ?? '#ffffff',
      backgroundTextStyle: json['backgroundTextStyle'] ?? 'dark',
      enablePullDownRefresh: json['enablePullDownRefresh'] ?? false,
      onReachBottomDistance: json['onReachBottomDistance'] ?? 50,
    );
  }
  
  Map<String, dynamic> toJson() => {
    'navigationBarTitleText': navigationBarTitleText,
    'navigationBarTextStyle': navigationBarTextStyle,
    'navigationBarBackgroundColor': navigationBarBackgroundColor,
    'navigationStyle': navigationStyle,
    'backgroundColor': backgroundColor,
    'backgroundTextStyle': backgroundTextStyle,
    'enablePullDownRefresh': enablePullDownRefresh,
    'onReachBottomDistance': onReachBottomDistance,
  };
}

/// 网络配置
class NetworkConfig {
  List<String> requestDomain = [];
  List<String> uploadDomain = [];
  List<String> downloadDomain = [];
  List<String> socketDomain = [];
  bool debug = false;
  
  NetworkConfig({
    this.requestDomain = const [],
    this.uploadDomain = const [],
    this.downloadDomain = const [],
    this.socketDomain = const [],
    this.debug = false,
  });
  
  factory NetworkConfig.fromJson(Map<String, dynamic> json) {
    return NetworkConfig(
      requestDomain: List<String>.from(json['requestDomain'] ?? []),
      uploadDomain: List<String>.from(json['uploadDomain'] ?? []),
      downloadDomain: List<String>.from(json['downloadDomain'] ?? []),
      socketDomain: List<String>.from(json['socketDomain'] ?? []),
      debug: json['debug'] ?? false,
    );
  }
  
  Map<String, dynamic> toJson() => {
    'requestDomain': requestDomain,
    'uploadDomain': uploadDomain,
    'downloadDomain': downloadDomain,
    'socketDomain': socketDomain,
    'debug': debug,
  };
}

/// 权限配置
class PermissionConfig {
  Map<String, PermissionScope> scopes = {};
  
  PermissionConfig({this.scopes = const {}});
  
  factory PermissionConfig.fromJson(Map<String, dynamic> json) {
    final scopes = <String, PermissionScope>{};
    json['scopes']?.forEach((key, value) {
      scopes[key] = PermissionScope.fromJson(value);
    });
    return PermissionConfig(scopes: scopes);
  }
  
  Map<String, dynamic> toJson() => {
    'scopes': scopes.map((k, v) => MapEntry(k, v.toJson())),
  };
}

class PermissionScope {
  String desc = '';
  
  PermissionScope({this.desc = ''});
  
  factory PermissionScope.fromJson(Map<String, dynamic> json) {
    return PermissionScope(desc: json['desc'] ?? '');
  }
  
  Map<String, dynamic> toJson() => {'desc': desc};
}

/// 编译配置
class CompileConfig {
  List<String> babelSetting = [];
  bool es6 = true;
  bool enhance = true;
  bool postcss = true;
  bool minified = true;
  bool uglifyFileName = false;
  
  CompileConfig({
    this.babelSetting = const [],
    this.es6 = true,
    this.enhance = true,
    this.postcss = true,
    this.minified = true,
    this.uglifyFileName = false,
  });
  
  factory CompileConfig.fromJson(Map<String, dynamic> json) {
    return CompileConfig(
      babelSetting: List<String>.from(json['babelSetting'] ?? []),
      es6: json['es6'] ?? true,
      enhance: json['enhance'] ?? true,
      postcss: json['postcss'] ?? true,
      minified: json['minified'] ?? true,
      uglifyFileName: json['uglifyFileName'] ?? false,
    );
  }
  
  Map<String, dynamic> toJson() => {
    'babelSetting': babelSetting,
    'es6': es6,
    'enhance': enhance,
    'postcss': postcss,
    'minified': minified,
    'uglifyFileName': uglifyFileName,
  };
}

/// 云开发配置
class CloudConfig {
  String root = './cloud/';
  
  CloudConfig({this.root = './cloud/'});
  
  factory CloudConfig.fromJson(Map<String, dynamic> json) {
    return CloudConfig(root: json['root'] ?? './cloud/');
  }
  
  Map<String, dynamic> toJson() => {'root': root};
}

/// 插件配置
class PluginConfig {
  String version = '';
  String provider = '';
  
  PluginConfig({this.version = '', this.provider = ''});
  
  factory PluginConfig.fromJson(Map<String, dynamic> json) {
    return PluginConfig(
      version: json['version'] ?? '',
      provider: json['provider'] ?? '',
    );
  }
  
  Map<String, dynamic> toJson() => {
    'version': version,
    'provider': provider,
  };
}

/// 预加载规则
class PreloadRule {
  String path = '';
  List<String> packages = [];
  bool network = true;
  
  PreloadRule({
    this.path = '',
    this.packages = const [],
    this.network = true,
  });
  
  factory PreloadRule.fromJson(Map<String, dynamic> json) {
    return PreloadRule(
      path: json['path'] ?? '',
      packages: List<String>.from(json['packages'] ?? []),
      network: json['network'] ?? true,
    );
  }
  
  Map<String, dynamic> toJson() => {
    'path': path,
    'packages': packages,
    'network': network,
  };
}

/// 分包配置
class SubPackage {
  String root = '';
  List<String> pages = [];
  String? name;
  bool independent = false;
  
  SubPackage({
    this.root = '',
    this.pages = const [],
    this.name,
    this.independent = false,
  });
  
  factory SubPackage.fromJson(Map<String, dynamic> json) {
    return SubPackage(
      root: json['root'] ?? '',
      pages: List<String>.from(json['pages'] ?? []),
      name: json['name'],
      independent: json['independent'] ?? false,
    );
  }
  
  Map<String, dynamic> toJson() => {
    'root': root,
    'pages': pages,
    'name': name,
    'independent': independent,
  };
}

/// 运行时配置
class RuntimeConfig {
  int maxMemory = 256 * 1024 * 1024; // 256MB
  int maxThreads = 20;
  int maxExecutionTime = 5 * 60 * 1000; // 5分钟
  int idleTimeout = 30 * 60 * 1000; // 30分钟
  bool debug = false;
  
  RuntimeConfig({
    this.maxMemory = 268435456,
    this.maxThreads = 20,
    this.maxExecutionTime = 300000,
    this.idleTimeout = 1800000,
    this.debug = false,
  });
  
  factory RuntimeConfig.fromJson(Map<String, dynamic> json) {
    return RuntimeConfig(
      maxMemory: json['maxMemory'] ?? 268435456,
      maxThreads: json['maxThreads'] ?? 20,
      maxExecutionTime: json['maxExecutionTime'] ?? 300000,
      idleTimeout: json['idleTimeout'] ?? 1800000,
      debug: json['debug'] ?? false,
    );
  }
  
  Map<String, dynamic> toJson() => {
    'maxMemory': maxMemory,
    'maxThreads': maxThreads,
    'maxExecutionTime': maxExecutionTime,
    'idleTimeout': idleTimeout,
    'debug': debug,
  };
}
