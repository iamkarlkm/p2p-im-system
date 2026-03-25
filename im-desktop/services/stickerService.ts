/**
 * Sticker Service - Manages sticker packs and individual stickers
 * Handles API calls, local caching, and sticker usage tracking
 */

import api from './api';
import { StickerPack, Sticker, StickerCategory, StickerStats } from '../types/sticker';

const CACHE_KEY = 'sticker_packs_cache';
const CACHE_TTL = 5 * 60 * 1000; // 5 minutes

class StickerService {
  private cache: Map<string, { data: any; timestamp: number }> = new Map();
  private downloadQueue: Set<string> = new Set();
  private recentlyUsed: string[] = [];
  private maxRecentItems = 50;

  // ==================== Sticker Pack CRUD ====================

  async createStickerPack(pack: Partial<StickerPack>): Promise<StickerPack> {
    const response = await api.post('/stickers/packs', pack);
    this.invalidateCache();
    return response.data;
  }

  async getStickerPack(packId: string): Promise<StickerPack | null> {
    const cached = this.getFromCache(`pack_${packId}`);
    if (cached) return cached as StickerPack;

    try {
      const response = await api.get(`/stickers/packs/${packId}`);
      this.setCache(`pack_${packId}`, response.data);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch sticker pack:', error);
      return null;
    }
  }

  async updateStickerPack(id: number, updates: Partial<StickerPack>): Promise<StickerPack> {
    const response = await api.put(`/stickers/packs/${id}`, updates);
    this.invalidateCache();
    return response.data;
  }

  async deleteStickerPack(id: number, reason?: string): Promise<void> {
    await api.delete(`/stickers/packs/${id}`, { 
      params: { reason } 
    });
    this.invalidateCache();
  }

  // ==================== Sticker Pack Search ====================

  async searchStickerPacks(params: {
    query?: string;
    category?: string;
    isOfficial?: boolean;
    isFeatured?: boolean;
    isFree?: boolean;
    minRating?: number;
    minDownloads?: number;
    page?: number;
    size?: number;
  }): Promise<{ content: StickerPack[]; total: number }> {
    const response = await api.get('/stickers/packs/search', { params });
    return {
      content: response.data.content,
      total: response.data.totalElements,
    };
  }

  async getFeaturedStickerPacks(page = 0, size = 20): Promise<StickerPack[]> {
    const cached = this.getFromCache('featured_packs');
    if (cached) return cached as StickerPack[];

    const response = await api.get('/stickers/packs/featured', { 
      params: { page, size } 
    });
    this.setCache('featured_packs', response.data.content);
    return response.data.content;
  }

  async getOfficialStickerPacks(page = 0, size = 20): Promise<StickerPack[]> {
    const response = await api.get('/stickers/packs/official', { 
      params: { page, size } 
    });
    return response.data.content;
  }

  async getFreeStickerPacks(page = 0, size = 20): Promise<StickerPack[]> {
    const response = await api.get('/stickers/packs/free', { 
      params: { page, size } 
    });
    return response.data.content;
  }

  async getNewestStickerPacks(page = 0, size = 20): Promise<StickerPack[]> {
    const response = await api.get('/stickers/packs/new', { 
      params: { page, size } 
    });
    return response.data.content;
  }

  async getTrendingStickerPacks(page = 0, size = 20): Promise<StickerPack[]> {
    const response = await api.get('/stickers/packs/trending', { 
      params: { page, size } 
    });
    return response.data.content;
  }

  async getTopStickerPacksByDownloads(page = 0, size = 20): Promise<StickerPack[]> {
    const response = await api.get('/stickers/packs/top/downloads', { 
      params: { page, size } 
    });
    return response.data.content;
  }

  async getTopStickerPacksByLikes(page = 0, size = 20): Promise<StickerPack[]> {
    const response = await api.get('/stickers/packs/top/likes', { 
      params: { page, size } 
    });
    return response.data.content;
  }

  async getTopStickerPacksByRating(page = 0, size = 20): Promise<StickerPack[]> {
    const response = await api.get('/stickers/packs/top/rating', { 
      params: { page, size } 
    });
    return response.data.content;
  }

  async getRecommendedStickerPacks(limit = 10): Promise<StickerPack[]> {
    const response = await api.get('/stickers/packs/recommended', { 
      params: { limit } 
    });
    return response.data;
  }

  // ==================== Sticker Management ====================

