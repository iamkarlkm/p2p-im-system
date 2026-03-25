import '../models/login_anomaly_alert.dart';
import '../services/api_service.dart';

class LoginAnomalyAlertService {
  final ApiService _api = ApiService();

  Future<List<LoginAnomalyAlert>> getAlerts() async {
    final response = await _api.get('/api/v1/security/login-alerts');
    return (response as List)
        .map((json) => LoginAnomalyAlert.fromJson(json))
        .toList();
  }

  Future<List<LoginAnomalyAlert>> getPendingAlerts() async {
    final response = await _api.get('/api/v1/security/login-alerts/pending');
    return (response as List)
        .map((json) => LoginAnomalyAlert.fromJson(json))
        .toList();
  }

  Future<LoginAnomalyAlert> confirmAlert(int alertId) async {
    final response = await _api.post('/api/v1/security/login-alerts/$alertId/confirm', {});
    return LoginAnomalyAlert.fromJson(response);
  }

  Future<LoginAnomalyAlert> dismissAlert(int alertId) async {
    final response = await _api.post('/api/v1/security/login-alerts/$alertId/dismiss', {});
    return LoginAnomalyAlert.fromJson(response);
  }
}
