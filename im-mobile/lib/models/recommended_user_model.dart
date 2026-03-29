import 'package:json_annotation/json_annotation.dart';

part 'recommended_user_model.g.dart';

@JsonSerializable()
class RecommendedUserModel {
  final String userId;
  final String nickname;
  final String avatar;
  final String reasonType;
  final String reasonDescription;
  final double score;
  final int? mutualFriendsCount;
  final List<String>? commonTags;
  final List<String>? commonGroups;

  RecommendedUserModel({
    required this.userId,
    required this.nickname,
    required this.avatar,
    required this.reasonType,
    required this.reasonDescription,
    required this.score,
    this.mutualFriendsCount,
    this.commonTags,
    this.commonGroups,
  });

  factory RecommendedUserModel.fromJson(Map<String, dynamic> json) =>
      _$RecommendedUserModelFromJson(json);

  Map<String, dynamic> toJson() => _$RecommendedUserModelToJson(this);
}
