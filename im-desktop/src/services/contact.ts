// 联系人服务
import { invoke } from '@tauri-apps/api/core';

export interface Contact {
    id: string;
    username: string;
    nickname: string;
    avatar?: string;
    remark?: string;
    status: 'online' | 'offline' | 'away' | 'busy';
    lastSeen?: number;
    signature?: string;
    region?: string;
}

export interface ContactGroup {
    id: string;
    name: string;
    contacts: Contact[];
}

export interface FriendRequest {
    id: string;
    fromUser: Contact;
    message: string;
    status: 'pending' | 'accepted' | 'rejected';
    createdAt: number;
}

class ContactService {
    private contacts: Contact[] = [];
    private groups: ContactGroup[] = [];
    private friendRequests: FriendRequest[] = [];

    // 获取所有联系人
    async getContacts(): Promise<Contact[]> {
        try {
            // 模拟数据
            this.contacts = [
                {
                    id: 'user_001',
                    username: 'zhangsan',
                    nickname: '张三',
                    avatar: '/assets/avatars/user1.png',
                    remark: '同事-前端',
                    status: 'online',
                    signature: '热爱生活，热爱代码',
                    region: '北京'
                },
                {
                    id: 'user_002',
                    username: 'lisi',
                    nickname: '李四',
                    avatar: '/assets/avatars/user2.png',
                    status: 'away',
                    lastSeen: Date.now() - 3600000,
                    signature: '出差中...',
                    region: '上海'
                },
                {
                    id: 'user_003',
                    username: 'wangwu',
                    nickname: '王五',
                    avatar: '/assets/avatars/user3.png',
                    remark: '产品经理',
                    status: 'offline',
                    lastSeen: Date.now() - 86400000,
                    region: '深圳'
                },
                {
                    id: 'user_004',
                    username: 'zhaoliu',
                    nickname: '赵六',
                    status: 'online',
                    signature: '奋斗ing',
                    region: '杭州'
                },
                {
                    id: 'user_005',
                    username: 'qianqi',
                    nickname: '钱七',
                    status: 'busy',
                    signature: '开会中，稍后回复',
                    region: '广州'
                }
            ];
            return this.contacts;
        } catch (error) {
            console.error('获取联系人失败:', error);
            return [];
        }
    }

    // 搜索联系人
    async searchContacts(keyword: string): Promise<Contact[]> {
        if (!keyword.trim()) {
            return this.contacts;
        }
        
        const lowerKeyword = keyword.toLowerCase();
        return this.contacts.filter(contact => 
            contact.nickname.toLowerCase().includes(lowerKeyword) ||
            contact.username.toLowerCase().includes(lowerKeyword) ||
            (contact.remark && contact.remark.toLowerCase().includes(lowerKeyword))
        );
    }

    // 按字母分组联系人
    groupContactsByLetter(contacts: Contact[]): Map<string, Contact[]> {
        const groups = new Map<string, Contact[]>();
        
        contacts.forEach(contact => {
            const firstChar = contact.nickname.charAt(0);
            const letter = this.getFirstLetter(firstChar);
            
            if (!groups.has(letter)) {
                groups.set(letter, []);
            }
            groups.get(letter)!.push(contact);
        });
        
        // 排序
        const sortedGroups = new Map([...groups.entries()].sort());
        return sortedGroups;
    }

    // 获取首字母
    private getFirstLetter(char: string): string {
        // 简单实现：如果是中文，返回拼音首字母
        const code = char.charCodeAt(0);
        if (code >= 0x4e00 && code <= 0x9fa5) {
            // 中文，返回 # 代表需要拼音处理
            return '#';
        }
        return char.toUpperCase();
    }

    // 获取好友申请列表
    async getFriendRequests(): Promise<FriendRequest[]> {
        this.friendRequests = [
            {
                id: 'req_001',
                fromUser: {
                    id: 'user_006',
                    username: 'newfriend1',
                    nickname: '新朋友1',
                    status: 'online'
                },
                message: '你好，我是通过群聊添加的',
                status: 'pending',
                createdAt: Date.now() - 7200000
            }
        ];
        return this.friendRequests;
    }

    // 处理好友申请
    async handleFriendRequest(requestId: string, accept: boolean): Promise<boolean> {
        console.log(`处理好友申请: ${requestId}, 接受: ${accept}`);
        return true;
    }

    // 添加好友
    async addFriend(username: string, message?: string): Promise<boolean> {
        try {
            console.log(`添加好友: ${username}, 验证消息: ${message}`);
            return true;
        } catch (error) {
            console.error('添加好友失败:', error);
            return false;
        }
    }

    // 删除好友
    async deleteFriend(contactId: string): Promise<boolean> {
        try {
            this.contacts = this.contacts.filter(c => c.id !== contactId);
            return true;
        } catch (error) {
            console.error('删除好友失败:', error);
            return false;
        }
    }

    // 设置备注
    async setRemark(contactId: string, remark: string): Promise<boolean> {
        const contact = this.contacts.find(c => c.id === contactId);
        if (contact) {
            contact.remark = remark;
            return true;
        }
        return false;
    }

    // 获取联系人详情
    async getContactDetail(contactId: string): Promise<Contact | null> {
        return this.contacts.find(c => c.id === contactId) || null;
    }

    // 获取在线联系人数量
    getOnlineCount(): number {
        return this.contacts.filter(c => c.status === 'online').length;
    }

    // 获取联系人总数
    getTotalCount(): number {
        return this.contacts.length;
    }
}

export const contactService = new ContactService();
