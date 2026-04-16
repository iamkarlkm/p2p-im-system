/// IM异常
class IMException implements Exception {
  final String message;
  final int? code;

  IMException(this.message, {this.code});

  @override
  String toString() => 'IMException: $message (code: $code)';
}
