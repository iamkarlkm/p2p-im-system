import { invoke } from '@tauri-apps/api/core';

// 应用状态
interface AppState {
    isLoggedIn: boolean;
    currentUser: any | null;
    conversations: any[];
    currentConversation: string | null;
    messages: Map<string, any[]>;
}

const state: AppState = {
    isLoggedIn: false,
    currentUser: null,
    conversations: [],
    currentConversation: null,
    messages: new Map()
};

// DOM 元素
document.addEventListener('DOMContentLoaded', async () => {
    console.log('IM Desktop 初始化中...');
    
    // 初始化问候
    try {
        const response = await invoke<string>('greet', { name: '用户' });
        console.log(response);
    } catch (error) {
        console.error('调用 greet 失败:', error);
    }
    
    // 初始化事件监听
    initEventListeners();
    
    // 检查登录状态
    checkLoginStatus();
});

// 初始化事件监听
function initEventListeners(): void {
    // 登录表单
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    
    // 发送消息
    const sendBtn = document.getElementById('btn-send');
    if (sendBtn) {
        sendBtn.addEventListener('click', handleSendMessage);
    }
    
    // 消息输入框回车发送
    const messageInput = document.getElementById('message-input') as HTMLTextAreaElement;
    if (messageInput) {
        messageInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                handleSendMessage();
            }
        });
    }
    
    // 导航切换
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const tab = (item as HTMLElement).dataset.tab;
            switchTab(tab);
        });
    });
}

// 处理登录
async function handleLogin(e: Event): Promise<void> {
    e.preventDefault();
    
    const usernameInput = document.getElementById('username') as HTMLInputElement;
    const passwordInput = document.getElementById('password') as HTMLInputElement;
    const errorDiv = document.getElementById('login-error');
    
    const username = usernameInput.value.trim();
    const password = passwordInput.value.trim();
    
    if (!username || !password) {
        if (errorDiv) {
            errorDiv.textContent = '请输入用户名和密码';
        }
        return;
    }
    
    try {
        console.log('正在登录:', username);
        // 模拟登录成功
        state.isLoggedIn = true;
        state.currentUser = {
            id: 'user_001',
            username: username,
            nickname: username
        };
        
        // 切换到主界面
        showMainPage();
        
        // 加载会话列表
        await loadConversations();
        
    } catch (error) {
        console.error('登录失败:', error);
        if (errorDiv) {
            errorDiv.textContent = '登录失败，请检查用户名和密码';
        }
    }
}

// 检查登录状态
function checkLoginStatus(): void {
    // TODO: 从本地存储检查token
    console.log('检查登录状态...');
}

// 显示主界面
function showMainPage(): void {
    const loginPage = document.getElementById('login-page');
    const mainPage = document.getElementById('main-page');
    
    if (loginPage) loginPage.classList.remove('active');
    if (mainPage) mainPage.classList.add('active');
    
    // 更新用户信息
    const userNameEl = document.getElementById('current-user-name');
    if (userNameEl && state.currentUser) {
        userNameEl.textContent = state.currentUser.nickname || state.currentUser.username;
    }
}

// 加载会话列表
async function loadConversations(): Promise<void> {
    try {
        // 模拟数据
        state.conversations = [
            {
                id: 'conv_001',
                title: '张三',
                lastMessage: { content: '你好，在吗？', timestamp: Date.now() },
                unreadCount: 0
            },
            {
                id: 'conv_002',
                title: '工作群',
                lastMessage: { content: '下午开会', timestamp: Date.now() },
                unreadCount: 5
            }
        ];
        
        renderConversations();
    } catch (error) {
        console.error('加载会话列表失败:', error);
    }
}

// 渲染会话列表
function renderConversations(): void {
    const container = document.getElementById('conversations-container');
    if (!container) return;
    
    container.innerHTML = state.conversations.map(conv => `
        <div class="conversation-item ${conv.id === state.currentConversation ? 'active' : ''}" 
             data-conv-id="${conv.id}">
            <div class="conv-avatar">
                <img src="/assets/default-avatar.png" alt="${conv.title}">
            </div>
            <div class="conv-info">
                <div class="conv-title">${conv.title}</div>
                <div class="conv-preview">${conv.lastMessage?.content || ''}</div>
            </div>
            <div class="conv-meta">
                <span class="conv-time">${formatTime(conv.lastMessage?.timestamp)}</span>
                ${conv.unreadCount > 0 ? `<span class="conv-badge">${conv.unreadCount}</span>` : ''}
            </div>
        </div>
    `).join('');
    
    // 添加点击事件
    container.querySelectorAll('.conversation-item').forEach(item => {
        item.addEventListener('click', () => {
            const convId = (item as HTMLElement).dataset.convId;
            if (convId) {
                selectConversation(convId);
            }
        });
    });
}

// 选择会话
function selectConversation(convId: string): void {
    state.currentConversation = convId;
    
    const conv = state.conversations.find(c => c.id === convId);
    if (conv) {
        const titleEl = document.getElementById('chat-title-text');
        if (titleEl) {
            titleEl.textContent = conv.title;
        }
    }
    
    renderConversations();
    loadMessages(convId);
}

// 加载消息
function loadMessages(convId: string): void {
    const container = document.getElementById('chat-messages');
    if (!container) return;
    
    // 模拟消息数据
    const messages = [
        { id: '1', senderId: 'other', content: '你好！', timestamp: Date.now() - 3600000 },
        { id: '2', senderId: 'me', content: '你好，有什么事吗？', timestamp: Date.now() - 3000000 }
    ];
    
    container.innerHTML = messages.map(msg => `
        <div class="message ${msg.senderId === 'me' ? 'message-sent' : 'message-received'}">
            <div class="message-content">${msg.content}</div>
            <div class="message-time">${formatTime(msg.timestamp)}</div>
        </div>
    `).join('');
    
    // 滚动到底部
    container.scrollTop = container.scrollHeight;
}

// 发送消息
async function handleSendMessage(): Promise<void> {
    const input = document.getElementById('message-input') as HTMLTextAreaElement;
    if (!input) return;
    
    const content = input.value.trim();
    if (!content || !state.currentConversation) return;
    
    try {
        console.log('发送消息:', content);
        
        // 添加到界面
        const container = document.getElementById('chat-messages');
        if (container) {
            const msgDiv = document.createElement('div');
            msgDiv.className = 'message message-sent';
            msgDiv.innerHTML = `
                <div class="message-content">${escapeHtml(content)}</div>
                <div class="message-time">${formatTime(Date.now())}</div>
            `;
            container.appendChild(msgDiv);
            container.scrollTop = container.scrollHeight;
        }
        
        input.value = '';
        
    } catch (error) {
        console.error('发送消息失败:', error);
    }
}

// 切换标签
function switchTab(tab: string | undefined): void {
    if (!tab) return;
    
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
    });
    
    const activeItem = document.querySelector(`[data-tab="${tab}"]`);
    if (activeItem) {
        activeItem.classList.add('active');
    }
    
    console.log('切换到标签:', tab);
}

// 工具函数
function formatTime(timestamp: number | undefined): string {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
}

function escapeHtml(text: string): string {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// 导出供全局使用
(window as any).IMDesktop = {
    state,
    showMainPage,
    loadConversations
};
