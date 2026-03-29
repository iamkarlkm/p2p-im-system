<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="avatar-section" @click="triggerAvatarUpload">
        <img :src="myAvatar" class="avatar" alt="头像" />
        <div class="avatar-overlay">
          <span>更换头像</span>
        </div>
        <input
          ref="avatarInput"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleAvatarChange"
        />
      </div>
      <div class="user-info">
        <h2>{{ myNickname }}</h2>
        <div class="status-badge" :style="{ color: statusColor }">
          <span class="status-dot" :style="{ backgroundColor: statusColor }"></span>
          {{ statusLabel }}
        </div>
      </div>
    </div>

    <div class="profile-form">
      <!-- 基本信息 -->
      <div class="form-section">
        <h3>基本信息</h3>
        <div class="form-item">
          <label>昵称</label>
          <input v-model="form.nickname" placeholder="输入昵称" />
        </div>
        <div class="form-item">
          <label>个性签名</label>
          <textarea v-model="form.bio" placeholder="介绍一下自己" rows="3"></textarea>
        </div>
        <div class="form-item">
          <label>性别</label>
          <select v-model="form.gender">
            <option :value="0">未知</option>
            <option :value="1">男</option>
            <option :value="2">女</option>
          </select>
        </div>
        <div class="form-item">
          <label>生日</label>
          <input v-model="form.birthday" type="date" />
        </div>
        <div class="form-item">
          <label>邮箱</label>
          <input v-model="form.email" type="email" placeholder="example@email.com" />
        </div>
      </div>

      <!-- 在线状态 -->
      <div class="form-section">
        <h3>在线状态</h3>
        <div class="status-grid">
          <div
            v-for="status in statusOptions"
            :key="status.value"
            class="status-option"
            :class="{ active: form.onlineStatus === status.value }"
            @click="setStatus(status.value)"
          >
            <span class="status-icon" :style="{ backgroundColor: status.color }"></span>
            <span>{{ status.label }}</span>
          </div>
        </div>
        <div class="form-item" v-if="form.onlineStatus === 'CUSTOM'">
          <label>自定义状态文本</label>
          <input v-model="form.statusText" placeholder="输入自定义状态" />
        </div>
      </div>

      <!-- 位置信息 -->
      <div class="form-section">
        <h3>位置信息</h3>
        <div class="form-item">
          <label>国家/地区</label>
          <input v-model="form.country" placeholder="国家/地区" />
        </div>
        <div class="form-item">
          <label>城市</label>
          <input v-model="form.city" placeholder="城市" />
        </div>
      </div>

      <div class="form-actions">
        <button class="btn-secondary" @click="resetForm">重置</button>
        <button class="btn-primary" @click="saveProfile" :disabled="isSaving">
          {{ isSaving ? '保存中...' : '保存修改' }}
        </button>
      </div>
    </div>

    <!-- 好友分组管理 -->
    <div class="friend-groups-section">
      <h3>好友分组</h3>
      <div class="groups-list">
        <div v-for="group in friendGroups" :key="group.id" class="group-item">
          <span>{{ group.groupName }}</span>
          <span class="group-count">0 位好友</span>
        </div>
      </div>
      <div class="add-group">
        <input v-model="newGroupName" placeholder="新分组名称" />
        <button @click="addGroup">添加</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useUserProfileStore } from '../stores/user-profile-store';
import { OnlineStatus, OnlineStatusLabels, OnlineStatusColors } from '../types/user-profile';

const store = useUserProfileStore();
const avatarInput = ref<HTMLInputElement | null>(null);
const isSaving = ref(false);
const newGroupName = ref('');

const statusOptions = [
  { value: 'ONLINE' as OnlineStatus, label: '在线', color: '#52c41a' },
  { value: 'AWAY' as OnlineStatus, label: '离开', color: '#faad14' },
  { value: 'BUSY' as OnlineStatus, label: '忙碌', color: '#f5222d' },
  { value: 'DND' as OnlineStatus, label: '请勿打扰', color: '#f5222d' },
  { value: 'INVISIBLE' as OnlineStatus, label: '隐身', color: '#d9d9d9' },
];

