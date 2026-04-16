export interface Message {
  id: string;
  from: string;
  to: string;
  type: 'single' | 'group' | 'system';
  contentType: 'text' | 'image' | 'voice' | 'video' | 'file';
  content: string;
  timestamp: number;
  extras?: Record<string, any>;
}

export interface Conversation {
  id: string;
  type: 'single' | 'group';
  targetId: string;
  targetName: string;
  targetAvatar?: string;
  lastMessage: string;
  lastMessageTime: number;
  unreadCount: number;
}

export interface UserInfo {
  userId: string;
  username: string;
  avatar?: string;
  signature?: string;
  onlineStatus?: 'online' | 'away' | 'busy' | 'offline';
}

export interface Friend {
  userId: string;
  username: string;
  avatar?: string;
  remark?: string;
  onlineStatus: string;
}

export interface ConnectionStatus {
  status: 'disconnected' | 'connecting' | 'connected' | 'error';
  message?: string;
}
