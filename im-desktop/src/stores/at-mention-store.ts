import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { AtMention, AtMentionSettings } from '../types/at-mention';
import * as api from '../services/at-mention-service';

export const useAtMentionStore = defineStore('atMention', () => {
  const mentions = ref<AtMention[]>([]);
  const unreadCount = ref<number>(0);
  const roomUnreadCounts = ref<Map<number, number>>(new Map());
  const settings = ref<AtMentionSettings | null>(null);
  const loading = ref(false);
  const totalPages = ref(0);
  const totalElements = ref(0);
  const currentPage = ref(0);

  const hasUnreadMentions = computed(() => unreadCount.value > 0);

  async function loadMentions(userId: number, page = 0) {
    loading.value = true;
    try {
      const result = await api.getMentionList(userId, page);
      mentions.value = page === 0 ? result.data : [...mentions.value, ...result.data];
      totalPages.value = result.totalPages;
      totalElements.value = result.totalElements;
      currentPage.value = page;
    } finally {
      loading.value = false;
    }
  }

  async function loadUnreadCount(userId: number) {
    unreadCount.value = await api.getUnreadCount(userId);
  }

  async function loadRoomUnreadCount(userId: number, roomId: number) {
    const count = await api.getUnreadCountInRoom(userId, roomId);
    roomUnreadCounts.value.set(roomId, count);
  }

  async function markRead(userId: number, mentionIds: number[]) {
    await api.markAsRead(userId, mentionIds);
    mentions.value = mentions.value.map(m =>
      mentionIds.includes(m.id) ? { ...m, isRead: true } : m
    );
    unreadCount.value = Math.max(0, unreadCount.value - mentionIds.length);
  }

  async function markAllReadInRoom(userId: number, roomId: number) {
    const updated = await api.markAllAsReadInRoom(userId, roomId);
    mentions.value = mentions.value.map(m =>
      m.roomId === roomId ? { ...m, isRead: true } : m
    );
    roomUnreadCounts.value.set(roomId, 0);
  }

  async function loadSettings(userId: number) {
    settings.value = await api.getMentionSettings(userId);
  }

  async function saveSettings(userId: number, s: AtMentionSettings) {
    settings.value = await api.updateMentionSettings(userId, s);
  }

  return {
    mentions,
    unreadCount,
    roomUnreadCounts,
    settings,
    loading,
    totalPages,
    totalElements,
    currentPage,
    hasUnreadMentions,
    loadMentions,
    loadUnreadCount,
    loadRoomUnreadCount,
    markRead,
    markAllReadInRoom,
    loadSettings,
    saveSettings
  };
});
