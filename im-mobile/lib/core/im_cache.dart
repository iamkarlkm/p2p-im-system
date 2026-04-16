/// IM本地数据缓存 - 功能#9
class IMCache {
  static final IMCache _instance = IMCache._internal();
  factory IMCache() => _instance;
  IMCache._internal();

  final Map<String, dynamic> _memoryCache = {};
  final Map<String, List<IMMessage>> _messageCache = {};

  /// 存储数据
  void set(String key, dynamic value) {
    _memoryCache[key] = value;
  }

  /// 获取数据
  T? get<T>(String key) {
    return _memoryCache[key] as T?;
  }

  /// 缓存消息
  void cacheMessages(String conversationId, List<IMMessage> messages) {
    _messageCache[conversationId] = messages;
  }

  /// 获取缓存消息
  List<IMMessage> getCachedMessages(String conversationId) {
    return _messageCache[conversationId] ?? [];
  }

  /// 添加单条消息
  void addMessage(String conversationId, IMMessage message) {
    _messageCache.putIfAbsent(conversationId, () => []).add(message);
  }

  /// 清除缓存
  void clear() {
    _memoryCache.clear();
    _messageCache.clear();
  }
}
