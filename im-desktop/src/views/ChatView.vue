<template>
  <div class="chat-layout">
    <!-- 左侧会话列表 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h2>消息</h2>
        <ConnectionStatusBadge :status="imStore.connectionStatus" />
      </div>
      <ConversationList
        :conversations="imStore.conversations"
        :current-id="imStore.currentConversationId"
        @select="imStore.selectConversation"
      />
    </div>

    <!-- 右侧聊天区域 -->
    <div class="chat-area">
      <template v-if="imStore.currentConversation">
        <ChatHeader :conversation="imStore.currentConversation" />
        <MessageList 
          :messages="imStore.currentMessages" 
          :current-user-id="imService.userId"
        />
        <ChatInput @send="handleSendMessage" />
      </template>
      <div v-else class="empty-state">
        <p>选择一个会话开始聊天</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue';
import { useIMStore } from '@/stores/im';
import { IMService } from '@/services/IMService';
import ConnectionStatusBadge from '@/components/ConnectionStatusBadge.vue';
import ConversationList from '@/components/ConversationList.vue';
import ChatHeader from '@/components/ChatHeader.vue';
import MessageList from '@/components/MessageList.vue';
import ChatInput from '@/components/ChatInput.vue';

const imStore = useIMStore();
const imService = new IMService();

onMounted(async () => {
  // 从存储获取token和userId
  const token = localStorage.getItem('im_token');
  const userId = localStorage.getItem('im_userId');
  
  if (token && userId) {
    await imStore.connect(token, userId);
  }
});

onUnmounted(() => {
  imStore.disconnect();
});

function handleSendMessage(content: string) {
  imStore.sendMessage(content);
}
</script>

<style scoped>
.chat-layout {
  display: flex;
  height: 100vh;
  background: #f5f5f5;
}

.sidebar {
  width: 280px;
  background: #fff;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 18px;
}

.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.empty-state {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}
</style>
