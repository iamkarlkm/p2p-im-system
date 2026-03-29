<template>
  <div class="expiration-settings">
    <div class="settings-header">
      <h3>消息过期设置</h3>
    </div>

    <!-- 全局规则 -->
    <div class="section">
      <h4>全局默认规则</h4>
      <div v-if="store.globalRule" class="rule-card">
        <div class="rule-info">
          <span class="rule-type">{{ formatType(store.globalRule.expirationType) }}</span>
          <span class="rule-time">{{ formatTime(store.globalRule) }}</span>
        </div>
        <div class="rule-actions">
          <button @click="editGlobalRule" class="btn-edit">编辑</button>
          <button @click="toggleRule(store.globalRule)" class="btn-toggle">
            {{ store.globalRule.active ? '禁用' : '启用' }}
          </button>
        </div>
      </div>
      <div v-else class="empty-state">
        <p>未设置全局规则</p>
        <button @click="showCreateGlobal = true" class="btn-primary">创建全局规则</button>
      </div>
    </div>

    <!-- 会话规则列表 -->
    <div class="section">
      <h4>会话规则 ({{ store.conversationRules.length }})</h4>
      <div v-if="store.conversationRules.length === 0" class="empty-state">
        <p>暂无会话级规则</p>
      </div>
      <div v-for="rule in store.conversationRules" :key="rule.id" class="rule-card">
        <div class="rule-info">
          <span class="rule-type">{{ formatType(rule.expirationType) }}</span>
          <span class="rule-conv">{{ rule.conversationId }}</span>
          <span class="rule-time">{{ formatTime(rule) }}</span>
        </div>
        <div class="rule-actions">
          <button @click="deleteRule(rule)" class="btn-delete">删除</button>
          <button @click="toggleRule(rule)" class="btn-toggle">
            {{ rule.active ? '禁用' : '启用' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 创建规则弹窗 -->
    <div v-if="showCreateGlobal" class="modal-overlay" @click.self="showCreateGlobal = false">
      <div class="modal">
        <h4>创建全局过期规则</h4>
        <div class="form-group">
          <label>过期类型</label>
          <select v-model="newRule.expirationType">
            <option value="TIME_BASED">创建后定时过期</option>
            <option value="SELF_DESTRUCT">阅后即焚</option>
            <option value="GLOBAL">全局默认</option>
          </select>
        </div>
        <div class="form-group">
          <label>过期时长（秒）</label>
          <input type="number" v-model.number="newRule.relativeSeconds" placeholder="如: 3600 (1小时)" />
        </div>
        <div class="form-group">
          <label>消息类型</label>
          <select v-model="newRule.messageTypeFilter">
            <option value="ALL">所有消息</option>
            <option value="TEXT">仅文本</option>
            <option value="IMAGE">仅图片</option>
            <option value="FILE">仅文件</option>
          </select>
        </div>
        <div class="form-group">
          <label>
            <input type="checkbox" v-model="newRule.preExpireNotice" />
            过期前发送提醒
          </label>
        </div>
        <div class="modal-actions">
          <button @click="showCreateGlobal = false" class="btn-cancel">取消</button>
          <button @click="createGlobalRule" class="btn-primary">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useExpirationStore } from '../stores/message-expiration-store';
import type { ExpirationRule, ExpirationRuleRequest } from '../types/message-expiration';

const store = useExpirationStore();
const showCreateGlobal = ref(false);
const newRule = ref<ExpirationRuleRequest>({
  expirationType: 'TIME_BASED',
  relativeSeconds: 86400,
  messageTypeFilter: 'ALL',
  active: true,
  preExpireNotice: false,
});

onMounted(() => {
  store.loadRules();
});

function formatType(type: string): string {
  const map: Record<string, string> = {
    READ_AFTER: '阅后即焚',
    SELF_DESTRUCT: '阅后即焚',
    TIME_BASED: '定时过期',
    GLOBAL: '全局默认',
  };
  return map[type] || type;
}

function formatTime(rule: ExpirationRule): string {
  if (rule.readDestroySeconds) return `${rule.readDestroySeconds}秒后销毁`;
  if (rule.relativeSeconds) {
    const s = rule.relativeSeconds;
    if (s < 60) return `${s}秒`;
    if (s < 3600) return `${Math.floor(s / 60)}分钟`;
    if (s < 86400) return `${Math.floor(s / 3600)}小时`;
    return `${Math.floor(s / 86400)}天`;
  }
  return '永久';
}

async function createGlobalRule() {
  await store.createRule('', newRule.value);
  showCreateGlobal.value = false;
}

async function editGlobalRule() {
  // 复用创建弹窗
  if (store.globalRule) {
    newRule.value.expirationType = store.globalRule.expirationType as any;
    newRule.value.relativeSeconds = store.globalRule.relativeSeconds ?? undefined;
    newRule.value.messageTypeFilter = store.globalRule.messageTypeFilter as any;
    newRule.value.preExpireNotice = store.globalRule.preExpireNotice;
  }
  showCreateGlobal.value = true;
}

async function deleteRule(rule: ExpirationRule) {
  await store.deleteRule(rule.id);
}

async function toggleRule(rule: ExpirationRule) {
  await store.updateRule(rule.id, { active: !rule.active });
}
</script>

<style scoped>
.expiration-settings { padding: 16px; }
.settings-header { margin-bottom: 20px; }
.settings-header h3 { margin: 0; font-size: 18px; }
.section { margin-bottom: 24px; }
.section h4 { margin: 0 0 12px; font-size: 14px; color: #666; }
.rule-card {
  background: #f5f5f5;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.rule-info { display: flex; gap: 12px; align-items: center; }
.rule-type { font-weight: 600; color: #e74c3c; }
.rule-time { color: #888; font-size: 13px; }
.rule-conv { color: #3498db; font-size: 13px; }
.rule-actions { display: flex; gap: 8px; }
button {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
}
.btn-primary { background: #3498db; color: white; }
.btn-edit { background: #95a5a6; color: white; }
.btn-delete { background: #e74c3c; color: white; }
.btn-toggle { background: #2ecc71; color: white; }
.btn-cancel { background: #95a5a6; color: white; }
.empty-state { color: #999; text-align: center; padding: 20px; }
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.5);
  display: flex; align-items: center; justify-content: center; z-index: 1000;
}
.modal {
  background: white; border-radius: 8px; padding: 24px; width: 400px; max-width: 90vw;
}
.modal h4 { margin: 0 0 16px; }
.form-group { margin-bottom: 12px; }
.form-group label { display: block; margin-bottom: 4px; font-size: 13px; }
.form-group input, .form-group select {
  width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box;
}
.modal-actions { display: flex; gap: 8px; justify-content: flex-end; margin-top: 16px; }
</style>
