import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';
import 'package:flutter/services.dart';

import '../models/mini_program_model.dart';
import '../bridge/mini_program_bridge.dart';
import '../parser/dsl_parser.dart';
import '../components/mini_component_registry.dart';

/// 小程序渲染引擎
/// 负责解析DSL、渲染UI、处理用户交互
class MiniProgramRenderEngine extends ChangeNotifier {
  
  final String appId;
  final MiniProgramManifest manifest;
  final MiniProgramBridge bridge;
  
  late final WebViewController _webViewController;
  late final DSLParser _dslParser;
  
  // 渲染状态
  RenderState _state = RenderState.idle;
  String? _currentPage;
  Map<String, dynamic>? _pageData;
  
  // 组件树
  Widget? _rootWidget;
  final Map<String, Widget> _componentCache = {};
  
  // 事件通道
  final _eventController = StreamController<RenderEvent>.broadcast();
  Stream<RenderEvent> get events => _eventController.stream;
  
  // 性能统计
  final _performanceStats = RenderPerformanceStats();
  
  RenderState get state => _state;
  Widget? get rootWidget => _rootWidget;
  String? get currentPage => _currentPage;
  RenderPerformanceStats get performanceStats => _performanceStats;
  
  MiniProgramRenderEngine({
    required this.appId,
    required this.manifest,
    required this.bridge,
  }) {
    _dslParser = DSLParser();
    _initializeWebView();
  }
  
  /// 初始化WebView
  void _initializeWebView() {
    _webViewController = WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setBackgroundColor(const Color(0x00000000))
      ..setNavigationDelegate(
        NavigationDelegate(
          onProgress: (int progress) {
            // 加载进度
          },
          onPageStarted: (String url) {
            _state = RenderState.loading;
            notifyListeners();
          },
          onPageFinished: (String url) {
            _state = RenderState.ready;
            notifyListeners();
          },
          onWebResourceError: (WebResourceError error) {
            _state = RenderState.error;
            _eventController.add(RenderEvent.error('WebView error: ${error.description}'));
            notifyListeners();
          },
        ),
      )
      ..addJavaScriptChannel(
        'MiniProgramBridge',
        onMessageReceived: (JavaScriptMessage message) {
          _handleBridgeMessage(message.message);
        },
      );
  }
  
  /// 加载页面
  Future<void> loadPage(String pagePath, {Map<String, dynamic>? params}) async {
    final stopwatch = Stopwatch()..start();
    
    _state = RenderState.loading;
    _currentPage = pagePath;
    notifyListeners();
    
    try {
      // 1. 加载DSL文件
      final dslContent = await _loadDSLFile(pagePath);
      
      // 2. 解析DSL
      final parseStart = stopwatch.elapsedMilliseconds;
      final dslTree = _dslParser.parse(dslContent);
      _performanceStats.parseTime = stopwatch.elapsedMilliseconds - parseStart;
      
      // 3. 加载数据
      _pageData = await _loadPageData(pagePath, params);
      
      // 4. 构建组件树
      final buildStart = stopwatch.elapsedMilliseconds;
      _rootWidget = _buildWidgetTree(dslTree, _pageData);
      _performanceStats.buildTime = stopwatch.elapsedMilliseconds - buildStart;
      
      // 5. 执行页面生命周期
      bridge.callPageMethod(pagePath, 'onLoad', params);
      bridge.callPageMethod(pagePath, 'onShow', null);
      
      _state = RenderState.ready;
      _performanceStats.totalTime = stopwatch.elapsedMilliseconds;
      
      _eventController.add(RenderEvent.pageLoaded(pagePath));
      
    } catch (e, stackTrace) {
      _state = RenderState.error;
      _eventController.add(RenderEvent.error('Failed to load page: $e'));
    } finally {
      stopwatch.stop();
      notifyListeners();
    }
  }
  
  /// 重新加载当前页面
  Future<void> reload() async {
    if (_currentPage != null) {
      await loadPage(_currentPage!);
    }
  }
  
