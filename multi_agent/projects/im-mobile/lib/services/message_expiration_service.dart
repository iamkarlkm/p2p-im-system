/**
 * 消息过期服务
 */
import 'package:dio/dio.dart';
import '../models/message_expiration_rule.dart';

class MessageExpirationService {
  final Dio _dio;

  MessageExpirationService(this._dio);

  /// 创建会话级过期规则
  Future<MessageExpirationRule> createRule(String conversationId, ExpirationRuleRequest req) async {
    final resp = await _dio.post('/api/expiration/rules', data: {
      ...req.toJson(),
      'conversationId': conversationId,
    });
    return MessageExpirationRule.fromJson(resp.data);
  }

  /// 更新过期规则
  Future<MessageExpirationRule> updateRule(int ruleId, ExpirationRuleRequest req) async {
    final resp = await _dio.put('/api/expiration/rules/$ruleId', data: req.toJson());
    return MessageExpirationRule.fromJson(resp.data);
  }

  /// 获取用户所有规则
  Future<List<MessageExpirationRule>> getUserRules() async {
    final resp = await _dio.get('/api/expiration/rules');
    final List<dynamic> data = resp.data ?? [];
    return data.map((e) => MessageExpirationRule.fromJson(e)).toList();
  }

  /// 获取会话生效规则
  Future<MessageExpirationRule?> getEffectiveRule(String conversationId) async {
    final resp = await _dio.get('/api/expiration/rules/conversation/$conversationId');
    if (resp.data == null) return null;
    return MessageExpirationRule.fromJson(resp.data);
  }

  /// 获取全局默认规则
  Future<MessageExpirationRule?> getGlobalRule() async {
    final resp = await _dio.get('/api/expiration/rules/global');
    if (resp.data == null) return null;
    return MessageExpirationRule.fromJson(resp.data);
  }

  /// 删除规则
  Future<void> deleteRule(int ruleId) async {
    await _dio.delete('/api/expiration/rules/$ruleId');
  }

  /// 启用/禁用规则
  Future<MessageExpirationRule> toggleRule(int ruleId, bool enabled) async {
    final resp = await _dio.patch('/api/expiration/rules/$ruleId/toggle?enabled=$enabled');
    return MessageExpirationRule.fromJson(resp.data);
  }

  /// 获取消息剩余存活时间
  Future<int> getRemainingTime(int messageId) async {
    final resp = await _dio.get('/api/expiration/messages/$messageId/remaining');
    return resp.data['remainingSeconds'] ?? -1;
  }

  /// 记录消息被阅读（启动阅后即焚计时）
  Future<void> recordRead(int messageId) async {
    await _dio.post('/api/expiration/messages/$messageId/read');
  }
}
