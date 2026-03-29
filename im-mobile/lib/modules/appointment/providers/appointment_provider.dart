import 'package:flutter/foundation.dart';
import '../models/appointment_models.dart';
import '../services/appointment_service.dart';

/// 预约状态管理类
class AppointmentProvider extends ChangeNotifier {
  final AppointmentService _service = AppointmentService();

  List<AppointmentModel> _appointments = [];
  List<QueueTicketModel> _queueTickets = [];
  List<AvailableTimeSlot> _availableSlots = [];
  AppointmentModel? _currentAppointment;
  QueueTicketModel? _currentQueueTicket;
  bool _isLoading = false;
  String? _error;

  // Getters
  List<AppointmentModel> get appointments => _appointments;
  List<QueueTicketModel> get queueTickets => _queueTickets;
  List<AvailableTimeSlot> get availableSlots => _availableSlots;
  AppointmentModel? get currentAppointment => _currentAppointment;
  QueueTicketModel? get currentQueueTicket => _currentQueueTicket;
  bool get isLoading => _isLoading;
  String? get error => _error;

  /// 加载我的预约列表
  Future<void> loadMyAppointments({String? status}) async {
    _setLoading(true);
    try {
      _appointments = await _service.getMyAppointments(status: status);
      _error = null;
    } catch (e) {
      _error = e.toString();
    } finally {
      _setLoading(false);
    }
  }

  /// 加载我的排队列表
  Future<void> loadMyQueues({String? status}) async {
    _setLoading(true);
    try {
      _queueTickets = await _service.getMyQueues(status: status);
      _error = null;
    } catch (e) {
      _error = e.toString();
    } finally {
      _setLoading(false);
    }
  }

  /// 查询可预约时段
  Future<void> queryAvailableSlots({
    required int merchantId,
    required int serviceId,
    int days = 7,
  }) async {
    _setLoading(true);
    try {
      _availableSlots = await _service.queryAvailableSlots(
        merchantId: merchantId,
        serviceId: serviceId,
        days: days,
      );
      _error = null;
    } catch (e) {
      _error = e.toString();
    } finally {
      _setLoading(false);
    }
  }

  /// 提交预约
  Future<bool> submitAppointment({
    required int merchantId,
    required int serviceId,
    required DateTime appointmentDate,
    required String startTime,
    String? endTime,
    required String contactName,
    required String contactPhone,
    String? remark,
    int numberOfPeople = 1,
  }) async {
    _setLoading(true);
    try {
      _currentAppointment = await _service.submitAppointment(
        merchantId: merchantId,
        serviceId: serviceId,
        appointmentDate: appointmentDate,
        startTime: startTime,
        endTime: endTime,
        contactName: contactName,
        contactPhone: contactPhone,
        remark: remark,
        numberOfPeople: numberOfPeople,
      );
      _error = null;
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _setLoading(false);
    }
  }

  /// 取消预约
  Future<bool> cancelAppointment(int appointmentId, {String? reason}) async {
    _setLoading(true);
    try {
      await _service.cancelAppointment(
        appointmentId: appointmentId,
        cancelReason: reason,
      );
      _error = null;
      await loadMyAppointments();
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _setLoading(false);
    }
  }

  /// 获取预约详情
  Future<void> getAppointmentDetail(int appointmentId) async {
    _setLoading(true);
    try {
      _currentAppointment = await _service.getAppointmentDetail(appointmentId);
      _error = null;
    } catch (e) {
      _error = e.toString();
    } finally {
      _setLoading(false);
    }
  }

  /// 用户到店签到
  Future<bool> checkIn(int appointmentId) async {
    _setLoading(true);
    try {
      _currentAppointment = await _service.checkIn(appointmentId);
      _error = null;
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _setLoading(false);
    }
  }

  /// 远程取号
  Future<bool> takeQueue({
    required int merchantId,
    required String queueType,
    required int peopleCount,
    String? tableType,
    required String contactName,
    required String contactPhone,
    String? remark,
  }) async {
    _setLoading(true);
    try {
      _currentQueueTicket = await _service.takeQueue(
        merchantId: merchantId,
        queueType: queueType,
        peopleCount: peopleCount,
        tableType: tableType,
        contactName: contactName,
        contactPhone: contactPhone,
        remark: remark,
      );
      _error = null;
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _setLoading(false);
    }
  }

  /// 取消排队
  Future<bool> cancelQueue(int ticketId) async {
    _setLoading(true);
    try {
      await _service.cancelQueue(ticketId);
      _error = null;
      await loadMyQueues();
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _setLoading(false);
    }
  }

  /// 获取排队详情
  Future<void> getQueueDetail(int ticketId) async {
    _setLoading(true);
    try {
      _currentQueueTicket = await _service.getQueueDetail(ticketId);
      _error = null;
    } catch (e) {
      _error = e.toString();
    } finally {
      _setLoading(false);
    }
  }

  /// 用户确认到达
  Future<bool> confirmArrive(int ticketId) async {
    _setLoading(true);
    try {
      _currentQueueTicket = await _service.confirmArrive(ticketId);
      _error = null;
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      return false;
    } finally {
      _setLoading(false);
    }
  }

  /// 刷新当前排队状态
  Future<void> refreshCurrentQueue() async {
    if (_currentQueueTicket != null) {
      await getQueueDetail(_currentQueueTicket!.id);
    }
  }

  void _setLoading(bool value) {
    _isLoading = value;
    notifyListeners();
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }

  void clearCurrentAppointment() {
    _currentAppointment = null;
    notifyListeners();
  }

  void clearCurrentQueue() {
    _currentQueueTicket = null;
    notifyListeners();
  }
}
