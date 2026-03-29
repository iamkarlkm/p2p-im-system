/**
 * 快捷操作菜单服务
 * API 调用封装
 */

import 'package:dio/dio.dart';
import '../models/message_quick_action_menu_model.dart';

class QuickActionMenuService {
  final Dio _dio;
  final String _baseUrl = '/api/quick-action-menu';

  QuickActionMenuService(this._dio);

  // ============ 菜单 CRUD ============

  /// 创建菜单
  Future<MessageQuickActionMenu> createMenu(
    Map<String, dynamic> data,
  ) async {
    final response = await _dio.post(_baseUrl, data: data);
    return MessageQuickActionMenu.fromJson(response.data['data']);
  }

  /// 更新菜单
  Future<MessageQuickActionMenu> updateMenu(
    int menuId,
    Map<String, dynamic> data,
  ) async {
    final response = await _dio.put('$_baseUrl/$menuId', data: data);
    return MessageQuickActionMenu.fromJson(response.data['data']);
  }

  /// 删除菜单
  Future<void> deleteMenu(int menuId) async {
    await _dio.delete('$_baseUrl/$menuId');
  }

  /// 获取菜单详情
  Future<MessageQuickActionMenu> getMenu(int menuId) async {
    final response = await _dio.get('$_baseUrl/$menuId');
    return MessageQuickActionMenu.fromJson(response.data['data']);
  }

  // ============ 用户菜单 ============

  /// 获取用户的所有菜单
  Future<List<MessageQuickActionMenu>> getUserMenus(int userId) async {
    final response = await _dio.get('$_baseUrl/user/$userId');
    final List<dynamic> data = response.data['data'];
    return data.map((e) => MessageQuickActionMenu.fromJson(e)).toList();
  }

  /// 获取用户特定类型的菜单
  Future<List<MessageQuickActionMenu>> getUserMenusByType(
    int userId,
    MenuType menuType,
  ) async {
    final response = await _dio.get(
      '$_baseUrl/user/$userId/type/${menuType.toString().split('.').last}',
    );
    final List<dynamic> data = response.data['data'];
    return data.map((e) => MessageQuickActionMenu.fromJson(e)).toList();
  }

  /// 获取用户的默认菜单
  Future<List<MessageQuickActionMenu>> getDefaultMenus(int userId) async {
    final response = await _dio.get('$_baseUrl/user/$userId/default');
    final List<dynamic> data = response.data['data'];
    return data.map((e) => MessageQuickActionMenu.fromJson(e)).toList();
  }

  /// 获取特定类型的默认菜单
  Future<MessageQuickActionMenu?> getDefaultMenuByType(
    int userId,
    MenuType menuType,
  ) async {
    try {
      final response = await _dio.get(
        '$_baseUrl/user/$userId/default/type/${menuType.toString().split('.').last}',
      );
      return MessageQuickActionMenu.fromJson(response.data['data']);
    } catch (_) {
      return null;
    }
  }

  /// 初始化用户默认菜单
  Future<List<MessageQuickActionMenu>> initializeDefaultMenus(int userId) async {
    final response = await _dio.post('$_baseUrl/user/$userId/initialize');
    final List<dynamic> data = response.data['data'];
    return data.map((e) => MessageQuickActionMenu.fromJson(e)).toList();
  }

  // ============ 菜单操作 ============

  /// 设置菜单为默认
  Future<MessageQuickActionMenu> setAsDefault(int menuId) async {
    final response = await _dio.post('$_baseUrl/$menuId/set-default');
    return MessageQuickActionMenu.fromJson(response.data['data']);
  }

  /// 启用菜单
  Future<MessageQuickActionMenu> enableMenu(int menuId) async {
    final response = await _dio.post('$_baseUrl/$menuId/enable');
    return MessageQuickActionMenu.fromJson(response.data['data']);
  }

  /// 禁用菜单
  Future<MessageQuickActionMenu> disableMenu(int menuId) async {
    final response = await _dio.post('$_baseUrl/$menuId/disable');
    return MessageQuickActionMenu.fromJson(response.data['data']);
  }

  /// 复制菜单
  Future<MessageQuickActionMenu> duplicateMenu(
    int menuId,
    String newName,
  ) async {
    final response = await _dio.post(
      '$_baseUrl/$menuId/duplicate',
      queryParameters: {'newName': newName},
    );
    return MessageQuickActionMenu.fromJson(response.data['data']);
  }

  // ============ 会话菜单 ============

  /// 获取会话特定的菜单
  Future<List<MessageQuickActionMenu>> getConversationMenus(
    int userId,
    int conversationId,
  ) async {
    final response = await _dio.get(
      '$_baseUrl/user/$userId/conversation/$conversationId',
    );
    final List<dynamic> data = response.data['data'];
    return data.map((e) => MessageQuickActionMenu.fromJson(e)).toList();
  }

  /// 为会话设置自定义菜单
  Future<MessageQuickActionMenu> setConversationMenu(
    int userId,
    int conversationId,
    Map<String, dynamic> data,
  ) async {
    final response = await _dio.post(
      '$_baseUrl/user/$userId/conversation/$conversationId',
      data: data,
    );
    return MessageQuickActionMenu.fromJson(response.data['data']);
  }

  /// 重置会话菜单为默认
  Future<void> resetConversationMenu(int userId, int conversationId) async {
    await _dio.post('$_baseUrl/user/$userId/conversation/$conversationId/reset');
  }
}
