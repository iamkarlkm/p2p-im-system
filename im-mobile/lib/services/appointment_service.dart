import 'dart:convert';
import 'package:http/http.dart' as http;
import '../config/api_config.dart';
import '../models/appointment.dart';
import '../models/queue_ticket.dart';

/// 预约服务类
/// 本地生活服务预约与排班管理系统 - 移动端API服务
/// 
/// @author IM Development Team
/// @since 2026-03-28
class AppointmentService {
  static final AppointmentService _instance = AppointmentService._internal();
  factory AppointmentService() => _instance;
  AppointmentService._internal();

  final String _baseUrl = ApiConfig.baseUrl;
  final Map<String, String> _headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  };

  // ==================== 预约管理接口 ====================

  /// 创建预约
  Future<Appointment> createAppointment(Appointment appointment) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/v1/appointment/appointments'),
      headers: _headers,
      body: jsonEncode(appointment.toJson()),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return Appointment.fromJson(data['data']);
    }
    throw Exception('创建预约失败: ${response.body}');
  }

  /// 获取预约详情
  Future<Appointment> getAppointmentDetail(int appointmentId) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/api/v1/appointment/appointments/$appointmentId'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return Appointment.fromJson(data['data']);
    }
    throw Exception('获取预约详情失败: ${response.body}');
  }

  /// 修改预约
  Future<Appointment> modifyAppointment(int appointmentId, Appointment appointment) async {
    final response = await http.put(
      Uri.parse('$_baseUrl/api/v1/appointment/appointments/$appointmentId'),
      headers: _headers,
      body: jsonEncode(appointment.toJson()),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return Appointment.fromJson(data['data']);
    }
    throw Exception('修改预约失败: ${response.body}');
  }

  /// 取消预约
  Future<Appointment> cancelAppointment(int appointmentId, {String? reason}) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/v1/appointment/appointments/$appointmentId/cancel'),
      headers: _headers,
      body: jsonEncode({'reason': reason, 'cancelBy': 'USER'}),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return Appointment.fromJson(data['data']);
    }
    throw Exception('取消预约失败: ${response.body}');
  }

  /// 获取用户预约列表
  Future<List<Appointment>> getUserAppointments(int userId) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/api/v1/appointment/appointments/user/$userId'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final List<dynamic> list = data['data'];
      return list.map((e) => Appointment.fromJson(e)).toList();
    }
    throw Exception('获取预约列表失败: ${response.body}');
  }

  /// 查询可用时段
  Future<List<AppointmentSlot>> getAvailableSlots({
    required int merchantId,
    required int serviceTypeId,
    required DateTime date,
  }) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/api/v1/appointment/slots').replace(queryParameters: {
        'merchantId': merchantId.toString(),
        'serviceTypeId': serviceTypeId.toString(),
        'date': date.toIso8601String().split('T')[0],
      }),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final List<dynamic> list = data['data'];
      return list.map((e) => AppointmentSlot.fromJson(e)).toList();
    }
    throw Exception('查询可用时段失败: ${response.body}');
  }

  /// 检查时间冲突
  Future<bool> checkTimeConflict({
    required int merchantId,
    required DateTime date,
    required String startTime,
    required String endTime,
    int? excludeId,
  }) async {
    final params = {
      'merchantId': merchantId.toString(),
      'date': date.toIso8601String().split('T')[0],
      'startTime': startTime,
      'endTime': endTime,
      if (excludeId != null) 'excludeId': excludeId.toString(),
    };

    final response = await http.get(
      Uri.parse('$_baseUrl/api/v1/appointment/check-conflict').replace(queryParameters: params),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return data['data'] as bool;
    }
    return false;
  }

  // ==================== 排队叫号接口 ====================

  /// 远程取号
  Future<QueueTicket> takeQueueTicket(QueueTicket ticket) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/v1/appointment/queue/take'),
      headers: _headers,
      body: jsonEncode(ticket.toJson()),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return QueueTicket.fromJson(data['data']);
    }
    throw Exception('取号失败: ${response.body}');
  }

  /// 获取排队状态
  Future<QueueTicket> getQueueStatus(int ticketId) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/api/v1/appointment/queue/tickets/$ticketId'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return QueueTicket.fromJson(data['data']);
    }
    throw Exception('获取排队状态失败: ${response.body}');
  }

  /// 取消排队
  Future<QueueTicket> cancelQueueTicket(int ticketId) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/v1/appointment/queue/tickets/$ticketId/cancel'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return QueueTicket.fromJson(data['data']);
    }
    throw Exception('取消排队失败: ${response.body}');
  }

  /// 过号重排
  Future<QueueTicket> requeueTicket(int ticketId) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/v1/appointment/queue/tickets/$ticketId/requeue'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return QueueTicket.fromJson(data['data']);
    }
    throw Exception('重排失败: ${response.body}');
  }

  // ==================== 工具方法 ====================

  /// 获取推荐的最早可用时段
  Future<AppointmentSlot?> getEarliestAvailableSlot({
    required int merchantId,
    required int serviceTypeId,
    required DateTime date,
  }) async {
    final slots = await getAvailableSlots(
      merchantId: merchantId,
      serviceTypeId: serviceTypeId,
      date: date,
    );
    
    for (final slot in slots) {
      if (slot.available && slot.availableCount > 0) {
        return slot;
      }
    }
    return null;
  }

  /// 智能推荐预约时间
  /// 根据用户偏好和可用性推荐最佳预约时间
  Future<Map<String, dynamic>> recommendAppointmentTime({
    required int merchantId,
    required int serviceTypeId,
    required DateTime preferredDate,
    String? preferredTimeRange, // morning/afternoon/evening
  }) async {
    final slots = await getAvailableSlots(
      merchantId: merchantId,
      serviceTypeId: serviceTypeId,
      date: preferredDate,
    );

    List<AppointmentSlot> filteredSlots = slots.where((s) => s.available).toList();

    // 根据时间偏好筛选
    if (preferredTimeRange != null) {
      filteredSlots = filteredSlots.where((s) {
        final hour = int.parse(s.startTime.split(':')[0]);
        switch (preferredTimeRange) {
          case 'morning':
            return hour >= 9 && hour < 12;
          case 'afternoon':
            return hour >= 12 && hour < 18;
          case 'evening':
            return hour >= 18;
          default:
            return true;
        }
      }).toList();
    }

    if (filteredSlots.isEmpty) {
      return {'success': false, 'message': '所选日期暂无可用时段'};
    }

    // 优先推荐中间时段
    final recommendedSlot = filteredSlots[filteredSlots.length ~/ 2];
    
    return {
      'success': true,
      'slot': recommendedSlot,
      'alternatives': filteredSlots.take(3).toList(),
    };
  }

  /// 批量检查多个时段的可用性
  Future<Map<String, bool>> checkSlotsAvailability({
    required int merchantId,
    required int serviceTypeId,
    required List<DateTime> dates,
  }) async {
    final result = <String, bool>{};
    
    for (final date in dates) {
      try {
        final slots = await getAvailableSlots(
          merchantId: merchantId,
          serviceTypeId: serviceTypeId,
          date: date,
        );
        final hasAvailable = slots.any((s) => s.available);
        result[date.toIso8601String().split('T')[0]] = hasAvailable;
      } catch (e) {
        result[date.toIso8601String().split('T')[0]] = false;
      }
    }
    
    return result;
  }

  /// 格式化预约信息用于分享
  String formatAppointmentForShare(Appointment appointment) {
    return '''
📅 预约信息
━━━━━━━━━━━━━━
商户: ${appointment.serviceTypeName}
日期: ${appointment.formattedDate}
时间: ${appointment.appointmentTimeText}
人数: ${appointment.peopleCount}人
姓名: ${appointment.customerName}
电话: ${appointment.customerPhone}
━━━━━━━━━━━━━━
请准时到店，如有变动请提前取消。
    '''.trim();
  }
}
