export type MediaType = 'IMAGE' | 'VIDEO' | 'AUDIO' | 'FILE' | 'LINK' | 'VOICE';
export type LinkType = 'ARTICLE' | 'VIDEO' | 'PRODUCT' | 'MUSIC' | 'DOCUMENT' | 'UNKNOWN';
export type AlbumType = 'IMAGE' | 'VIDEO' | 'AUDIO' | 'FILE' | 'ALL';

export interface SharedMedia {
  id: number;
  conversationId: string;
  messageId: string;
  senderId: string;
  senderName?: string;
  senderAvatar?: string;
  mediaType: MediaType;
  fileName: string;
  fileUrl: string;
  thumbnailUrl?: string;
  fileSize?: number;
  mimeType?: string;
  width?: number;
  height?: number;
  duration?: number;
  description?: string;
  createdAt: string;
  canDelete?: boolean;
}

export interface MediaPage {
  items: SharedMedia[];
  page: number;
  size: number;
  total: number;
  totalPages: number;
  mediaType?: MediaType;
  totalSize?: number;
  statistics?: MediaStatistics;
}

export interface MediaStatistics {
  imageCount: number;
  videoCount: number;
  audioCount: number;
  fileCount: number;
  linkCount: number;
  totalSize: number;
}

export interface LinkPreview {
  id: number;
  conversationId: string;
  messageId: string;
  url: string;
  title?: string;
  description?: string;
  image?: string;
  domain?: string;
  linkType?: LinkType;
  createdAt: string;
}

export interface MediaRequest {
  conversationId: string;
  mediaType?: MediaType;
  page?: number;
  size?: number;
  senderId?: string;
  startTime?: number;
  endTime?: number;
  keyword?: string;
}

export interface MediaAlbum {
  id: number;
  userId: string;
  conversationId: string;
  albumType: AlbumType;
  name: string;
  description?: string;
  coverMediaId?: string;
  mediaCount: number;
  totalSize: number;
  createdAt: string;
  updatedAt: string;
}
