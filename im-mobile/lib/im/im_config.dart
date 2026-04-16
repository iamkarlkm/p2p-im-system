/// IM客户端配置
class IMConfig {
  /// WebSocket服务器地址
  final String wsUrl;
  
  /// REST API基础地址
  final String apiBaseUrl;
  
  /// 心跳间隔(秒)
  final int heartbeatInterval;
  
  /// 重连间隔(秒)
  final int reconnectInterval;
  
  /// 最大重连次数
  final int maxReconnectAttempts;
  
  /// 连接超时时间(秒)
  final int connectTimeout;
  
  /// 是否自动重连
  final bool autoReconnect;
  
  /// 是否启用日志
  final bool enableLog;
  
  /// 消息缓存大小
  final int messageCacheSize;

  const IMConfig({
    required this.wsUrl,
    required this.apiBaseUrl,
    this.heartbeatInterval = 30,
    this.reconnectInterval = 5,
    this.maxReconnectAttempts = 10,
    this.connectTimeout = 10,
    this.autoReconnect = true,
    this.enableLog = true,
    this.messageCacheSize = 1000,
  });

  /// 默认配置
  static const IMConfig defaultConfig = IMConfig(
    wsUrl: 'wss://api.im.example.com/ws/v1',
    apiBaseUrl: 'https://api.im.example.com/api/v1',
  );

  /// 开发环境配置
  static const IMConfig devConfig = IMConfig(
    wsUrl: 'ws://localhost:8080/ws/v1',
    apiBaseUrl: 'http://localhost:8080/api/v1',
    enableLog: true,
  );
}
