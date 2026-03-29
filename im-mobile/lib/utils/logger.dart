import 'dart:developer' as developer;

/// 日志工具类
class IMLogger {
  static const String _tag = 'IM';

  /// 调试日志
  static void d(String tag, String message) {
    _log('DEBUG', tag, message);
  }

  /// 信息日志
  static void i(String tag, String message) {
    _log('INFO', tag, message);
  }

  /// 警告日志
  static void w(String tag, String message) {
    _log('WARN', tag, message);
  }

  /// 错误日志
  static void e(String tag, String message, [dynamic error, StackTrace? stackTrace]) {
    _log('ERROR', tag, '$message${error != null ? ' | Error: $error' : ''}');
    if (stackTrace != null) {
      developer.log(
        stackTrace.toString(),
        name: '$_tag-$tag',
        level: 1000,
      );
    }
  }

  static void _log(String level, String tag, String message) {
    final timestamp = DateTime.now().toIso8601String();
    final logMessage = '[$timestamp] [$level] [$_tag-$tag] $message';
    
    // 输出到控制台
    developer.log(
      logMessage,
      name: '$_tag-$tag',
    );
  }
}
