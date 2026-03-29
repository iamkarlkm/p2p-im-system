import 'package:dio/dio.dart';
import '../../../core/api/api_client.dart';
import '../models/appointment_models.dart';

/// 预约服务类
class AppointmentService {
  final ApiClient _apiClient = ApiClient();

  /// 提交预约
  Future<AppointmentModel> submitAppointment({
    required int merchantId,
    required int serviceId,
    required DateTime appointmentDate,
    required String startTime,
    String? endTime,
    required String contactName,
    required String contactPhone,
    String? remark,
    int numberOfPeople = 1,
    int? staffId,
    int? seatId,
    String? source,
  }) async {
    final response = await _apiClient.post(
      '/api/v1/appointments',
      data: {
        'merchantId': merchantId,
        'serviceId': serviceId,
        'appointmentDate': appointmentDate.toIso8601String().split('T')[0],
        'startTime': startTime,
        'endTime': endTime,
        'contactName': contactName,
        'contactPhone': contactPhone,
        'remark': remark,
        'numberOfPeople': numberOfPeople,
        'staffId': staffId,
        'seatId': seatId,
        'source': source,
      },
    );
    return AppointmentModel.fromJson(response.data);
  }

  /// 取消预约
  Future<AppointmentModel> cancelAppointment({
    required int appointmentId,
    String? cancelReason,
  }) async {
    final response = await _apiClient.post(
      '/api/v1/appointments/cancel',
      data: {
        'appointmentId': appointmentId,
        'cancelReason': cancelReason,
      },
    );
    return AppointmentModel.fromJson(response.data);
  }

  /// 获取预约详情
  Future<AppointmentModel> getAppointmentDetail(int appointmentId) async {
    final response = await _apiClient.get(
      '/api/v1/appointments/$appointmentId',
    );
    return AppointmentModel.fromJson(response.data);
  }

  /// 获取我的预约列表
  Future<List<AppointmentModel>> getMyAppointments({
    String? status,
    int page = 0,
    int size = 20,
  }) async {
    final response = await _apiClient.get(
      '/api/v1/appointments/my',
      queryParameters: {
        if (status != null) 'status': status,
        'page': page,
        'size': size,
      },
    );
    final List<dynamic> data = response.data['content'];
    return data.map((e) => AppointmentModel.fromJson(e)).toList();
  }

  /// 查询可预约时段
  Future<List<AvailableTimeSlot>> queryAvailableSlots({
    required int merchantId,
    required int serviceId,
    int days = 7,
  }) async {
    final response = await _apiClient.get(
      '/api/v1/appointments/available-slots',
      queryParameters: {
        'merchantId': merchantId,
        'serviceId': serviceId,
        'days': days,
      },
    );
    final List<dynamic> data = response.data;
    return data.map((e) => AvailableTimeSlot.fromJson(e)).toList();
  }

  /// 用户到店签到
  Future<AppointmentModel> checkIn(int appointmentId) async {
    final response = await _apiClient.post(
      '/api/v1/appointments/$appointmentId/check-in',
    );
    return AppointmentModel.fromJson(response.data);
  }

  /// 获取排队票号列表
  Future<List<QueueTicketModel>> getMyQueues({String? status}) async {
    final response = await _apiClient.get(
      '/api/v1/queue/my',
      queryParameters: {
        if (status != null) 'status': status,
      },
    );
    final List<dynamic> data = response.data;
    return data.map((e) => QueueTicketModel.fromJson(e)).toList();
  }

  /// 远程取号
  Future<QueueTicketModel> takeQueue({
    required int merchantId,
    required String queueType,
    required int peopleCount,
    String? tableType,
    required String contactName,
    required String contactPhone,
    String? remark,
    double? userLatitude,
    double? userLongitude,
    String? source,
  }) async {
    final response = await _apiClient.post(
      '/api/v1/queue/take',
      data: {
        'merchantId': merchantId,
        'queueType': queueType,
        'peopleCount': peopleCount,
        'tableType': tableType,
        'contactName': contactName,
        'contactPhone': contactPhone,
        'remark': remark,
        'userLatitude': userLatitude,
        'userLongitude': userLongitude,
        'source': source,
      },
    );
    return QueueTicketModel.fromJson(response.data);
  }

  /// 取消排队
  Future<QueueTicketModel> cancelQueue(int ticketId) async {
    final response = await _apiClient.post(
      '/api/v1/queue/$ticketId/cancel',
    );
    return QueueTicketModel.fromJson(response.data);
  }

  /// 获取排队详情
  Future<QueueTicketModel> getQueueDetail(int ticketId) async {
    final response = await _apiClient.get(
      '/api/v1/queue/$ticketId',
    );
    return QueueTicketModel.fromJson(response.data);
  }

  /// 用户确认到达
  Future<QueueTicketModel> confirmArrive(int ticketId) async {
    final response = await _apiClient.post(
      '/api/v1/queue/$ticketId/arrive',
    );
    return QueueTicketModel.fromJson(response.data);
  }

  /// 获取商户排队状态
  Future<List<dynamic>> getMerchantQueueStatus(int merchantId) async {
    final response = await _apiClient.get(
      '/api/v1/queue/merchant/$merchantId/status',
    );
    return response.data;
  }

  /// 获取商户各队列等待人数
  Future<List<dynamic>> getQueueTypeStatus(int merchantId) async {
    final response = await _apiClient.get(
      '/api/v1/queue/merchant/$merchantId/queue-types',
    );
    return response.data;
  }
}
