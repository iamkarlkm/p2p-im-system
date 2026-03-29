<template>
  <div class="shared-media-page">
    <div class="media-header">
      <h2>共同媒体</h2>
      <div class="statistics-bar">
        <div class="stat-item" @click="filterMedia('IMAGE')">
          <span class="stat-icon">🖼️</span>
          <span class="stat-count">{{ imageCount }}</span>
        </div>
        <div class="stat-item" @click="filterMedia('VIDEO')">
          <span class="stat-icon">🎬</span>
          <span class="stat-count">{{ videoCount }}</span>
        </div>
        <div class="stat-item" @click="filterMedia('AUDIO')">
          <span class="stat-icon">🎵</span>
          <span class="stat-count">{{ audioCount }}</span>
        </div>
        <div class="stat-item" @click="filterMedia('FILE')">
          <span class="stat-icon">📎</span>
          <span class="stat-count">{{ fileCount }}</span>
        </div>
        <div class="stat-item" @click="filterMedia('LINK')">
          <span class="stat-icon">🔗</span>
          <span class="stat-count">{{ linkCount }}</span>
        </div>
      </div>
    </div>

    <div class="media-filter-tabs">
      <button v-for="tab in mediaTabs" :key="tab.type"
        :class="['filter-tab', { active: selectedMediaType === tab.type }]"
        @click="filterMedia(tab.type)">
        {{ tab.icon }} {{ tab.label }}
      </button>
    </div>

    <div v-if="isLoading" class="loading-state">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else-if="currentMedia.length === 0" class="empty-state">
      <p>暂无媒体内容</p>
    </div>

    <div v-else class="media-grid">
      <div v-for="media in currentMedia" :key="media.id"
        class="media-item" @click="openMediaDetail(media)">
        <div class="media-thumbnail">
          <img v-if="media.mediaType === 'IMAGE'" :src="media.thumbnailUrl || media.fileUrl" :alt="media.fileName" />
          <div v-else class="media-placeholder">
            <span>{{ getMediaIcon(media.mediaType) }}</span>
            <small>{{ media.fileName }}</small>
          </div>
        </div>
        <div class="media-info">
          <span class="media-time">{{ formatTime(media.createdAt) }}</span>
          <span v-if="media.fileSize" class="media-size">{{ formatSize(media.fileSize) }}</span>
        </div>
      </div>
    </div>

    <div v-if="currentMedia.length > 0 && currentPage && currentPage.page < currentPage.totalPages - 1"
      class="load-more">
      <button @click="loadMore">加载更多</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useSharedMediaStore } from '../stores/shared-media-store';
import { sharedMediaService } from '../services/shared-media-service';
import type { SharedMedia } from '../types/shared-media';

const store = useSharedMediaStore();

const currentMedia = computed(() => store.currentMedia);
const currentPage = computed(() => store.currentPage);
const statistics = computed(() => store.statistics);
const isLoading = computed(() => store.isLoading);
const selectedMediaType = computed(() => store.selectedMediaType);
const imageCount = computed(() => store.imageCount);
const videoCount = computed(() => store.videoCount);
const audioCount = computed(() => store.audioCount);
const fileCount = computed(() => store.fileCount);
const linkCount = computed(() => store.linkCount);

const mediaTabs = [
  { type: 'ALL', label: '全部', icon: '📁' },
  { type: 'IMAGE', label: '图片', icon: '🖼️' },
  { type: 'VIDEO', label: '视频', icon: '🎬' },
  { type: 'AUDIO', label: '音频', icon: '🎵' },
  { type: 'FILE', label: '文件', icon: '📎' },
  { type: 'LINK', label: '链接', icon: '🔗' },
];

function filterMedia(type: string) {
  store.setMediaTypeFilter(type);
}

function loadMore() {
  store.loadMore();
}

function openMediaDetail(media: SharedMedia) {
  console.log('Open media detail:', media);
}

function getMediaIcon(mediaType: string): string {
  return sharedMediaService.getMediaTypeIcon(mediaType);
}

function formatTime(timestamp: string): string {
  const date = new Date(timestamp);
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' });
}

function formatSize(bytes: number): string {
  return sharedMediaService.formatFileSize(bytes);
}
</script>

<style scoped>
.shared-media-page {
  padding: 16px;
  height: 100%;
  overflow-y: auto;
  background: var(--bg-primary, #fff);
}
.media-header h2 { margin: 0 0 12px 0; }
.statistics-bar {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}
.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.2s;
}
.stat-item:hover { background: var(--bg-secondary, #f5f5f5); }
.stat-icon { font-size: 20px; }
.stat-count { font-weight: 600; font-size: 14px; }
.media-filter-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.filter-tab {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 16px;
  background: white;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;
}
.filter-tab.active {
  background: var(--primary, #4f46e5);
  color: white;
  border-color: var(--primary, #4f46e5);
}
.media-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 8px;
}
.media-item {
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  aspect-ratio: 1;
}
.media-thumbnail {
  width: 100%;
  height: 100%;
}
.media-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.media-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f0f0f0;
  font-size: 32px;
}
.media-placeholder small {
  font-size: 10px;
  color: #666;
  margin-top: 4px;
  word-break: break-all;
  padding: 0 4px;
}
.media-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0,0,0,0.6));
  color: white;
  padding: 16px 4px 4px;
  display: flex;
  justify-content: space-between;
  font-size: 11px;
}
.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px;
  color: #999;
}
.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid var(--primary, #4f46e5);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 8px;
}
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
.load-more { text-align: center; padding: 16px; }
.load-more button {
  padding: 8px 24px;
  background: var(--primary, #4f46e5);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
</style>
