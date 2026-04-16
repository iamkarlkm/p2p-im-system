/// 网络层抽象 - 功能#9
abstract class IMNetworkLayer {
  Future<dynamic> get(String path, {Map<String, dynamic>? params});
  Future<dynamic> post(String path, {Map<String, dynamic>? data});
  Future<dynamic> upload(String path, String filePath);
  void setToken(String token);
}

/// HTTP网络层实现
class IMHttpNetwork implements IMNetworkLayer {
  String? _token;
  final String baseUrl;

  IMHttpNetwork({required this.baseUrl});

  @override
  void setToken(String token) {
    _token = token;
  }

  @override
  Future<dynamic> get(String path, {Map<String, dynamic>? params}) async {
    // HTTP GET实现
    print('GET $baseUrl$path, token=$_token');
    return {'code': 0, 'data': {}};
  }

  @override
  Future<dynamic> post(String path, {Map<String, dynamic>? data}) async {
    // HTTP POST实现
    print('POST $baseUrl$path, token=$_token');
    return {'code': 0, 'data': {}};
  }

  @override
  Future<dynamic> upload(String path, String filePath) async {
    // 文件上传实现
    print('UPLOAD $baseUrl$path, file=$filePath');
    return {'code': 0, 'data': {'url': 'https://example.com/file.jpg'}};
  }
}
