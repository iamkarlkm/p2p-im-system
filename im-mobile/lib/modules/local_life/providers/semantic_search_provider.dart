import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/semantic_search_result.dart';
import '../services/semantic_search_service.dart';

/// 语义搜索状态
class SemanticSearchState {
  final List<SemanticSearchResult> results;
  final bool isLoading;
  final String? error;
  final String? currentQuery;
  final List<String> suggestions;
  final List<String> hotSearches;
  final SearchIntent? lastIntent;

  const SemanticSearchState({
    this.results = const [],
    this.isLoading = false,
    this.error,
    this.currentQuery,
    this.suggestions = const [],
    this.hotSearches = const [],
    this.lastIntent,
  });

  SemanticSearchState copyWith({
    List<SemanticSearchResult>? results,
    bool? isLoading,
    String? error,
    String? currentQuery,
    List<String>? suggestions,
    List<String>? hotSearches,
    SearchIntent? lastIntent,
  }) {
    return SemanticSearchState(
      results: results ?? this.results,
      isLoading: isLoading ?? this.isLoading,
      error: error ?? this.error,
      currentQuery: currentQuery ?? this.currentQuery,
      suggestions: suggestions ?? this.suggestions,
      hotSearches: hotSearches ?? this.hotSearches,
      lastIntent: lastIntent ?? this.lastIntent,
    );
  }
}

/// 搜索意图模型
class SearchIntent {
  final String intentType;
  final String intentTypeLabel;
  final double confidenceScore;
  final Map<String, dynamic> extractedEntities;
  final List<String> poiCategories;
  final String? locationDesc;
  final String? priceConstraint;
  final bool needsClarification;
  final List<String>? clarificationQuestions;

  SearchIntent({
    required this.intentType,
    required this.intentTypeLabel,
    required this.confidenceScore,
    required this.extractedEntities,
    required this.poiCategories,
    this.locationDesc,
    this.priceConstraint,
    this.needsClarification = false,
    this.clarificationQuestions,
  });

  factory SearchIntent.fromJson(Map<String, dynamic> json) {
    return SearchIntent(
      intentType: json['intentType'] ?? 'UNKNOWN',
      intentTypeLabel: json['intentTypeLabel'] ?? '未知',
      confidenceScore: (json['confidenceScore'] ?? 0.0).toDouble(),
      extractedEntities: json['extractedEntities'] ?? {},
      poiCategories: List<String>.from(json['poiCategories'] ?? []),
      locationDesc: json['locationDesc'],
      priceConstraint: json['priceConstraint'],
      needsClarification: json['needsClarification'] ?? false,
      clarificationQuestions: json['clarificationQuestions'] != null
          ? List<String>.from(json['clarificationQuestions'])
          : null,
    );
  }
}

/// 语义搜索Provider
final semanticSearchProvider = StateNotifierProvider<SemanticSearchNotifier, SemanticSearchState>(
  (ref) => SemanticSearchNotifier(),
);

class SemanticSearchNotifier extends StateNotifier<SemanticSearchState> {
  final SemanticSearchService _service = SemanticSearchService();

  SemanticSearchNotifier() : super(const SemanticSearchState());

  /// 执行语义搜索
  Future<void> search(String query, {
    double? latitude,
    double? longitude,
    int radius = 3000,
    String? sessionId,
  }) async {
    state = state.copyWith(isLoading: true, error: null, currentQuery: query);

    try {
      final results = await _service.search(
        query: query,
        latitude: latitude,
        longitude: longitude,
        radius: radius,
        sessionId: sessionId,
      );

      state = state.copyWith(
        results: results,
        isLoading: false,
      );
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.toString(),
      );
    }
  }

  /// 识别搜索意图
  Future<SearchIntent?> recognizeIntent(String query, {String? sessionId}) async {
    try {
      final intent = await _service.recognizeIntent(query, sessionId: sessionId);
      state = state.copyWith(lastIntent: intent);
      return intent;
    } catch (e) {
      return null;
    }
  }

  /// 获取搜索建议
  Future<void> getSuggestions(String query, {double? latitude, double? longitude}) async {
    if (query.isEmpty) {
      state = state.copyWith(suggestions: []);
      return;
    }

    try {
      final suggestions = await _service.getSuggestions(
        query: query,
        latitude: latitude,
        longitude: longitude,
      );
      state = state.copyWith(suggestions: suggestions);
    } catch (e) {
      // 忽略错误
    }
  }

  /// 获取热门搜索
  Future<void> getHotSearches({double? latitude, double? longitude}) async {
    try {
      final hotSearches = await _service.getHotSearches(
        latitude: latitude,
        longitude: longitude,
      );
      state = state.copyWith(hotSearches: hotSearches);
    } catch (e) {
      // 忽略错误
    }
  }

  /// 清除搜索结果
  void clearResults() {
    state = state.copyWith(results: [], currentQuery: null);
  }

  /// 清除错误
  void clearError() {
    state = state.copyWith(error: null);
  }
}

/// 搜索历史Provider
final searchHistoryProvider = FutureProvider<List<String>>((ref) async {
  final service = SemanticSearchService();
  return await service.getSearchHistory();
});

/// 当前位置Provider
final userLocationProvider = StateProvider<Map<String, double>?>((ref) => null);