const form = ref({
  nickname: '',
  bio: '',
  gender: 0,
  birthday: '',
  email: '',
  country: '',
  city: '',
  onlineStatus: 'ONLINE' as OnlineStatus,
  statusText: '',
});

const myAvatar = computed(() => store.myAvatar);
const myNickname = computed(() => store.myNickname);
const statusLabel = computed(() => OnlineStatusLabels[store.myOnlineStatus]);
const statusColor = computed(() => OnlineStatusColors[store.myOnlineStatus]);
const friendGroups = computed(() => store.friendGroups);

onMounted(async () => {
  await store.loadMyProfile();
  await store.loadFriendGroups();
  // 填充表单
  const profile = store.myProfile;
  if (profile) {
    form.value.nickname = profile.nickname;
    form.value.bio = profile.bio || '';
    form.value.gender = profile.gender || 0;
    form.value.birthday = profile.birthday?.split('T')[0] || '';
    form.value.email = profile.email || '';
    form.value.country = profile.country || '';
    form.value.city = profile.city || '';
    form.value.onlineStatus = profile.onlineStatus;
    form.value.statusText = profile.statusText || '';
  }
});

function triggerAvatarUpload() {
  avatarInput.value?.click();
}

async function handleAvatarChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = async () => {
    const base64 = (reader.result as string).split(',')[1];
    await store.uploadAvatar(base64);
  };
  reader.readAsDataURL(file);
}

async function saveProfile() {
  isSaving.value = true;
  try {
    await store.updateMyProfile({
      nickname: form.value.nickname,
      bio: form.value.bio,
      gender: form.value.gender,
      birthday: form.value.birthday ? new Date(form.value.birthday).toISOString() : undefined,
      email: form.value.email,
      country: form.value.country,
      city: form.value.city,
    });
    alert('保存成功！');
  } catch (error) {
    alert('保存失败');
  } finally {
    isSaving.value = false;
  }
}

async function setStatus(status: OnlineStatus) {
  form.value.onlineStatus = status;
  await store.updateOnlineStatus(status, form.value.statusText || undefined);
}

function resetForm() {
  const profile = store.myProfile;
  if (profile) {
    form.value.nickname = profile.nickname;
    form.value.bio = profile.bio || '';
    form.value.gender = profile.gender || 0;
    form.value.birthday = profile.birthday?.split('T')[0] || '';
    form.value.email = profile.email || '';
    form.value.country = profile.country || '';
    form.value.city = profile.city || '';
    form.value.onlineStatus = profile.onlineStatus;
    form.value.statusText = profile.statusText || '';
  }
}

async function addGroup() {
  if (!newGroupName.value.trim()) return;
  await store.createFriendGroup(newGroupName.value.trim());
  newGroupName.value = '';
}
</script>

<style scoped>
.profile-page {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.avatar-section {
  position: relative;
  cursor: pointer;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0,0,0,0.5);
  display: none;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
}

.avatar-section:hover .avatar-overlay {
  display: flex;
}

.user-info h2 {
  margin: 0 0 8px;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.form-section {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.form-section h3 {
  margin: 0 0 12px;
  font-size: 16px;
}

.form-item {
  margin-bottom: 12px;
}

.form-item label {
  display: block;
  margin-bottom: 4px;
  font-size: 14px;
  color: #666;
}

.form-item input,
.form-item textarea,
.form-item select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin-bottom: 12px;
}

.status-option {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.status-option.active {
  border-color: #1890ff;
  background: #e6f7ff;
}

.status-icon {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
}

.btn-primary,
.btn-secondary {
  padding: 8px 24px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.btn-primary {
  background: #1890ff;
  color: white;
  border: none;
}

.btn-primary:disabled {
  opacity: 0.6;
}

.btn-secondary {
  background: white;
  border: 1px solid #ddd;
}

.friend-groups-section {
  margin-top: 20px;
}

.groups-list {
  margin-bottom: 12px;
}

.group-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 12px;
  background: #fafafa;
  border-radius: 4px;
  margin-bottom: 8px;
}

.group-count {
  color: #999;
  font-size: 12px;
}

.add-group {
  display: flex;
  gap: 8px;
}

.add-group input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.add-group button {
  padding: 8px 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
