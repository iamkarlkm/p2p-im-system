import 'package:json_annotation/json_annotation.dart';

part 'friend_group.g.dart';

@JsonSerializable()
class FriendGroup {
  final String id;
  final String userId;
  final String name;
  final int sortOrder;
  final DateTime createdAt;
  final DateTime updatedAt;
  final List<String>? memberIds;
  final int? memberCount;

  FriendGroup({
    required this.id,
    required this.userId,
    required this.name,
    this.sortOrder = 0,
    required this.createdAt,
    required this.updatedAt,
    this.memberIds,
    this.memberCount,
  });

  factory FriendGroup.fromJson(Map<String, dynamic> json) =>
      _$FriendGroupFromJson(json);
  Map<String, dynamic> toJson() => _$FriendGroupToJson(this);

  FriendGroup copyWith({
    String? id,
    String? userId,
    String? name,
    int? sortOrder,
    DateTime? createdAt,
    DateTime? updatedAt,
    List<String>? memberIds,
    int? memberCount,
  }) {
    return FriendGroup(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      name: name ?? this.name,
      sortOrder: sortOrder ?? this.sortOrder,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      memberIds: memberIds ?? this.memberIds,
      memberCount: memberCount ?? this.memberCount,
    );
  }
}

@JsonSerializable()
class FriendGroupMember {
  final String id;
  final String groupId;
  final String friendId;
  final String friendName;
  final String? friendAvatar;
  final bool isStarred;
  final bool isMuted;
  final DateTime addedAt;

  FriendGroupMember({
    required this.id,
    required this.groupId,
    required this.friendId,
    required this.friendName,
    this.friendAvatar,
    this.isStarred = false,
    this.isMuted = false,
    required this.addedAt,
  });

  factory FriendGroupMember.fromJson(Map<String, dynamic> json) =>
      _$FriendGroupMemberFromJson(json);
  Map<String, dynamic> toJson() => _$FriendGroupMemberToJson(this);

  FriendGroupMember copyWith({
    String? id,
    String? groupId,
    String? friendId,
    String? friendName,
    String? friendAvatar,
    bool? isStarred,
    bool? isMuted,
    DateTime? addedAt,
  }) {
    return FriendGroupMember(
      id: id ?? this.id,
      groupId: groupId ?? this.groupId,
      friendId: friendId ?? this.friendId,
      friendName: friendName ?? this.friendName,
      friendAvatar: friendAvatar ?? this.friendAvatar,
      isStarred: isStarred ?? this.isStarred,
      isMuted: isMuted ?? this.isMuted,
      addedAt: addedAt ?? this.addedAt,
    );
  }
}

@JsonSerializable()
class CreateGroupRequest {
  final String name;
  final int? sortOrder;

  CreateGroupRequest({
    required this.name,
    this.sortOrder,
  });

  factory CreateGroupRequest.fromJson(Map<String, dynamic> json) =>
      _$CreateGroupRequestFromJson(json);
  Map<String, dynamic> toJson() => _$CreateGroupRequestToJson(this);
}

@JsonSerializable()
class UpdateGroupRequest {
  final String? name;
  final int? sortOrder;

  UpdateGroupRequest({
    this.name,
    this.sortOrder,
  });

  factory UpdateGroupRequest.fromJson(Map<String, dynamic> json) =>
      _$UpdateGroupRequestFromJson(json);
  Map<String, dynamic> toJson() => _$UpdateGroupRequestToJson(this);
}