  /// 更新页面数据
  void updateData(Map<String, dynamic> newData) {
    _pageData = {...?_pageData, ...newData};
    _rootWidget = _rebuildWidget(_rootWidget, _pageData);
    notifyListeners();
  }
  
  /// 处理用户交互事件
  void handleUserEvent(String eventType, String componentId, dynamic data) {
    bridge.callPageMethod(_currentPage!, 'on${eventType.capitalize()}', {
      'target': {'id': componentId},
      'detail': data,
    });
  }
  
  /// 处理来自Bridge的消息
  void _handleBridgeMessage(String message) {
    try {
      final data = jsonDecode(message) as Map<String, dynamic>;
      final type = data['type'] as String;
      
      switch (type) {
        case 'updateData':
          final newData = data['data'] as Map<String, dynamic>;
          updateData(newData);
          break;
        case 'navigateTo':
          final url = data['url'] as String;
          loadPage(url);
          break;
        case 'navigateBack':
          // 处理返回
          break;
        case 'showToast':
          _showToast(data);
          break;
        case 'showModal':
          _showModal(data);
          break;
        case 'showLoading':
          _showLoading(data);
          break;
        case 'hideLoading':
          _hideLoading();
          break;
        default:
          break;
      }
    } catch (e) {
      debugPrint('Error handling bridge message: $e');
    }
  }
  
  /// 加载DSL文件
  Future<String> _loadDSLFile(String pagePath) async {
    // 从本地文件系统或网络加载
    final filePath = '${manifest.codeBasePath}/pages/$pagePath/${pagePath.split('/').last}.wxml';
    
    try {
      final file = File(filePath);
      if (await file.exists()) {
        return await file.readAsString();
      }
    } catch (e) {
      // 文件读取失败，尝试从asset加载
    }
    
    // 从flutter asset加载
    try {
      return await rootBundle.loadString('assets/miniprograms/$appId/pages/$pagePath.wxml');
    } catch (e) {
      throw Exception('DSL file not found: $pagePath');
    }
  }
  
  /// 加载页面数据
  Future<Map<String, dynamic>> _loadPageData(String pagePath, Map<String, dynamic>? params) async {
    // 调用页面的data初始化
    final result = await bridge.callPageMethod(pagePath, 'data', params);
    return result ?? {};
  }
  
  /// 构建组件树
  Widget _buildWidgetTree(DSLNode dslTree, Map<String, dynamic>? data) {
    return _buildWidget(dslTree, data);
  }
  
  /// 递归构建Widget
  Widget _buildWidget(DSLNode node, Map<String, dynamic>? data) {
    final componentName = node.tag;
    final attributes = _processAttributes(node.attributes, data);
    final children = node.children.map((child) => _buildWidget(child, data)).toList();
    
    // 从注册表获取组件构建器
    final builder = MiniComponentRegistry.instance.getBuilder(componentName);
    
    if (builder != null) {
      return builder(
        attributes: attributes,
        children: children,
        data: data,
        onEvent: (event, detail) => handleUserEvent(event, attributes['id'] ?? '', detail),
      );
    }
    
    // 未知组件返回占位符
    return Container(
      padding: const EdgeInsets.all(8),
      color: Colors.red.withOpacity(0.1),
      child: Text('Unknown component: $componentName'),
    );
  }
  
  /// 重建Widget（局部更新）
  Widget? _rebuildWidget(Widget? widget, Map<String, dynamic>? data) {
    // 简化的重建逻辑，实际应使用更高效的diff算法
    if (_currentPage != null) {
      // 重新构建整个树
      // 实际应实现细粒度更新
    }
    return widget;
  }
  
  /// 处理属性（数据绑定）
  Map<String, dynamic> _processAttributes(
    Map<String, String> attributes,
    Map<String, dynamic>? data,
  ) {
    final processed = <String, dynamic>{};
    
    attributes.forEach((key, value) {
      // 处理{{}}数据绑定
      if (value.contains('{{')) {
        processed[key] = _bindData(value, data);
      } else {
        processed[key] = value;
      }
    });
    
    return processed;
  }
  