  async getStickersInPack(packId: number): Promise<Sticker[]> {
    const cached = this.getFromCache(`pack_${packId}_stickers`);
    if (cached) return cached as Sticker[];

    const response = await api.get(`/stickers/packs/${packId}/stickers`);
    this.setCache(`pack_${packId}_stickers`, response.data);
    return response.data;
  }

  async addStickerToPack(packId: number, sticker: Partial<Sticker>): Promise<Sticker> {
    const response = await api.post(`/stickers/packs/${packId}/stickers`, sticker);
    this.invalidateCache();
    return response.data;
  }

  async removeStickerFromPack(packId: number, stickerId: number): Promise<void> {
    await api.delete(`/stickers/packs/${packId}/stickers/${stickerId}`);
    this.invalidateCache();
  }

  // ==================== Usage Tracking ====================

  async downloadStickerPack(packId: number): Promise<void> {
    if (this.downloadQueue.has(`pack_${packId}`)) {
      return; // Already downloading
    }

    this.downloadQueue.add(`pack_${packId}`);
    try {
      await api.post(`/stickers/packs/${packId}/download`);
    } finally {
      this.downloadQueue.delete(`pack_${packId}`);
    }
  }

  async useSticker(stickerId: number, conversationId: string, messageId?: string): Promise<void> {
    await api.post(`/stickers/stickers/${stickerId}/use`, {
      conversationId,
      messageId,
    });

    // Update recently used list
    this.recentlyUsed = [
      `sticker_${stickerId}`,
      ...this.recentlyUsed.filter(id => id !== `sticker_${stickerId}`),
    ].slice(0, this.maxRecentItems);

    this.saveRecentlyUsed();
  }

  async favoriteSticker(stickerId: number): Promise<void> {
    await api.post(`/stickers/stickers/${stickerId}/favorite`);
  }

  // ==================== Rating and Feedback ====================

  async rateStickerPack(packId: number, rating: number, comment?: string): Promise<void> {
    await api.post(`/stickers/packs/${packId}/rate`, { rating, comment });
  }

  // ==================== Admin Operations ====================

  async approveStickerPack(packId: number): Promise<void> {
    await api.post(`/stickers/admin/packs/${packId}/approve`);
    this.invalidateCache();
  }

  async rejectStickerPack(packId: number, reason: string): Promise<void> {
    await api.post(`/stickers/admin/packs/${packId}/reject`, { reason });
    this.invalidateCache();
  }

  async featureStickerPack(packId: number): Promise<void> {
    await api.post(`/stickers/admin/packs/${packId}/feature`);
    this.invalidateCache();
  }

  async unfeatureStickerPack(packId: number): Promise<void> {
    await api.post(`/stickers/admin/packs/${packId}/unfeature`);
    this.invalidateCache();
  }

  // ==================== Statistics ====================

  async getStickerPackStatistics(packId: number): Promise<StickerStats> {
    const response = await api.get(`/stickers/packs/${packId}/stats`);
    return response.data;
  }

  async getSystemStatistics(): Promise<any> {
    const response = await api.get('/stickers/stats');
    return response.data;
  }

  // ==================== File Upload ====================

  async uploadStickerPack(packId: number, file: File): Promise<any> {
    const formData = new FormData();
    formData.append('file', file);

    const response = await api.post(`/stickers/packs/${packId}/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  }

  // ==================== Cache Management ====================

  private getFromCache(key: string): any {
    const cached = this.cache.get(key);
    if (cached && Date.now() - cached.timestamp < CACHE_TTL) {
      return cached.data;
    }
    this.cache.delete(key);
    return null;
  }

  private setCache(key: string, data: any): void {
    this.cache.set(key, {
      data,
      timestamp: Date.now(),
    });
  }

  private invalidateCache(): void {
    this.cache.clear();
  }

  // ==================== Recently Used Management ====================

  getRecentlyUsed(): string[] {
    return this.recentlyUsed;
  }

  private saveRecentlyUsed(): void {
    try {
      localStorage.setItem('sticker_recently_used', JSON.stringify(this.recentlyUsed));
    } catch (error) {
      console.error('Failed to save recently used stickers:', error);
    }
  }

  private loadRecentlyUsed(): void {
    try {
      const stored = localStorage.getItem('sticker_recently_used');
      if (stored) {
        this.recentlyUsed = JSON.parse(stored);
      }
    } catch (error) {
      console.error('Failed to load recently used stickers:', error);
    }
  }

  // ==================== Initialization ====================

  init(): void {
    this.loadRecentlyUsed();
  }
}

export const stickerService = new StickerService();
export default stickerService;