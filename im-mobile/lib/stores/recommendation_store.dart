import 'package:mobx/mobx.dart';
import 'package:im_mobile/models/recommended_user_model.dart';
import 'package:im_mobile/services/friend_recommendation_service.dart';

part 'recommendation_store.g.dart';

class RecommendationStore = _RecommendationStore with _$RecommendationStore;

abstract class _RecommendationStore with Store {
  final FriendRecommendationService _service = FriendRecommendationService();

  @observable
  ObservableList<RecommendedUserModel> recommendations = ObservableList<RecommendedUserModel>();

  @observable
  bool isLoading = false;

  @observable
  String? error;

  @observable
  int currentPage = 1;

  @observable
  bool hasMore = true;

  @action
  Future<void> loadRecommendations({
    String? algorithm,
    bool refresh = false,
  }) async {
    if (isLoading) return;

    isLoading = true;
    error = null;

    if (refresh) {
      currentPage = 1;
      recommendations.clear();
    }

    try {
      final result = await _service.getRecommendations(
        page: currentPage,
        size: 20,
        algorithm: algorithm,
      );

      if (refresh) {
        recommendations = ObservableList.of(result);
      } else {
        recommendations.addAll(result);
      }

      hasMore = result.length == 20;
      currentPage++;
    } catch (e) {
      error = e.toString();
    } finally {
      isLoading = false;
    }
  }

  @action
  Future<void> sendFriendRequest(String userId) async {
    try {
      await _service.sendFriendRequest(userId);
      recommendations.removeWhere((r) => r.userId == userId);
    } catch (e) {
      error = e.toString();
      throw e;
    }
  }

  @action
  Future<void> ignoreRecommendation(String userId) async {
    try {
      await _service.ignoreRecommendation(userId);
      recommendations.removeWhere((r) => r.userId == userId);
    } catch (e) {
      error = e.toString();
      throw e;
    }
  }

  @action
  void clearError() {
    error = null;
  }
}
