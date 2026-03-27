import 'dart:convert';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:webview_flutter/webview_flutter.dart';

/// JSBridge通信层
/// 负责Flutter与JavaScript之间的双向通信
class MiniProgramBridge {
  
  final String appId;
  final String sandboxId;
  
  WebViewController? _webViewController;
  
  // 方法调用处理器
  final Map<String, MethodHandler> _methodHandlers = {};
  
  // 待处理的调用
  final Map<String, Completer<dynamic>> _pendingCalls = {};
  
  // 调用ID生成器
  int _callIdCounter = 0;
  
  // 事件流控制器
  final _eventController = StreamController<BridgeEvent>.broadcast();
  Stream<BridgeEvent> get events => _eventController.stream;
  
  // 页面方法映射（模拟JavaScript环境）
  final Map<String, Map<String, Function>> _pageMethods = {};
  
  MiniProgramBridge({
    required this.appId,
    required this.sandboxId,
  }) {
    _registerDefaultHandlers();
  }
  
  /// 设置WebView控制器
  void setWebViewController(WebViewController controller) {
    _webViewController = controller;
    _injectBridgeScript();
  }
  
  /// 注入Bridge脚本到WebView
  Future<void> _injectBridgeScript() async {
    if (_webViewController == null) return;
    
    const bridgeScript = '''
      (function() {
        window.MiniProgramBridge = {
          invoke: function(method, data, callback) {
            var callId = 'call_' + Date.now() + '_' + Math.random();
            if (callback) {
              window.MiniProgramCallbacks = window.MiniProgramCallbacks || {};
              window.MiniProgramCallbacks[callId] = callback;
            }
            window.flutter_inappwebview.callHandler('MiniProgramBridge', JSON.stringify({
              type: 'invoke',
              callId: callId,
              method: method,
              data: data
            }));
          },
          
          postMessage: function(message) {
            window.flutter_inappwebview.callHandler('MiniProgramBridge', JSON.stringify({
              type: 'message',
              data: message
            }));
          },
          
          onMessage: function(callback) {
            window.MiniProgramMessageCallback = callback;
          }
        };
        
        // 暴露小程序API
        window.wx = window.wx || {};
        
        // 存储API
        wx.getStorageSync = function(key) {
          var result;
          MiniProgramBridge.invoke('storage.get', {key: key}, function(res) {
            result = res.data;
          });
          return result;
        };
        
        wx.setStorageSync = function(key, data) {
          MiniProgramBridge.invoke('storage.set', {key: key, data: data});
        };
        
        // 网络API
        wx.request = function(options) {
          MiniProgramBridge.invoke('request', {
            url: options.url,
            method: options.method || 'GET',
            data: options.data,
            header: options.header
          }, function(res) {
            if (res.statusCode === 200 && options.success) {
              options.success(res);
            } else if (options.fail) {
              options.fail(res);
            }
            if (options.complete) options.complete(res);
          });
        };
        
        // 导航API
        wx.navigateTo = function(options) {
          MiniProgramBridge.postMessage({
            type: 'navigateTo',
            url: options.url
          });
          if (options.success) options.success({});
          if (options.complete) options.complete({});
        };
        
        wx.navigateBack = function(options) {
          MiniProgramBridge.postMessage({
            type: 'navigateBack',
            delta: options.delta || 1
          });
          if (options.success) options.success({});
          if (options.complete) options.complete({});
        };
        
        // UI API
        wx.showToast = function(options) {
          MiniProgramBridge.postMessage({
            type: 'showToast',
            title: options.title,
            icon: options.icon || 'success',
            duration: options.duration || 1500
          });
          if (options.success) options.success({});
          if (options.complete) options.complete({});
        };
        
        wx.showModal = function(options) {
          MiniProgramBridge.postMessage({
            type: 'showModal',
            title: options.title,
            content: options.content
          });
          if (options.success) options.success({confirm: true});
          if (options.complete) options.complete({});
        };
        
        wx.showLoading = function(options) {
          MiniProgramBridge.postMessage({
            type: 'showLoading',
            title: options.title || '加载中...'
          });
          if (options.success) options.success({});
          if (options.complete) options.complete({});
        };
        
        wx.hideLoading = function() {
          MiniProgramBridge.postMessage({type: 'hideLoading'});
        };
        
        // 设备API
        wx.getSystemInfo = function(options) {
          MiniProgramBridge.invoke('device.getSystemInfo', {}, function(res) {
            if (options.success) options.success(res);
            if (options.complete) options.complete(res);
          });
        };
        
        // 页面生命周期
        window.Page = function(config) {
          window.currentPageConfig = config;
          if (config.onLoad) config.onLoad();
          if (config.onShow) config.onShow();
        };
        
        // App生命周期
        window.App = function(config) {
          window.appConfig = config;
          if (config.onLaunch) config.onLaunch();
        };
        
        console.log('MiniProgramBridge initialized');
      })();
    ''';
    
    await _webViewController!.runJavaScript(bridgeScript);
  }
  
  /// 处理来自JavaScript的消息
  void handleJavaScriptMessage(String message) {
    try {
      final data = jsonDecode(message) as Map<String, dynamic>;
      final type = data['type'] as String;
      
      switch (type) {
        case 'invoke':
          _handleInvoke(data);
          break;
        case 'message':
          _handleMessage(data['data']);
          break;
        case 'callback':
          _handleCallback(data);
          break;
        default:
          _eventController.add(BridgeEvent.unknown(message));
      }
    } catch (e) {
      _eventController.add(BridgeEvent.error('Failed to parse message: $e'));
    }
  }
  
