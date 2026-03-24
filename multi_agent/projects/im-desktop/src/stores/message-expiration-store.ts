/**
 * 消息过期状态管理
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { ExpirationRule } from '../types/message-expiration';
import { expirationService } from '../services/message-expiration-service';

export const useExpirationStore = defineStore('expiration', () => {
  const rules = ref<ExpirationRule[]>([]);
  const globalRule = ref<ExpirationRule | null>(null);
  const loading = ref(false);
  const activeTimers = ref<Map<number, ReturnType<typeof setTimeout>>>(new Map());

  const conversationRules = computed(() =>
    rules.value.filter(r => r.conversationId != null)
  );

  /**
   * 加载用户所有规则
   */
  async function loadRules() {
    loading.value = true;
    try {
      const [allRules, global] = await Promise.all([
        expirationService.getUserRules(),
        expirationService.getGlobalRule(),
      ]);
      rules.value = allRules;
      globalRule.value = global;
    } catch (e) {
      console.error('加载过期规则失败', e);
    } finally {
      loading.value = false;
    }
  }

  /**
   * 创建规则
   */
  async function createRule(conversationId: string, request: any) {
    const rule = await expirationService.createRule(conversationId, request);
    rules.value.push(rule);
    return rule;
  }

  /**
   * 更新规则
   */
  async function updateRule(ruleId: number, request: any) {
    const updated = await expirationService.updateRule(ruleId, request);
    const idx = rules.value.findIndex(r => r.id === ruleId);
    if (idx >= 0) rules.value[idx] = updated;
    return updated;
  }

  /**
   * 删除规则
   */
  async function deleteRule(ruleId: number) {
    await expirationService.deleteRule(ruleId);
    rules.value = rules.value.filter(r => r.id !== ruleId);
  }

  /**
   * 获取会话的生效规则
   */
  async function getEffectiveRule(conversationId: string): Promise<ExpirationRule | null> {
    // 先从本地找
    const local = rules.value.find(r => r.conversationId === conversationId && r.active);
    if (local) return local;
    // 从服务端获取
    return expirationService.getEffectiveRule(conversationId);
  }

  /**
   * 启动消息倒计时
   */
  function startCountdown(messageId: number, seconds: number, onExpire: () => void) {
    if (activeTimers.value.has(messageId)) return;
    const timer = setTimeout(() => {
      activeTimers.value.delete(messageId);
      onExpire();
    }, seconds * 1000);
    activeTimers.value.set(messageId, timer);
  }

  /**
   * 取消消息倒计时
   */
  function cancelCountdown(messageId: number) {
    const timer = activeTimers.value.get(messageId);
    if (timer) {
      clearTimeout(timer);
      activeTimers.value.delete(messageId);
    }
  }

  /**
   * 清理所有计时器
   */
  function clearAllTimers() {
    activeTimers.value.forEach(timer => clearTimeout(timer));
    activeTimers.value.clear();
  }

  return {
    rules,
    globalRule,
    conversationRules,
    loading,
    loadRules,
    createRule,
    updateRule,
    deleteRule,
    getEffectiveRule,
    startCountdown,
    cancelCountdown,
    clearAllTimers,
  };
});
