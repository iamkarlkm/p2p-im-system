/**
 * Sticker System Type Definitions
 */

export interface StickerPack {
  id: number;
  packId: string;
  name: string;
  description?: string;
  author: string;
  publisher?: string;
  coverUrl: string;
  coverThumbnailUrl: string;
  totalStickers: number;
  totalDownloads: number;
  totalLikes: number;
  averageRating: number;
  isOfficial: boolean;
  isFeatured: boolean;
  isFree: boolean;
  price?: number;
  currency?: string;
  category: string;
  tags: string[];
  licenseType: string;
  licenseUrl: string;
  fileFormat: string;
  totalSizeBytes: number;
  createdAt: string;
  updatedAt: string;
  publishedAt?: string;
  isActive: boolean;
  isDeleted: boolean;
}

export interface Sticker {
  id: number;
  stickerId: string;
  name: string;
  description?: string;
  imageUrl: string;
  thumbnailUrl: string;
  animatedUrl?: string;
  width: number;
  height: number;
  fileSizeBytes: number;
  fileFormat: string;
  isAnimated: boolean;
  frameCount: number;
  durationSeconds?: number;
  frameRate: number;
  mimeType: string;
  checksum: string;
  checksumAlgorithm: string;
  sortOrder: number;
  totalUses: number;
  totalFavorites: number;
  createdAt: string;
  updatedAt: string;
  isActive: boolean;
  isDeleted: boolean;
  packId?: number;
  pack?: StickerPack;
}

export interface StickerCategory {
  id: string;
  name: string;
  description?: string;
  iconUrl?: string;
  color?: string;
  sortOrder: number;
  packCount: number;
  stickerCount: number;
  isFeatured: boolean;
}

export interface StickerStats {
  pack_id: string;
  total_downloads: number;
  total_likes: number;
  average_rating: number;
  total_stickers: number;
  created_at: string;
  updated_at: string;
  total_sticker_uses: number;
  total_sticker_favorites: number;
  top_stickers: Array<{
    sticker_id: string;
    name: string;
    total_uses: number;
    total_favorites: number;
  }>;
}

export interface StickerSearchFilters {
  query?: string;
  category?: string;
  isOfficial?: boolean;
  isFeatured?: boolean;
  isFree?: boolean;
  minRating?: number;
  minDownloads?: number;
  tags?: string[];
  fileFormat?: string;
  isAnimated?: boolean;
  maxWidth?: number;
  maxHeight?: number;
  maxSize?: number;
}

export interface StickerPackCreateRequest {
  name: string;
  description?: string;
  author: string;
  category: string;
  tags?: string[];
  isFree?: boolean;
  price?: number;
  licenseType?: string;
}

export interface StickerCreateRequest {
  name: string;
  description?: string;
  imageUrl: string;
  thumbnailUrl: string;
  animatedUrl?: string;
  width: number;
  height: number;
  fileSizeBytes: number;
  fileFormat: string;
  isAnimated: boolean;
  frameCount?: number;
  durationSeconds?: number;
  frameRate?: number;
  sortOrder?: number;
}

export interface StickerRating {
  id: number;
  packId: number;
  userId: string;
  rating: number;
  comment?: string;
  createdAt: string;
  updatedAt: string;
}

export interface StickerFavorite {
  id: number;
  stickerId: number;
  userId: string;
  createdAt: string;
}

export interface StickerDownload {
  id: number;
  packId: number;
  userId: string;
  deviceId: string;
  ipAddress?: string;
  downloadedAt: string;
}

export interface StickerUsage {
  id: number;
  stickerId: number;
  userId: string;
  conversationId: string;
  messageId?: string;
  usedAt: string;
}

export interface StickerPackAnalytics {
  packId: string;
  date: string;
  downloads: number;
  views: number;
  favorites: number;
  uses: number;
  ratings: number;
  averageRating: number;
}

export interface StickerSystemStats {
  total_packs: number;
  official_packs: number;
  featured_packs: number;
  free_packs: number;
  pending_packs: number;
  total_downloads: number;
  total_likes: number;
  average_rating: number;
  total_stickers: number;
  animated_stickers: number;
  total_sticker_uses: number;
  total_sticker_favorites: number;
  category_distribution: Record<string, number>;
  format_distribution: Record<string, number>;
}