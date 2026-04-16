<template>
  <div class="chat-input">
    <div class="toolbar">
      <button class="tool-btn" title="表情">
        <i class="icon-emoji"></i>
      </button>
      <button class="tool-btn" title="图片">
        <i class="icon-image"></i>
      </button>
      <button class="tool-btn" title="文件">
        <i class="icon-file"></i>
      </button>
    </div>
    <textarea
      v-model="text"
      class="input-area"
      placeholder="输入消息..."
      @keydown.enter.prevent="send"
    ></textarea>
    <div class="footer">
      <span class="hint">按 Enter 发送</span>
      <button class="send-btn" :disabled="!text.trim()" @click="send">
        发送
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const text = ref('');

const emit = defineEmits<{
  (e: 'send', content: string): void;
}>();

function send() {
  const content = text.value.trim();
  if (content) {
    emit('send', content);
    text.value = '';
  }
}
</script>

<style scoped>
.chat-input {
  padding: 12px 20px;
  background: #fff;
  border-top: 1px solid #e0e0e0;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
}

.tool-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  cursor: pointer;
  color: #666;
}

.tool-btn:hover {
  color: #1890ff;
}

.input-area {
  width: 100%;
  height: 80px;
  border: none;
  resize: none;
  outline: none;
  font-size: 14px;
  line-height: 1.5;
}

.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hint {
  font-size: 12px;
  color: #999;
}

.send-btn {
  padding: 6px 20px;
  background: #1890ff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.send-btn:hover:not(:disabled) {
  background: #40a9ff;
}

.send-btn:disabled {
  background: #d9d9d9;
  cursor: not-allowed;
}
</style>
