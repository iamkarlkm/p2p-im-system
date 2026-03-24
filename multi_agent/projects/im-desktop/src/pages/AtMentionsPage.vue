<template>
  <div class="at-mentions-page">
    <div class="page-header">
      <h2>消息@提醒</h2>
      <el-badge :value="store.unreadCount" :hidden="store.unreadCount === 0" type="danger">
        <span class="unread-text">未读 {{ store.unreadCount }} 条</span>
      </el-badge>
    </div>

    <div class="mention-settings-btn">
      <el-button size="small" @click="showSettings = true">
        <i class="el-icon-setting"></i> 提醒设置
      </el-button>
    </div>

    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <el-tab-pane label="全部" name="all"></el-tab-pane>
      <el-tab-pane label="未读" name="unread">
        <template #label>
          <span>未读 <el-badge :value="store.unreadCount" :hidden="store.unreadCount === 0" type="danger"></el-badge></span>
        </template>
      </el-tab-pane>
    </el-tabs>

    <div class="mention-list" v-loading="store.loading">
      <div v-if="filteredMentions.length === 0 && !store.loading" class="empty-state">
        <i class="el-icon-chat-line-round empty-icon"></i>
        <p>暂无@提及</p>
      </div>

      <div
        v-for="mention in filteredMentions"
        :key="mention.id"
        class="mention-item"
        :class="{ unread: !mention.isRead, 'at-all': mention.isAtAll }"
        @click="handleClick(mention)"
      >
        <div class="mention-avatar">
          <el-avatar :size="40" :icon="mention.isAtAll ? 'el-icon-user' : undefined">
            {{ mention.isAtAll ? 'ALL' : (mention.senderNickname || mention.senderUserId).toString().slice(0, 2) }}
          </el-avatar>
          <span v-if="mention.isAtAll" class="at-all-badge">@所有人</span>
        </div>
        <div class="mention-content">
          <div class="mention-header">
            <span class="sender-name">
              {{ mention.senderNickname || `用户${mention.senderUserId}` }}
            </span>
            <span class="mention-time">{{ formatTime(mention.mentionedAt) }}</span>
          </div>
          <div class="mention-preview">
            <span v-if="mention.isAtAll" class="at-tag">@所有人</span>
            <span v-else class="at-tag">@了你</span>
            <span class="preview-text">{{ mention.messagePreview }}</span>
          </div>
          <div v-if="mention.roomName" class="mention-room">
            <i class="el-icon-house"></i> {{ mention.roomName }}
          </div>
        </div>
        <div v-if="!mention.isRead" class="unread-dot"></div>
      </div>
    </div>

    <div v-if="hasMore" class="load-more">
      <el-button size="small" :loading="store.loading" @click="loadMore">
        加载更多
      </el-button>
    </div>

    <!-- 设置对话框 -->
    <el-dialog v-model="showSettings" title="@提醒设置" width="500px">
      <el-form :model="settingsForm" label-width="120px">
        <el-form-item label="开启@提醒">
          <el-switch v-model="settingsForm.enabled"></el-switch>
        </el-form-item>
        <el-form-item label="仅@所有人时提醒">
          <el-switch v-model="settingsForm.onlyAtAll"></el-switch>
        </el-form-item>
        <el-form-item label="允许陌生人@">
          <el-switch v-model="settingsForm.allowStrangerAt"></el-switch>
        </el-form-item>
        <el-form-item label="同步到其他设备">
          <el-switch v-model="settingsForm.syncToOtherDevices"></el-switch>
        </el-form-item>
        <el-divider>免打扰</el-divider>
        <el-form-item label="开启免打扰">
          <el-switch v-model="settingsForm.dndEnabled"></el-switch>
        </el-form-item>
        <el-form-item v-if="settingsForm.dndEnabled" label="免打扰时段">
          <el-time-select
            v-model="settingsForm.dndStartTime"
            placeholder="开始时间"
            start="00:00"
            step="00:30"
            end="23:30"
            style="width: 100px"
          ></el-time-select>
          <span style="margin: 0 10px">至</span>
          <el-time-select
            v-model="settingsForm.dndEndTime"
            placeholder="结束时间"
            start="00:00"
            step="00:30"
            end="23:30"
            style="width: 100px"
          ></el-time-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSettings = false">取消</el-button>
        <el-button type="primary" @click="saveSettings">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useAtMentionStore } from '../stores/at-mention-store';
