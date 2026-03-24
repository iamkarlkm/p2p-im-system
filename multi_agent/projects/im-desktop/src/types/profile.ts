export interface UserProfileDTO {
  userId: string;
  nickname?: string;
  realName?: string;
  avatarUrl?: string;
  avatarThumbnailUrl?: string;
  bio?: string;
  gender?: number;
  birthday?: string;
  country?: string;
  province?: string;
  city?: string;
  language?: string;
  timezone?: string;
  website?: string;
  email?: string;
  phone?: string;
  onlineStatusVisibility?: 'public' | 'friends' | 'private';
  lastSeenVisibility?: 'public' | 'friends' | 'private';
  avatarVisibility?: 'public' | 'friends' | 'private';
  profileVisibility?: 'public' | 'friends' | 'private';
  searchableBy?: 'all' | 'phone' | 'id';
  friendRequestPolicy?: 'everyone' | 'requires_approval' | 'nobody';
  readReceiptEnabled?: boolean;
  showOnlineStatus?: boolean;
  showTypingStatus?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface UserProfileRequest {
  nickname?: string;
  realName?: string;
  avatarUrl?: string;
  avatarThumbnailUrl?: string;
  bio?: string;
  gender?: number;
  birthday?: string;
  country?: string;
  province?: string;
  city?: string;
  language?: string;
  timezone?: string;
  website?: string;
  email?: string;
  phone?: string;
  onlineStatusVisibility?: string;
  lastSeenVisibility?: string;
  avatarVisibility?: string;
  profileVisibility?: string;
  searchableBy?: string;
  friendRequestPolicy?: string;
  readReceiptEnabled?: boolean;
  showOnlineStatus?: boolean;
  showTypingStatus?: boolean;
}
