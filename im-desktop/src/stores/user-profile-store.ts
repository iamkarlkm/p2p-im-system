/**
 * 用户资料状态管理 (Pinia Store)
 */

import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import {
  UserProfile,
  OnlineStatus,
  FriendGroup,
  FriendRemark,
  OnlineStatusLabels
} from '../types/user-profile';
import { userProfileService } from '../services/user-profile-service';

export const useUserProfileStore = defineStore('userProfile', () => {
  // 状态
  const myProfile = ref<UserProfile | null>(null);
  const profileCache = ref<Map<number, UserProfile>>(new Map());
  const friendGroups = ref<FriendGroup[]>([]);
  const friendRemarks = ref<Map<number, FriendRemark>>(new Map());
  const isLoading = ref(false);

  // 计算属性
  const myNickname = computed(() => myProfile.value?.nickname || '未知用户');
  const myAvatar = computed(() => myProfile.value?.avatarUrl || '/default-avatar.png');
  const myOnlineStatus = computed(() => myProfile.value?.onlineStatus || 'OFFLINE');

  // 获取用户显示名称（优先显示备注名）
  function getDisplayName(userId: number, defaultName: string): string {
    const remark = friendRemarks.value.get(userId);
    return remark?.remarkName || defaultName;
  }

  // 获取用户头像
  function getAvatar(userId: number): string {
    const profile = profileCache.value.get(userId);
    return profile?.avatarUrl || '/default-avatar.png';
  }

  // 获取用户在线状态
  function getOnlineStatus(userId: number): OnlineStatus {
    const profile = profileCache.value.get(userId);
    return profile?.onlineStatus || 'OFFLINE';
  }

  // 获取用户状态文本
  function getStatusText(userId: number): string | undefined {
    const profile = profileCache.value.get(userId);
    return profile?.statusText;
  }

  // 获取用户在线状态标签
  function getStatusLabel(userId: number): string {
    const status = getOnlineStatus(userId);
    return OnlineStatusLabels[status] || '离线';
  }

  // 行动：加载我的资料
  async function loadMyProfile() {
    isLoading.value = true;
    try {
      myProfile.value = await userProfileService.getMyProfile();
      profileCache.value.set(myProfile.value.userId, myProfile.value);
    } catch (error) {
      console.error('加载个人资料失败:', error);
    } finally {
      isLoading.value = false;
    }
  }

  // 行动：加载用户资料（单个）
  async function loadProfile(userId: number) {
    if (profileCache.value.has(userId)) return;
    try {
      const profile = await userProfileService.getProfile(userId);
      profileCache.value.set(userId, profile);
    } catch (error) {
      console.error('加载用户资料失败:', error);
    }
  }

  // 行动：批量加载用户资料
  async function loadProfiles(userIds: number[]) {
    const uncachedIds = userIds.filter(id => !profileCache.value.has(id));
    if (uncachedIds.length === 0) return;
    try {
      const profiles = await userProfileService.getProfiles(uncachedIds);
      profiles.forEach(p => profileCache.value.set(p.userId, p));
    } catch (error) {
      console.error('批量加载用户资料失败:', error);
    }
  }

  // 行动：更新个人资料
  async function updateMyProfile(data: Partial<UserProfile>) {
    try {
      const updated = await userProfileService.updateProfile(data);
      myProfile.value = updated;
      profileCache.value.set(updated.userId, updated);
    } catch (error) {
      console.error('更新个人资料失败:', error);
      throw error;
    }
  }

  // 行动：更新在线状态
  async function updateOnlineStatus(status: OnlineStatus, statusText?: string) {
    try {
      const updated = await userProfileService.updateOnlineStatus({ status, statusText });
      myProfile.value = updated;
      profileCache.value.set(updated.userId, updated);
    } catch (error) {
      console.error('更新在线状态失败:', error);
      throw error;
    }
  }

  // 行动：上传头像
  async function uploadAvatar(fileData: string) {
    try {
      const avatarUrl = await userProfileService.uploadAvatar(fileData);
      if (myProfile.value) {
        myProfile.value.avatarUrl = avatarUrl;
        profileCache.value.set(myProfile.value.userId, myProfile.value);
      }
      return avatarUrl;
    } catch (error) {
      console.error('上传头像失败:', error);
      throw error;
    }
  }

  // 行动：加载好友分组
  async function loadFriendGroups() {
    try {
      friendGroups.value = await userProfileService.getFriendGroups();
    } catch (error) {
      console.error('加载好友分组失败:', error);
    }
  }

  // 行动：创建好友分组
  async function createFriendGroup(groupName: string) {
    try {
      const group = await userProfileService.createFriendGroup(groupName);
      friendGroups.value.push(group);
      return group;
    } catch (error) {
      console.error('创建好友分组失败:', error);
      throw error;
    }
  }

  // 行动：更新好友备注
  async function updateFriendRemark(friendId: number, data: Partial<FriendRemark>) {
    try {
      const remark = await userProfileService.updateFriendRemark({ friendId, ...data });
      friendRemarks.value.set(friendId, remark);
      return remark;
    } catch (error) {
      console.error('更新好友备注失败:', error);
      throw error;
    }
  }

  // 行动：处理WebSocket状态更新
  function handleStatusChange(userId: number, status: OnlineStatus, statusText?: string) {
    const profile = profileCache.value.get(userId);
    if (profile) {
      profile.onlineStatus = status;
      profile.statusText = statusText;
    }
  }

  return {
    // 状态
    myProfile,
    profileCache,
    friendGroups,
    friendRemarks,
    isLoading,
    // 计算属性
    myNickname,
    myAvatar,
    myOnlineStatus,
    // 方法
    getDisplayName,
    getAvatar,
    getOnlineStatus,
    getStatusText,
    getStatusLabel,
    loadMyProfile,
    loadProfile,
    loadProfiles,
    updateMyProfile,
    updateOnlineStatus,
    uploadAvatar,
    loadFriendGroups,
    createFriendGroup,
    updateFriendRemark,
    handleStatusChange,
  };
});