import { useAuthStore } from '../stores/auth-store';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { AtMention } from '../types/at-mention';

const store = useAtMentionStore();
const authStore = useAuthStore();
const router = useRouter();

const activeTab = ref('all');
const showSettings = ref(false);
const settingsForm = ref({
  enabled: true,
  onlyAtAll: false,
  allowStrangerAt: true,
  syncToOtherDevices: true,
  dndEnabled: false,
  dndStartTime: '',
  dndEndTime: ''
});

const filteredMentions = computed(() => {
  if (activeTab.value === 'unread') {
    return store.mentions.filter(m => !m.isRead);
  }
  return store.mentions;
});

const hasMore = computed(() => store.currentPage < store.totalPages - 1);

onMounted(async () => {
  const userId = authStore.currentUser?.id;
  if (userId) {
    await Promise.all([
      store.loadMentions(userId),
      store.loadUnreadCount(userId),
      store.loadSettings(userId)
    ]);
    if (store.settings) {
      settingsForm.value = {
        enabled: store.settings.enabled ?? true,
        onlyAtAll: store.settings.onlyAtAll ?? false,
        allowStrangerAt: store.settings.allowStrangerAt ?? true,
        syncToOtherDevices: store.settings.syncToOtherDevices ?? true,
        dndEnabled: store.settings.dndEnabled ?? false,
        dndStartTime: store.settings.dndStartTime ?? '',
        dndEndTime: store.settings.dndEndTime ?? ''
      };
    }
  }
});

async function loadMore() {
  const userId = authStore.currentUser?.id;
  if (userId) {
    await store.loadMentions(userId, store.currentPage + 1);
  }
}

async function handleClick(mention: AtMention) {
  const userId = authStore.currentUser?.id;
  if (userId && !mention.isRead) {
    await store.markRead(userId, [mention.id]);
  }
  // 跳转到对应会话
  if (mention.conversationId) {
    router.push(`/chat/${mention.conversationId}?messageId=${mention.messageId}`);
  }
}

async function saveSettings() {
  const userId = authStore.currentUser?.id;
  if (userId) {
    await store.saveSettings(userId, {
      userId,
      ...settingsForm.value
    });
    showSettings.value = false;
    ElMessage.success('设置已保存');
  }
}

function onTabChange() {
  // Tab切换时可重置分页
}

function formatTime(time: string): string {
  const date = new Date(time);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const minutes = Math.floor(diff / 60000);
  if (minutes < 1) return '刚刚';
  if (minutes < 60) return `${minutes}分钟前`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours}小时前`;
  const days = Math.floor(hours / 24);
  if (days < 7) return `${days}天前`;
  return date.toLocaleDateString();
}
</script>

<style scoped>
.at-mentions-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.page-header {
  padding: 16px 20px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #eee;
}

.page-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.unread-text {
  font-size: 13px;
  color: #666;
}

.mention-settings-btn {
  padding: 8px 20px;
  background: #fff;
}

.mention-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.mention-item {
  display: flex;
  align-items: flex-start;
  padding: 12px 20px;
  background: #fff;
  cursor: pointer;
  transition: background 0.2s;
  position: relative;
}

.mention-item:hover {
  background: #f9f9f9;
}

.mention-item.unread {
  background: #f0f7ff;
}

.mention-item.unread:hover {
  background: #e6f0ff;
}

.mention-avatar {
  position: relative;
  margin-right: 12px;
  flex-shrink: 0;
}

.at-all-badge {
  position: absolute;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%);
  background: #ff6b00;
  color: #fff;
  font-size: 8px;
  padding: 0 3px;
  border-radius: 2px;
  white-space: nowrap;
}

.mention-content {
  flex: 1;
  min-width: 0;
}

.mention-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.sender-name {
  font-weight: 600;
  font-size: 14px;
  color: #333;
}

.mention-time {
  font-size: 12px;
  color: #999;
}

.mention-preview {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.at-tag {
  color: #1890ff;
  font-weight: 600;
  flex-shrink: 0;
}

.preview-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mention-room {
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}

.unread-dot {
  width: 8px;
  height: 8px;
  background: #1890ff;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 6px;
}

.load-more {
  text-align: center;
  padding: 16px;
}
</style>