  /// 处理方法调用
  Future<void> _handleInvoke(Map<String, dynamic> data) async {
    final callId = data['callId'] as String;
    final method = data['method'] as String;
    final params = data['data'] as Map<String, dynamic>?;
    
    final handler = _methodHandlers[method];
    
    try {
      final result = handler != null 
        ? await handler(params)
        : await _handleDefaultMethod(method, params);
      
      _sendCallback(callId, result);
    } catch (e) {
      _sendCallback(callId, {'error': e.toString()});
    }
  }
  
  /// 处理消息
  void _handleMessage(dynamic data) {
    _eventController.add(BridgeEvent.message(data));
  }
  
  /// 处理回调
  void _handleCallback(Map<String, dynamic> data) {
    final callId = data['callId'] as String;
    final result = data['result'];
    
    final completer = _pendingCalls.remove(callId);
    if (completer != null) {
      completer.complete(result);
    }
  }
  
  /// 发送回调到JavaScript
  Future<void> _sendCallback(String callId, dynamic result) async {
    if (_webViewController == null) return;
    
    final script = '''
      if (window.MiniProgramCallbacks && window.MiniProgramCallbacks['$callId']) {
        window.MiniProgramCallbacks['$callId'](${jsonEncode(result)});
        delete window.MiniProgramCallbacks['$callId'];
      }
    ''';
    
    await _webViewController!.runJavaScript(script);
  }
  
  /// 调用JavaScript方法
  Future<dynamic> callJavaScript(String method, [List<dynamic>? args]) async {
    if (_webViewController == null) {
      throw Exception('WebViewController not set');
    }
    
    final callId = 'flutter_${_callIdCounter++}';
    final completer = Completer<dynamic>();
    _pendingCalls[callId] = completer;
    
    final argsJson = args != null ? jsonEncode(args) : '[]';
    final script = '''
      (function() {
        var result = $method.apply(null, $argsJson);
        window.flutter_inappwebview.callHandler('MiniProgramBridge', JSON.stringify({
          type: 'callback',
          callId: '$callId',
          result: result
        }));
      })();
    ''';
    
    await _webViewController!.runJavaScript(script);
    
    return completer.future.timeout(
      const Duration(seconds: 5),
      onTimeout: () {
        _pendingCalls.remove(callId);
        throw TimeoutException('JavaScript call timeout');
      },
    );
  }
  
  /// 调用页面方法
  Future<dynamic> callPageMethod(String pagePath, String method, Map<String, dynamic>? params) async {
    // 检查是否有注册的页面方法
    final page = _pageMethods[pagePath];
    if (page != null && page.containsKey(method)) {
      return page[method]!(params);
    }
    
    // 通过JavaScript调用
    if (_webViewController != null) {
      final script = '''
        (function() {
          if (window.currentPageConfig && window.currentPageConfig.$method) {
            return window.currentPageConfig.$method(${params != null ? jsonEncode(params) : ''});
          }
          return null;
        })()
      ''';
      
      final result = await _webViewController!.runJavaScriptReturningResult(script);
      return result;
    }
    
    return null;
  }
  
  /// 注册方法处理器
  void registerMethodHandler(String method, MethodHandler handler) {
    _methodHandlers[method] = handler;
  }
  
  /// 注销方法处理器
  void unregisterMethodHandler(String method) {
    _methodHandlers.remove(method);
  }
  
  /// 注册页面方法
  void registerPageMethod(String pagePath, String method, Function handler) {
    _pageMethods.putIfAbsent(pagePath, () => {})[method] = handler;
  }
  
  /// 发送事件到JavaScript
  Future<void> sendEvent(String eventType, Map<String, dynamic> data) async {
    if (_webViewController == null) return;
    
    final script = '''
      if (window.MiniProgramMessageCallback) {
        window.MiniProgramMessageCallback({
          type: '$eventType',
          data: ${jsonEncode(data)}
        });
      }
    ''';
    
    await _webViewController!.runJavaScript(script);
  }
  
  /// 注册默认处理器
  void _registerDefaultHandlers() {
    // 存储API
    registerMethodHandler('storage.get', (params) async {
      // 实际实现：从本地存储读取
      return {'data': null};
    });
    
    registerMethodHandler('storage.set', (params) async {
      // 实际实现：写入本地存储
      return {'success': true};
    });
    
    // 设备API
    registerMethodHandler('device.getSystemInfo', (params) async {
      return {
        'model': 'Unknown',
        'system': 'Flutter',
        'version': '1.0.0',
        'platform': 'mobile',
      };
    });
    
    // 网络API
    registerMethodHandler('request', (params) async {
      // 实际实现：HTTP请求
      return {
        'statusCode': 200,
        'data': '{}',
        'header': {},
      };
    });
  }
  
  /// 处理默认方法
  Future<dynamic> _handleDefaultMethod(String method, Map<String, dynamic>? params) async {
    switch (method) {
      case 'log':
        print('[MiniProgram] ${params?['message']}');
        return {'success': true};
      default:
        return {'error': 'Method not found: $method'};
    }
  }
  
  void dispose() {
    _eventController.close();
  }
}

/// 方法处理器类型
typedef MethodHandler = Future<dynamic> Function(Map<String, dynamic>? params);

/// Bridge事件
class BridgeEvent {
  final String type;
  final dynamic data;
  
  BridgeEvent._(this.type, this.data);
  
  factory BridgeEvent.message(dynamic data) => BridgeEvent._('message', data);
  factory BridgeEvent.unknown(String raw) => BridgeEvent._('unknown', raw);
  factory BridgeEvent.error(String message) => BridgeEvent._('error', message);
}

/// 超时异常
class TimeoutException implements Exception {
  final String message;
  TimeoutException(this.message);
  
  @override
  String toString() => 'TimeoutException: $message';
}
