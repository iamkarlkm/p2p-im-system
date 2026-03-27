/**
 * 好友分组类型定义
 */

export interface FriendGroup {
  id: string;
  userId: string;
  name: string;
  sortOrder: number;
  isDefault: boolean;
  memberCount: number;
  createdAt: number;
  updatedAt: number;
}

export interface FriendGroupMember {
  id: string;
  groupId: string;
  friendId: string;
  friendName: string;
  friendAvatar?: string;
  sortOrder: number;
  isStarred: boolean;
  isMuted: boolean;
  addedAt: number;
}

export interface FriendGroupWithMembers extends FriendGroup {
  members: FriendGroupMember[];
}

export interface CreateGroupRequest {
  name: string;
  sortOrder?: number;
}

export interface UpdateGroupRequest {
  name?: string;
  sortOrder?: number;
}

export interface MoveFriendToGroupRequest {
  friendId: string;
  targetGroupId: string;
}

export interface UpdateMemberRequest {
  isStarred?: boolean;
  isMuted?: boolean;
  sortOrder?: number;
}
