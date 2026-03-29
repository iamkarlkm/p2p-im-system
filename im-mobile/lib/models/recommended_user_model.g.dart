// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'recommended_user_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

RecommendedUserModel _$RecommendedUserModelFromJson(Map<String, dynamic> json) =>
    RecommendedUserModel(
      userId: json['userId'] as String,
      nickname: json['nickname'] as String,
      avatar: json['avatar'] as String,
      reasonType: json['reasonType'] as String,
      reasonDescription: json['reasonDescription'] as String,
      score: (json['score'] as num).toDouble(),
      mutualFriendsCount: (json['mutualFriendsCount'] as num?)?.toInt(),
      commonTags: (json['commonTags'] as List<dynamic>?)
          ?.map((e) => e as String)
          .toList(),
      commonGroups: (json['commonGroups'] as List<dynamic>?)
          ?.map((e) => e as String)
          .toList(),
    );

Map<String, dynamic> _$RecommendedUserModelToJson(RecommendedUserModel instance) =>
    <String, dynamic>{
      'userId': instance.userId,
      'nickname': instance.nickname,
      'avatar': instance.avatar,
      'reasonType': instance.reasonType,
      'reasonDescription': instance.reasonDescription,
      'score': instance.score,
      'mutualFriendsCount': instance.mutualFriendsCount,
      'commonTags': instance.commonTags,
      'commonGroups': instance.commonGroups,
    };