  /// 数据绑定解析
  dynamic _bindData(String expression, Map<String, dynamic>? data) {
    // 简单实现：提取{{}}内的表达式并求值
    final regExp = RegExp(r'\{\{(.+?)\}\}');
    final matches = regExp.allMatches(expression);
    
    if (matches.isEmpty) return expression;
    
    String result = expression;
    for (final match in matches) {
      final expr = match.group(1)!.trim();
      final value = _evaluateExpression(expr, data);
      result = result.replaceFirst(match.group(0)!, value?.toString() ?? '');
    }
    
    return result;
  }
  
  /// 表达式求值
  dynamic _evaluateExpression(String expression, Map<String, dynamic>? data) {
    if (data == null) return null;
    
    // 支持简单的属性访问，如 user.name
    final keys = expression.split('.');
    dynamic value = data;
    
    for (final key in keys) {
      if (value is Map<String, dynamic>) {
        value = value[key];
      } else {
        return null;
      }
    }
    
    return value;
  }
  
  /// 显示Toast
  void _showToast(Map<String, dynamic> data) {
    // 通过Flutter显示Toast
    final title = data['title'] as String? ?? '';
    final duration = data['duration'] as int? ?? 1500;
    
    // 通知UI层显示Toast
    _eventController.add(RenderEvent.showToast(title, duration));
  }
  
  /// 显示Modal
  void _showModal(Map<String, dynamic> data) {
    final title = data['title'] as String?;
    final content = data['content'] as String?;
    
    _eventController.add(RenderEvent.showModal(title, content));
  }
  
  /// 显示Loading
  void _showLoading(Map<String, dynamic> data) {
    final title = data['title'] as String? ?? '加载中...';
    _eventController.add(RenderEvent.showLoading(title));
  }
  
  /// 隐藏Loading
  void _hideLoading() {
    _eventController.add(RenderEvent.hideLoading());
  }
  
  /// 获取WebView控制器
  WebViewController get webViewController => _webViewController;
  
  @override
  void dispose() {
    _eventController.close();
    super.dispose();
  }
}

/// 渲染状态
enum RenderState {
  idle,
  loading,
  ready,
  error,
}

/// 渲染事件
class RenderEvent {
  final String type;
  final dynamic data;
  
  RenderEvent._(this.type, this.data);
  
  factory RenderEvent.pageLoaded(String pagePath) => 
    RenderEvent._('pageLoaded', pagePath);
  
  factory RenderEvent.error(String message) => 
    RenderEvent._('error', message);
  
  factory RenderEvent.showToast(String title, int duration) => 
    RenderEvent._('showToast', {'title': title, 'duration': duration});
  
  factory RenderEvent.showModal(String? title, String? content) => 
    RenderEvent._('showModal', {'title': title, 'content': content});
  
  factory RenderEvent.showLoading(String title) => 
    RenderEvent._('showLoading', title);
  
  factory RenderEvent.hideLoading() => 
    RenderEvent._('hideLoading', null);
}

/// 渲染性能统计
class RenderPerformanceStats {
  int parseTime = 0;
  int buildTime = 0;
  int totalTime = 0;
  int frameCount = 0;
  double avgFrameTime = 0;
  
  Map<String, dynamic> toJson() => {
    'parseTime': parseTime,
    'buildTime': buildTime,
    'totalTime': totalTime,
    'frameCount': frameCount,
    'avgFrameTime': avgFrameTime,
  };
}

/// DSL节点
class DSLNode {
  final String tag;
  final Map<String, String> attributes;
  final List<DSLNode> children;
  final String? textContent;
  
  DSLNode({
    required this.tag,
    this.attributes = const {},
    this.children = const [],
    this.textContent,
  });
}

/// String扩展
extension StringExtension on String {
  String capitalize() {
    if (isEmpty) return this;
    return '${this[0].toUpperCase()}${substring(1)}';
  }
}
