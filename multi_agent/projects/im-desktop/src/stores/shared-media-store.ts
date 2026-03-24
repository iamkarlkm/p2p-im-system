import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { sharedMediaService } from '../services/shared-media-service';
import type { SharedMedia, MediaPage, MediaStatistics, LinkPreview, MediaRequest } from '../types/shared-media';

export const useSharedMediaStore = defineStore('sharedMedia', () => {
  const currentMedia = ref<SharedMedia[]>([]);
  const currentPage = ref<MediaPage | null>(null);
  const statistics = ref<MediaStatistics | null>(null);
  const sharedLinks = ref<LinkPreview[]>([]);
  const selectedMediaType = ref<string>('ALL');
  const isLoading = ref(false);
  const selectedConversationId = ref<string>('');

  const imageCount = computed(() => statistics.value?.imageCount ?? 0);
  const videoCount = computed(() => statistics.value?.videoCount ?? 0);
  const audioCount = computed(() => statistics.value?.audioCount ?? 0);
  const fileCount = computed(() => statistics.value?.fileCount ?? 0);
  const linkCount = computed(() => statistics.value?.linkCount ?? 0);
  const totalSize = computed(() => statistics.value?.totalSize ?? 0);

  async function loadSharedMedia(conversationId: string, mediaType?: string) {
    isLoading.value = true;
    selectedConversationId.value = conversationId;
    try {
      const request: MediaRequest = {
        conversationId,
        mediaType: mediaType as any,
        page: 0,
        size: 50,
      };
      currentPage.value = await sharedMediaService.getSharedMedia(request);
      currentMedia.value = currentPage.value.items;
      statistics.value = currentPage.value.statistics ?? null;
    } catch (error) {
      console.error('Failed to load shared media:', error);
    } finally {
      isLoading.value = false;
    }
  }

  async function loadMore() {
    if (!currentPage.value || isLoading.value) return;
    isLoading.value = true;
    try {
      const request: MediaRequest = {
        conversationId: selectedConversationId.value,
        mediaType: selectedMediaType.value !== 'ALL' ? selectedMediaType.value as any : undefined,
        page: currentPage.value.page + 1,
        size: currentPage.value.size,
      };
      const nextPage = await sharedMediaService.getSharedMedia(request);
      currentMedia.value = [...currentMedia.value, ...nextPage.items];
      currentPage.value = nextPage;
    } catch (error) {
      console.error('Failed to load more media:', error);
    } finally {
      isLoading.value = false;
    }
  }

  async function loadStatistics(conversationId: string) {
    try {
      statistics.value = await sharedMediaService.getMediaStatistics(conversationId);
    } catch (error) {
      console.error('Failed to load statistics:', error);
    }
  }

  async function loadSharedLinks(conversationId: string) {
    try {
      sharedLinks.value = await sharedMediaService.getSharedLinks(conversationId, 0, 20);
    } catch (error) {
      console.error('Failed to load links:', error);
    }
  }

  async function deleteMedia(mediaId: number) {
    try {
      await sharedMediaService.deleteMedia(mediaId);
      currentMedia.value = currentMedia.value.filter(m => m.id !== mediaId);
    } catch (error) {
      console.error('Failed to delete media:', error);
    }
  }

  function setMediaTypeFilter(mediaType: string) {
    selectedMediaType.value = mediaType;
    if (selectedConversationId.value) {
      loadSharedMedia(selectedConversationId.value, mediaType);
    }
  }

  return {
    currentMedia, currentPage, statistics, sharedLinks,
    selectedMediaType, isLoading, selectedConversationId,
    imageCount, videoCount, audioCount, fileCount, linkCount, totalSize,
    loadSharedMedia, loadMore, loadStatistics, loadSharedLinks, deleteMedia, setMediaTypeFilter,
  };
});
