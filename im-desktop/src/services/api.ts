import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';

// API配置
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

/**
 * API客户端配置
 */
export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    return response.data;
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token过期，清除本地存储并跳转到登录页
      localStorage.removeItem('auth_token');
      localStorage.removeItem('user_id');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// ==================== 认证API ====================

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: string;
  username: string;
  nickname?: string;
  avatar?: string;
}

export const authApi = {
  login: (data: LoginRequest): Promise<LoginResponse> =>
    apiClient.post('/api/auth/login', data),

  register: (data: {
    username: string;
    password: string;
    nickname?: string;
    email?: string;
  }): Promise<LoginResponse> =>
    apiClient.post('/api/auth/register', data),

  logout: (): Promise<void> =>
    apiClient.post('/api/auth/logout'),

  refreshToken: (): Promise<{ token: string }> =>
    apiClient.post('/api/auth/refresh'),
};

// ==================== 消息API ====================

export interface Message {
  id: string;
  conversationId: string;
  senderId: string;
  senderName?: string;
  senderAvatar?: string;
  content: string;
  messageType: 'TEXT' | 'IMAGE' | 'FILE' | 'VOICE' | 'LOCATION';
  timestamp: number;
  status: 'sending' | 'sent' | 'delivered' | 'read' | 'failed';
  extra?: Record<string, any>;
}

export interface SendMessageRequest {
  conversationId: string;
  content: string;
  messageType?: string;
  extra?: Record<string, any>;
}

export const messageApi = {
  sendMessage: (data: SendMessageRequest): Promise<Message> =>
    apiClient.post('/api/messages', data),

  getMessages: (params: {
    conversationId: string;
    page?: number;
    size?: number;
    beforeMessageId?: string;
  }): Promise<{ content: Message[]; totalElements: number }> =>
    apiClient.get('/api/messages', { params }),

  recallMessage: (messageId: string): Promise<void> =>
    apiClient.post(`/api/messages/${messageId}/recall`),

  deleteMessage: (messageId: string): Promise<void> =>
    apiClient.delete(`/api/messages/${messageId}`),
};

// ==================== 会话API ====================

export interface Conversation {
  id: string;
  type: 'DIRECT' | 'GROUP';
  name?: string;
  avatar?: string;
  participants: string[];
  lastMessage?: Message;
  unreadCount: number;
  updatedAt: number;
}

export const conversationApi = {
  getConversations: (params?: { page?: number; size?: number }): Promise<{
    content: Conversation[];
    totalElements: number;
  }> => apiClient.get('/api/conversations', { params }),

  getConversation: (id: string): Promise<Conversation> =>
    apiClient.get(`/api/conversations/${id}`),

  createConversation: (data: {
    participantIds: string[];
    name?: string;
    type?: string;
  }): Promise<Conversation> =>
    apiClient.post('/api/conversations', data),

  markAsRead: (conversationId: string): Promise<void> =>
    apiClient.post(`/api/conversations/${conversationId}/read`),
};

// ==================== 好友API ====================

export interface Friend {
  id: string;
  userId: string;
  username: string;
  nickname?: string;
  avatar?: string;
  status?: string;
  remark?: string;
}

export const friendApi = {
  getFriends: (): Promise<Friend[]> =>
    apiClient.get('/api/friends'),

  sendFriendRequest: (targetUserId: string, message?: string): Promise<void> =>
    apiClient.post('/api/friends/requests', { targetUserId, message }),

  handleFriendRequest: (requestId: string, accept: boolean): Promise<void> =>
    apiClient.post(`/api/friends/requests/${requestId}/${accept ? 'accept' : 'reject'}`),

  deleteFriend: (friendId: string): Promise<void> =>
    apiClient.delete(`/api/friends/${friendId}`),
};

// ==================== 群组API ====================

export interface Group {
  id: string;
  name: string;
  avatar?: string;
  description?: string;
  ownerId: string;
  memberCount: number;
  createdAt: number;
}

export const groupApi = {
  getGroups: (): Promise<Group[]> =>
    apiClient.get('/api/groups'),

  getGroup: (id: string): Promise<Group> =>
    apiClient.get(`/api/groups/${id}`),

  createGroup: (data: {
    name: string;
    memberIds?: string[];
    avatar?: string;
    description?: string;
  }): Promise<Group> =>
    apiClient.post('/api/groups', data),

  joinGroup: (groupId: string): Promise<void> =>
    apiClient.post(`/api/groups/${groupId}/join`),

  leaveGroup: (groupId: string): Promise<void> =>
    apiClient.post(`/api/groups/${groupId}/leave`),
};

// ==================== 用户API ====================

export interface User {
  id: string;
  username: string;
  nickname?: string;
  avatar?: string;
  signature?: string;
  email?: string;
  phone?: string;
  status?: string;
}

export const userApi = {
  getCurrentUser: (): Promise<User> =>
    apiClient.get('/api/users/me'),

  getUserInfo: (userId: string): Promise<User> =>
    apiClient.get(`/api/users/${userId}`),

  updateUserInfo: (data: Partial<User>): Promise<User> =>
    apiClient.put('/api/users/me', data),

  searchUsers: (keyword: string): Promise<User[]> =>
    apiClient.get('/api/users/search', { params: { keyword } }),

  uploadAvatar: (file: File): Promise<{ url: string }> => {
    const formData = new FormData();
    formData.append('file', file);
    return apiClient.post('/api/users/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};

export default apiClient;
