// 聊天服务
import { invoke } from '@tauri-apps/api/core';

export interface ChatMessage {
    id: string;
    conversationId: string;
    senderId: string;
    senderName: string;
    senderAvatar?: string;
    content: string;
    contentType: 'text' | 'image' | 'file' | 'voice' | 'video';
    timestamp: number;
    status: 'sending' | 'sent' | 'delivered' | 'read' | 'failed';
    isMe: boolean;
    fileInfo?: {
        name: string;
        size: number;
        url: string;
    };
}

export interface Conversation {
    id: string;
    type: 'private' | 'group';
    title: string;
    avatar?: string;
    participants: string[];
    lastMessage?: ChatMessage;
    unreadCount: number;
    isPinned: boolean;
    isMuted: boolean;
    updatedAt: number;
}

class ChatService {
    private conversations: Map<string, Conversation> = new Map();
    private messages: Map<string, ChatMessage[]> = new Map();

    // 获取会话列表
    async getConversations(): Promise<Conversation[]> {
        const mockConversations: Conversation[] = [
            {
                id: 'conv_001',
                type: 'private',
                title: '张三',
                avatar: '/assets/avatars/user1.png',
                participants: ['user_001', 'user_002'],
                lastMessage: {
                    id: 'msg_001',
                    conversationId: 'conv_001',
                    senderId: 'user_002',
                    senderName: '张三',
                    content: '你好，今天的会议准备得怎么样了？',
                    contentType: 'text',
                    timestamp: Date.now() - 300000,
                    status: 'read',
                    isMe: false
                },
                unreadCount: 0,
                isPinned: true,
                isMuted: false,
                updatedAt: Date.now() - 300000
            },
            {
                id: 'conv_002',
                type: 'group',
                title: '技术部工作群',
                avatar: '/assets/avatars/group1.png',
                participants: ['user_001', 'user_002', 'user_003', 'user_004'],
                lastMessage: {
                    id: 'msg_002',
                    conversationId: 'conv_002',
                    senderId: 'user_003',
                    senderName: '李四',
                    content: '@所有人 下午3点开会',
                    contentType: 'text',
                    timestamp: Date.now() - 1800000,
                    status: 'delivered',
                    isMe: false
                },
                unreadCount: 5,
                isPinned: false,
                isMuted: false,
                updatedAt: Date.now() - 1800000
            },
            {
                id: 'conv_003',
                type: 'private',
                title: '王五',
                avatar: '/assets/avatars/user3.png',
                participants: ['user_001', 'user_005'],
                lastMessage: {
                    id: 'msg_003',
                    conversationId: 'conv_003',
                    senderId: 'user_001',
                    senderName: '我',
                    content: '好的，明天见',
                    contentType: 'text',
                    timestamp: Date.now() - 86400000,
                    status: 'read',
                    isMe: true
                },
                unreadCount: 0,
                isPinned: false,
                isMuted: true,
                updatedAt: Date.now() - 86400000
            }
        ];

        mockConversations.forEach(conv => {
            this.conversations.set(conv.id, conv);
        });

        return mockConversations.sort((a, b) => b.updatedAt - a.updatedAt);
    }

    // 获取消息历史
    async getMessages(conversationId: string, before?: number, limit: number = 20): Promise<ChatMessage[]> {
        const mockMessages: ChatMessage[] = [
            {
                id: 'msg_001',
                conversationId,
                senderId: 'user_002',
                senderName: '张三',
                senderAvatar: '/assets/avatars/user1.png',
                content: '在吗？',
                contentType: 'text',
                timestamp: Date.now() - 3600000,
                status: 'read',
                isMe: false
            },
            {
                id: 'msg_002',
                conversationId,
                senderId: 'user_001',
                senderName: '我',
                content: '在的，有什么事吗？',
                contentType: 'text',
                timestamp: Date.now() - 3500000,
                status: 'read',
                isMe: true
            },
            {
                id: 'msg_003',
                conversationId,
                senderId: 'user_002',
                senderName: '张三',
                content: '你好，今天的会议准备得怎么样了？',
                contentType: 'text',
                timestamp: Date.now() - 300000,
                status: 'read',
                isMe: false
            }
        ];

        this.messages.set(conversationId, mockMessages);
        return mockMessages;
    }

    // 发送消息
    async sendMessage(conversationId: string, content: string, type: ChatMessage['contentType'] = 'text'): Promise<ChatMessage> {
        const message: ChatMessage = {
            id: `msg_${Date.now()}`,
            conversationId,
            senderId: 'user_001',
            senderName: '我',
            content,
            contentType: type,
            timestamp: Date.now(),
            status: 'sending',
            isMe: true
        };

        // 模拟发送延迟
        await new Promise(resolve => setTimeout(resolve, 300));
        message.status = 'sent';

        // 保存到本地
        const existing = this.messages.get(conversationId) || [];
        existing.push(message);
        this.messages.set(conversationId, existing);

        return message;
    }

    // 标记已读
    async markAsRead(conversationId: string): Promise<void> {
        const conv = this.conversations.get(conversationId);
        if (conv) {
            conv.unreadCount = 0;
        }
    }

    // 置顶/取消置顶
    async togglePin(conversationId: string): Promise<boolean> {
        const conv = this.conversations.get(conversationId);
        if (conv) {
            conv.isPinned = !conv.isPinned;
            return conv.isPinned;
        }
        return false;
    }

    // 静音/取消静音
    async toggleMute(conversationId: string): Promise<boolean> {
        const conv = this.conversations.get(conversationId);
        if (conv) {
            conv.isMuted = !conv.isMuted;
            return conv.isMuted;
        }
        return false;
    }

    // 删除会话
    async deleteConversation(conversationId: string): Promise<void> {
        this.conversations.delete(conversationId);
        this.messages.delete(conversationId);
    }

    // 撤回消息
    async recallMessage(messageId: string): Promise<boolean> {
        console.log('撤回消息:', messageId);
        return true;
    }

    // 转发消息
    async forwardMessage(messageId: string, targetConversationIds: string[]): Promise<void> {
        console.log('转发消息:', messageId, '到:', targetConversationIds);
    }
}

export const chatService = new ChatService();
