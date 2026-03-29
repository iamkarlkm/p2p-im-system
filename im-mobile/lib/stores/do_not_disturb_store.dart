// stores/do_not_disturb_store.dart
import 'package:mobx/mobx.dart';
import '../models/do_not_disturb_period_model.dart';
import '../services/api_service.dart';

part 'do_not_disturb_store.g.dart';

class DoNotDisturbStore = _DoNotDisturbStore with _$DoNotDisturbStore;

abstract class _DoNotDisturbStore with Store {
  final ApiService _apiService = ApiService();

  @observable
  ObservableList<DoNotDisturbPeriodModel> periods = ObservableList<DoNotDisturbPeriodModel>();

  @observable
  bool isLoading = false;

  @observable
  String? error;

  @observable
  bool isGlobalEnabled = true;

  @computed
  List<DoNotDisturbPeriodModel> get enabledPeriods =>
      periods.where((p) => p.isEnabled).toList();

  @computed
  List<DoNotDisturbPeriodModel> get activePeriods =>
      periods.where((p) => p.isCurrentlyActive()).toList();

  @computed
  bool get isInDoNotDisturbMode => 
      isGlobalEnabled && activePeriods.isNotEmpty;

  @computed
  bool get shouldAllowCalls {
    if (!isInDoNotDisturbMode) return true;
    return activePeriods.every((p) => p.allowCalls);
  }

  @computed
  bool get shouldAllowMentions {
    if (!isInDoNotDisturbMode) return true;
    return activePeriods.any((p) => p.allowMentions);
  }

  @action
  Future<void> loadPeriods() async {
    isLoading = true;
    error = null;
    
    try {
      final response = await _apiService.get('/do-not-disturb/periods');;
      final List<dynamic> data = response['data'] ?? [];
      periods = ObservableList.of(
        data.map((json) => DoNotDisturbPeriodModel.fromJson(json)),
      );
    } catch (e) {
      error = '加载免打扰时段失败: $e';
    } finally {
      isLoading = false;
    }
  }

  @action
  Future<void> createPeriod(DoNotDisturbPeriodModel period) async {
    isLoading = true;
    error = null;
    
    try {
      final response = await _apiService.post(
        '/do-not-disturb/periods',
        data: period.toJson(),
      );
      final newPeriod = DoNotDisturbPeriodModel.fromJson(response['data']);
      periods.add(newPeriod);
    } catch (e) {
      error = '创建免打扰时段失败: $e';
      throw e;
    } finally {
      isLoading = false;
    }
  }

  @action
  Future<void> updatePeriod(DoNotDisturbPeriodModel period) async {
    isLoading = true;
    error = null;
    
    try {
      final response = await _apiService.put(
        '/do-not-disturb/periods/${period.id}',
        data: period.toJson(),
      );
      final updatedPeriod = DoNotDisturbPeriodModel.fromJson(response['data']);
      final index = periods.indexWhere((p) => p.id == period.id);
      if (index != -1) {
        periods[index] = updatedPeriod;
      }
    } catch (e) {
      error = '更新免打扰时段失败: $e';
      throw e;
    } finally {
      isLoading = false;
    }
  }

  @action
  Future<void> deletePeriod(String periodId) async {
    isLoading = true;
    error = null;
    
    try {
      await _apiService.delete('/do-not-disturb/periods/$periodId');
      periods.removeWhere((p) => p.id == periodId);
    } catch (e) {
      error = '删除免打扰时段失败: $e';
      throw e;
    } finally {
      isLoading = false;
    }
  }

  @action
  Future<void> togglePeriod(String periodId, bool isEnabled) async {
    final period = periods.firstWhere((p) => p.id == periodId);
    final updatedPeriod = period.copyWith(isEnabled: isEnabled);
    await updatePeriod(updatedPeriod);
  }

  @action
  Future<void> toggleGlobalSetting(bool enabled) async {
    isLoading = true;
    error = null;
    
    try {
      await _apiService.patch('/do-not-disturb/settings', data: {
        'isGlobalEnabled': enabled,
      });
      isGlobalEnabled = enabled;
    } catch (e) {
      error = '更新全局设置失败: $e';
    } finally {
      isLoading = false;
    }
  }

  @action
  void clearError() {
    error = null;
  }
}
