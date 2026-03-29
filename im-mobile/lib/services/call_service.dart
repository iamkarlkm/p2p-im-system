import 'package:dio/dio.dart';
import '../models/call_record.dart';
import '../services/api_service.dart';

class CallService {
  final ApiService _api = ApiService();

  Future<CallRecord> initiateCall({
    required String calleeId,
    required String conversationId,
    required String callType,
  }) async {
    final resp = await _api.post('/calls/initiate', data: {
      'calleeId': calleeId,
      'conversationId': conversationId,
      'callType': callType,
    });
    return CallRecord.fromJson(resp.data as Map<String, dynamic>);
  }

  Future<CallRecord> updateStatus(String callId, String status) async {
    final resp = await _api.put('/calls/${Uri.encodeComponent(callId)}/status', data: {'status': status});
    return CallRecord.fromJson(resp.data as Map<String, dynamic>);
  }

  Future<CallRecord> endCall(String callId, {bool endedByCaller = true}) async {
    final resp = await _api.post('/calls/${Uri.encodeComponent(callId)}/end', data: {'endedByCaller': endedByCaller});
    return CallRecord.fromJson(resp.data as Map<String, dynamic>);
  }

  Future<void> markMissed(String callId) async {
    await _api.post('/calls/${Uri.encodeComponent(callId)}/missed');
  }

  Future<List<CallRecord>> getCallHistory({int page = 0, int size = 20}) async {
    final resp = await _api.get('/calls/history', queryParameters: {'page': page, 'size': size});
    final list = resp.data['content'] as List<dynamic>;
    return list.map((e) => CallRecord.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<List<CallRecord>> getMissedCalls() async {
    final resp = await _api.get('/calls/missed');
    final list = resp.data as List<dynamic>;
    return list.map((e) => CallRecord.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<void> deleteCall(int id) async {
    await _api.delete('/calls/$id');
  }

  String formatDuration(int? seconds) {
    if (seconds == null || seconds == 0) return '0秒';
    final m = seconds ~/ 60;
    final s = seconds % 60;
    return m > 0 ? '${m}分${s}秒' : '${s}秒';
  }

  String getStatusLabel(String status) {
    const map = {
      'INITIATED': '已拨打',
      'RINGING': '响铃中',
      'ANSWERED': '已接听',
      'ENDED': '已结束',
      'MISSED': '未接来电',
      'REJECTED': '已拒绝',
      'FAILED': '呼叫失败',
    };
    return map[status] ?? status;
  }
}
