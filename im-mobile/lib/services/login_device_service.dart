import '../models/login_device.dart';
import '../services/api_service.dart';

class LoginDeviceService {
  final ApiService _api = ApiService();

  Future<List<LoginDevice>> getDevices() async {
    final response = await _api.get('/api/v1/security/devices');
    return (response as List)
        .map((json) => LoginDevice.fromJson(json))
        .toList();
  }

  Future<void> terminateDevice(String deviceId) async {
    await _api.post('/api/v1/security/devices/$deviceId/terminate', {});
  }

  Future<void> trustDevice(String deviceId) async {
    await _api.post('/api/v1/security/devices/$deviceId/trust', {});
  }

  Future<void> terminateAllOthers(String currentDeviceId) async {
    await _api.post('/api/v1/security/devices/terminate-others?currentDeviceId=$currentDeviceId', {});
  }
}
