import 'package:flutter_test/flutter_test.dart';
import 'package:im_mobile/models/recommended_user_model.dart';

void main() {
  group('RecommendedUserModel', () {
    test('should create model from JSON', () {
      final json = {
        'userId': 'user123',
        'nickname': 'Test User',
        'avatar': 'https://example.com/avatar.jpg',
        'reasonType': 'mutual_friends',
        'reasonDescription': '3个共同好友',
        'score': 0.85,
        'mutualFriendsCount': 3,
        'commonTags': ['技术', '游戏'],
        'commonGroups': ['Flutter开发者'],
      };

      final model = RecommendedUserModel.fromJson(json);

      expect(model.userId, 'user123');
      expect(model.nickname, 'Test User');
      expect(model.reasonType, 'mutual_friends');
      expect(model.score, 0.85);
      expect(model.mutualFriendsCount, 3);
      expect(model.commonTags, ['技术', '游戏']);
    });

    test('should convert model to JSON', () {
      final model = RecommendedUserModel(
        userId: 'user456',
        nickname: 'Another User',
        avatar: '',
        reasonType: 'interest_tags',
        reasonDescription: '相似兴趣',
        score: 0.72,
        commonTags: ['音乐', '运动'],
      );

      final json = model.toJson();

      expect(json['userId'], 'user456');
      expect(json['reasonType'], 'interest_tags');
      expect(json['score'], 0.72);
    });

    test('should handle null optional fields', () {
      final json = {
        'userId': 'user789',
        'nickname': 'Minimal User',
        'avatar': '',
        'reasonType': 'mixed',
        'reasonDescription': '综合推荐',
        'score': 0.60,
      };

      final model = RecommendedUserModel.fromJson(json);

      expect(model.mutualFriendsCount, null);
      expect(model.commonTags, null);
      expect(model.commonGroups, null);
    });
  });
}
