import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/user.dart';

class AuthService extends ChangeNotifier {
  static const String _userKey = 'im_user';
  
  User? _currentUser;
  String? _token;
  bool _isLoading = false;

  User? get currentUser => _currentUser;
  String? get currentUserId => _currentUser?.id;
  String? get token => _token;
  bool get isLoggedIn => _currentUser != null && _token != null;
  bool get isLoading => _isLoading;

  static const String _apiBaseUrl = 'http://10.0.2.2:8080'; // Android模拟器访问主机

  AuthService() {
    _loadSavedUser();
  }

  Future<void> _loadSavedUser() async {
    final prefs = await SharedPreferences.getInstance();
    final userJson = prefs.getString(_userKey);
    
    if (userJson != null) {
      final data = jsonDecode(userJson);
      _currentUser = User.fromJson(data['user']);
      _token = data['token'];
      notifyListeners();
    }
  }

  Future<bool> login(String username, String password) async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/api/auth/login'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'username': username, 'password': password}),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        
        _token = data['token'];
        _currentUser = User(
          id: data['userId'].toString(),
          username: data['username'],
          nickname: data['nickname'],
        );

        // 保存到本地
        final prefs = await SharedPreferences.getInstance();
        await prefs.setString(_userKey, jsonEncode({
          'user': _currentUser!.toJson(),
          'token': _token,
        }));

        _isLoading = false;
        notifyListeners();
        return true;
      } else {
        _isLoading = false;
        notifyListeners();
        return false;
      }
    } catch (e) {
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  Future<bool> register(String username, String password, String nickname) async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/api/auth/register'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'username': username,
          'password': password,
          'nickname': nickname,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        
        _token = data['token'];
        _currentUser = User(
          id: data['userId'].toString(),
          username: data['username'],
          nickname: data['nickname'] ?? username,
        );

        // 保存到本地
        final prefs = await SharedPreferences.getInstance();
        await prefs.setString(_userKey, jsonEncode({
          'user': _currentUser!.toJson(),
          'token': _token,
        }));

        _isLoading = false;
        notifyListeners();
        return true;
      } else {
        _isLoading = false;
        notifyListeners();
        return false;
      }
    } catch (e) {
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  Future<void> logout() async {
    _currentUser = null;
    _token = null;
    
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_userKey);
    
    notifyListeners();
  }
}
