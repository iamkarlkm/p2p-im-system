// IM Desktop - Main JavaScript

// Configuration
const API_BASE_URL = 'http://localhost:8080/api';
const WS_URL = 'ws://localhost:9000/ws';

// State
let currentUser = null;
let token = localStorage.getItem('token');
let websocket = null;
let currentChat = null;
let chatList = [];

// DOM Elements
const loginPage = document.getElementById('login-page');
const registerPage = document.getElementById('register-page');
const mainPage = document.getElementById('main-page');
const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');
const notification = document.getElementById('notification');

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    init();
});

function init() {
    // Check if user is already logged in
    if (token) {
        showMainPage();
    } else {
        showLoginPage();
    }
    
    // Setup event listeners
    setupEventListeners();
    setupModalListeners();
}

function setupEventListeners() {
    // Login form
    loginForm.addEventListener('submit', handleLogin);
    
    // Register form
    registerForm.addEventListener('submit', handleRegister);
    
    // Toggle between login and register pages
    document.getElementById('show-register').addEventListener('click', () => {
        loginPage.classList.add('hidden');
        registerPage.classList.remove('hidden');
    });
    
    document.getElementById('show-login').addEventListener('click', () => {
        registerPage.classList.add('hidden');
        loginPage.classList.remove('hidden');
    });
    
    // Logout
    document.getElementById('btn-logout').addEventListener('click', handleLogout);
    
    // Tabs
    document.querySelectorAll('.tab').forEach(tab => {
        tab.addEventListener('click', () => handleTabChange(tab.dataset.tab));
    });
    
    // Send message
    document.getElementById('btn-send').addEventListener('click', sendMessage);
    document.getElementById('message-input').addEventListener('keypress', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });
    
    // Search
    document.getElementById('search-input').addEventListener('input', handleSearch);
}

// Authentication
async function handleLogin(e) {
    e.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        
        const data = await response.json();
        
        if (response.ok && data.code === 200) {
            token = data.data.token;
            localStorage.setItem('token', token);
            currentUser = data.data;
            
            showNotification('登录成功', 'success');
            showMainPage();
        } else {
            showNotification(data.message || '登录失败', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showNotification('网络错误，请稍后重试', 'error');
    }
}

async function handleRegister(e) {
    e.preventDefault();
    
    const username = document.getElementById('reg-username').value;
    const password = document.getElementById('reg-password').value;
    const nickname = document.getElementById('reg-nickname').value;
    const email = document.getElementById('reg-email').value;
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password, nickname, email })
        });
        
        const data = await response.json();
        
        if (response.ok && data.code === 200) {
            showNotification('注册成功，请登录', 'success');
            document.getElementById('show-login').click();
        } else {
            showNotification(data.message || '注册失败', 'error');
        }
    } catch (error) {
        console.error('Register error:', error);
        showNotification('网络错误，请稍后重试', 'error');
    }
}

function handleLogout() {
    token = null;
    localStorage.removeItem('token');
    currentUser = null;
    
    if (websocket) {
        websocket.close();
        websocket = null;
    }
    
    showLoginPage();
    showNotification('已退出登录', 'success');
}

// Page Navigation
function showLoginPage() {
    loginPage.classList.remove('hidden');
    registerPage.classList.add('hidden');
    mainPage.classList.add('hidden');
}

function showRegisterPage() {
    loginPage.classList.add('hidden');
    registerPage.classList.remove('hidden');
    mainPage.classList.add('hidden');
}

async function showMainPage() {
    loginPage.classList.add('hidden');
    registerPage.classList.add('hidden');
    mainPage.classList.remove('hidden');
    
    // Load user info
    await loadUserInfo();
    
    // Load chat list
    await loadChatList();
    
    // Connect WebSocket
    connectWebSocket();
    
    // Update notification badge
    updateNotificationBadge();
}

async function loadUserInfo() {
    try {
        const response = await fetch(`${API_BASE_URL}/users/me`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        const data = await response.json();
        
        if (response.ok && data.code === 200) {
            currentUser = data.data;
            document.getElementById('user-nickname').textContent = currentUser.nickname || currentUser.username;
            document.getElementById('user-avatar').src = currentUser.avatarUrl || 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 40 40"><circle cx="20" cy="20" r="20" fill="%231890ff"/><text x="20" y="25" text-anchor="middle" fill="white" font-size="16">' + (currentUser.nickname || currentUser.username).charAt(0).toUpperCase() + '</text></svg>';
        }
    } catch (error) {
        console.error('Load user info error:', error);
    }
}

async function loadChatList() {
    try {
        // Load friends
        const friendsResponse = await fetch(`${API_BASE_URL}/friends`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        // Load groups
        const groupsResponse = await fetch(`${API_BASE_URL}/groups/my`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        const friendsData = await friendsResponse.json();
        const groupsData = await groupsResponse.json();
        
        chatList = [];
        
        if (friendsData.code === 200) {
            friendsData.data.forEach(item => {
                chatList.push({
                    type: 'private',
                    id: item.user.id,
                    name: item.friend.friendRemark || item.user.nickname || item.user.username,
                    avatar: item.user.avatarUrl,
                    lastMessage: '',
                    unreadCount: 0
                });
            });
        }
        
        if (groupsData.code === 200) {
            groupsData.data.forEach(item => {
                chatList.push({
                    type: 'group',
                    id: item.group.id,
                    name: item.group.groupName,
                    avatar: item.group.avatarUrl,
                    lastMessage: '',
                    unreadCount: 0
                });
            });
        }
        
        renderChatList();
    } catch (error) {
        console.error('Load chat list error:', error);
    }
}

function renderChatList() {
    const chatListEl = document.getElementById('chat-list');
    chatListEl.innerHTML = '';
    
    chatList.forEach(chat => {
        const chatItem = document.createElement('div');
        chatItem.className = 'chat-item';
        chatItem.dataset.id = chat.id;
        chatItem.dataset.type = chat.type;
        
        const avatarInitial = chat.name.charAt(0).toUpperCase();
        const avatarSvg = `data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 40 40"><circle cx="20" cy="20" r="20" fill="%231890ff"/><text x="20" y="25" text-anchor="middle" fill="white" font-size="16">${avatarInitial}</text></svg>`;
        
        chatItem.innerHTML = `
            <img class="avatar" src="${chat.avatar || avatarSvg}" alt="${chat.name}">
            <div class="chat-item-info">
                <div class="chat-item-name">${chat.name}</div>
                <div class="chat-item-preview">${chat.lastMessage || '暂无消息'}</div>
            </div>
            ${chat.unreadCount > 0 ? `<div class="chat-item-badge">${chat.unreadCount}</div>` : ''}
        `;
        
        chatItem.addEventListener('click', () => selectChat(chat));
        chatListEl.appendChild(chatItem);
    });
}

async function selectChat(chat) {
    currentChat = chat;
    
    // Update UI
    document.querySelectorAll('.chat-item').forEach(item => {
        item.classList.remove('active');
        if (parseInt(item.dataset.id) === chat.id && item.dataset.type === chat.type) {
            item.classList.add('active');
        }
    });
    
    document.getElementById('chat-title').textContent = chat.name;
    
    // Load messages
    await loadMessages(chat);
}

async function loadMessages(chat) {
    const messagesContainer = document.getElementById('messages-container');
    messagesContainer.innerHTML = '<div class="loading"><div class="spinner"></div></div>';
    
    try {
        let url;
        if (chat.type === 'private') {
            url = `${API_BASE_URL}/messages/private/${chat.id}?page=0&size=50`;
        } else {
            url = `${API_BASE_URL}/messages/group/${chat.id}?page=0&size=50`;
        }
        
        const response = await fetch(url, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        const data = await response.json();
        
        if (response.ok && data.code === 200) {
            renderMessages(data.data.reverse());
        } else {
            messagesContainer.innerHTML = '<div class="empty-state"><p>加载消息失败</p></div>';
        }
    } catch (error) {
        console.error('Load messages error:', error);
        messagesContainer.innerHTML = '<div class="empty-state"><p>加载消息失败</p></div>';
    }
}

function renderMessages(messages) {
    const messagesContainer = document.getElementById('messages-container');
    
    if (!messages || messages.length === 0) {
        messagesContainer.innerHTML = '<div class="empty-state"><p>暂无消息</p></div>';
        return;
    }
    
    messagesContainer.innerHTML = '';
    
    messages.forEach(msg => {
        const isSent = msg.fromUserId === currentUser.id;
        const messageEl = document.createElement('div');
        messageEl.className = `message ${isSent ? 'sent' : 'received'}`;
        
        const time = new Date(msg.createTime).toLocaleTimeString('zh-CN', { 
            hour: '2-digit', 
            minute: '2-digit' 
        });
        
        let content = msg.content;
        if (msg.msgType === 2) { // Image
            content = `<img src="${msg.content}" style="max-width: 200px; border-radius: 8px;">`;
        } else if (msg.msgType === 3) { // File
            content = `<a href="${msg.content}" target="_blank">📎 文件</a>`;
        } else if (msg.msgType === 4) { // Voice
            content = `<audio controls src="${msg.content}"></audio>`;
        }
        
        messageEl.innerHTML = `
            <img class="message-avatar" src="${isSent ? (currentUser.avatarUrl || '') : 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 36 36"><circle cx="18" cy="18" r="18" fill="%231890ff"/><text x="18" y="23" text-anchor="middle" fill="white" font-size="14">?</text></svg>'}" alt="头像">
            <div class="message-content">
                <div class="message-bubble">${content}</div>
                <div class="message-time">${time}</div>
            </div>
        `;
        
        messagesContainer.appendChild(messageEl);
    });
    
    // Scroll to bottom
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

async function sendMessage() {
    if (!currentChat) {
        showNotification('请选择会话', 'warning');
        return;
    }
    
    const input = document.getElementById('message-input');
    const content = input.value.trim();
    
    if (!content) {
        return;
    }
    
    try {
        let url, body;
        
        if (currentChat.type === 'private') {
            url = `${API_BASE_URL}/messages`;
            body = {
                toUserId: currentChat.id,
                chatType: 1,
                chatId: currentChat.id,
                msgType: 1, // Text
                content: content
            };
        } else {
            url = `${API_BASE_URL}/messages`;
            body = {
                toUserId: 0,
                chatType: 2,
                chatId: currentChat.id,
                msgType: 1,
                content: content
            };
        }
        
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        });
        
        const data = await response.json();
        
        if (response.ok && data.code === 200) {
            input.value = '';
            
            // Reload messages
            await loadMessages(currentChat);
            
            // Send via WebSocket if connected
            if (websocket && websocket.readyState === WebSocket.OPEN) {
                websocket.send(JSON.stringify({
                    cmd: currentChat.type === 'private' ? 1001 : 1002,
                    seq: data.data.msgId,
                    timestamp: Date.now(),
                    data: {
                        ...body,
                        fromUserId: currentUser.id
                    }
                }));
            }
        } else {
            showNotification(data.message || '发送失败', 'error');
        }
    } catch (error) {
        console.error('Send message error:', error);
        showNotification('发送失败', 'error');
    }
}

// Tab Change
function handleTabChange(tab) {
    document.querySelectorAll('.tab').forEach(t => {
        t.classList.remove('active');
        if (t.dataset.tab === tab) {
            t.classList.add('active');
        }
    });
    
    // Reload chat list based on tab
    loadChatList();
}

// Search
function handleSearch(e) {
    const query = e.target.value.toLowerCase();
    
    if (!query) {
        renderChatList();
        return;
    }
    
    const filtered = chatList.filter(chat => 
        chat.name.toLowerCase().includes(query)
    );
    
    const chatListEl = document.getElementById('chat-list');
    chatListEl.innerHTML = '';
    
    filtered.forEach(chat => {
        const chatItem = document.createElement('div');
        chatItem.className = 'chat-item';
        chatItem.dataset.id = chat.id;
        chatItem.dataset.type = chat.type;
        
        const avatarInitial = chat.name.charAt(0).toUpperCase();
        const avatarSvg = `data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 40 40"><circle cx="20" cy="20" r="20" fill="%231890ff"/><text x="20" y="25" text-anchor="middle" fill="white" font-size="16">${avatarInitial}</text></svg>`;
        
        chatItem.innerHTML = `
            <img class="avatar" src="${chat.avatar || avatarSvg}" alt="${chat.name}">
            <div class="chat-item-info">
                <div class="chat-item-name">${chat.name}</div>
                <div class="chat-item-preview">${chat.lastMessage || '暂无消息'}</div>
            </div>
        `;
        
        chatItem.addEventListener('click', () => selectChat(chat));
        chatListEl.appendChild(chatItem);
    });
}

// WebSocket
function connectWebSocket() {
    if (!token) return;
    
    websocket = new WebSocket(`${WS_URL}?token=${token}`);
    
    websocket.onopen = () => {
        console.log('WebSocket connected');
        showNotification('已连接', 'success');
    };
    
    websocket.onmessage = (event) => {
        try {
            const data = JSON.parse(event.data);
            handleWebSocketMessage(data);
        } catch (error) {
            console.error('WebSocket message error:', error);
        }
    };
    
    websocket.onclose = () => {
        console.log('WebSocket disconnected');
        // Reconnect after 5 seconds
        setTimeout(connectWebSocket, 5000);
    };
    
    websocket.onerror = (error) => {
        console.error('WebSocket error:', error);
    };
}

function handleWebSocketMessage(data) {
    const cmd = data.cmd;
    
    switch (cmd) {
        case 1001: // Private message
        case 1002: // Group message
            // Add message to current chat if applicable
            if (currentChat && 
                ((data.data.chatType === 1 && data.data.toUserId === currentChat.id) ||
                 (data.data.chatType === 2 && data.data.chatId === currentChat.id))) {
                loadMessages(currentChat);
            }
            break;
            
        case 1003: // Message ACK
            console.log('Message ACK:', data);
            break;
            
        case 2001: // User online
        case 2002: // User offline
            // Update friend status
            break;
            
        case 3002: // Pong
            // Heartbeat response
            break;
    }
}

// Heartbeat
setInterval(() => {
    if (websocket && websocket.readyState === WebSocket.OPEN) {
        websocket.send(JSON.stringify({
            cmd: 3001,
            timestamp: Date.now()
        }));
    }
}, 30000);

// Notification
function showNotification(message, type = 'info') {
    notification.textContent = message;
    notification.className = `notification ${type}`;
    notification.classList.remove('hidden');
    
    setTimeout(() => {
        notification.classList.add('hidden');
    }, 3000);
}

// ============================================
// Friend Request Management
// ============================================

// Load friend requests
async function loadFriendRequests() {
    try {
        const response = await fetch(`${API_BASE_URL}/friend-requests/pending`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        const data = await response.json();
        
        if (response.ok && data.code === 200) {
            renderFriendRequests(data.data, 'received');
        }
    } catch (error) {
        console.error('Load friend requests error:', error);
    }
}

// Load sent friend requests
async function loadSentFriendRequests() {
    try {
        const response = await fetch(`${API_BASE_URL}/friend-requests/sent`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        const data = await response.json();
        
        if (response.ok && data.code === 200) {
            renderFriendRequests(data.data, 'sent');
        }
    } catch (error) {
        console.error('Load sent friend requests error:', error);
    }
}

// Render friend requests
function renderFriendRequests(requests, type) {
    const container = document.getElementById('friend-requests-list');
    const emptyState = document.getElementById('no-friend-requests');
    
    if (!requests || requests.length === 0) {
        container.innerHTML = '';
        emptyState.classList.remove('hidden');
        return;
    }
    
    emptyState.classList.add('hidden');
    container.innerHTML = '';
    
    requests.forEach(request => {
        const item = document.createElement('div');
        item.className = 'friend-request-item';
        
        const user = type === 'received' ? request.fromUser : request.toUser;
        const avatarInitial = user.nickname ? user.nickname.charAt(0).toUpperCase() : user.username.charAt(0).toUpperCase();
        const avatarSvg = `data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 48 48"><circle cx="24" cy="24" r="24" fill="%231890ff"/><text x="24" y="30" text-anchor="middle" fill="white" font-size="18">${avatarInitial}</text></svg>`;
        
        let statusHtml = '';
        if (type === 'received') {
            statusHtml = `
                <div class="friend-request-actions">
                    <button class="btn-primary" onclick="acceptFriendRequest(${request.id})">同意</button>
                    <button class="btn-secondary" onclick="rejectFriendRequest(${request.id})">拒绝</button>
                </div>
            `;
        } else {
            statusHtml = `<span class="status">待处理</span>`;
        }
        
        item.innerHTML = `
            <img class="avatar" src="${user.avatarUrl || avatarSvg}" alt="${user.username}">
            <div class="friend-request-info">
                <div class="friend-request-name">${user.nickname || user.username}</div>
                <div class="friend-request-desc">@${user.username}</div>
            </div>
            ${statusHtml}
        `;
        
        container.appendChild(item);
    });
}

// Accept friend request
async function acceptFriendRequest(requestId) {
    try {
        const response = await fetch(`${API_BASE_URL}/friend-requests/${requestId}/accept`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        const data = await response.json();
        
        if (response.ok && data.code === 200) {
            showNotification('已同意好友请求', 'success');
            loadFriendRequests();
            loadChatList();
            updateUnreadCount();
        } else {
            showNotification(data.message || '操作失败', 'error');
        }
    } catch (error) {
        console.error('Accept friend request error:', error);
        showNotification('操作失败', 'error');
    }
}

// Reject friend request
async function rejectFriendRequest(requestId) {
    try {
        const response = await fetch(`${API_BASE_URL}/friend-requests/${requestId}/reject`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        const data = await response.json();
        
        if (response.ok && data.code === 200) {
            showNotification('已拒绝好友请求', 'success');
            loadFriendRequests();
        } else {
            showNotification(data.message || '操作失败', 'error');
        }
    } catch (error) {
        console.error('Reject friend request error:', error);
        showNotification('操作失败', 'error');
    }
}

// Make functions global
window.acceptFriendRequest = acceptFriendRequest;
window.rejectFriendRequest = rejectFriendRequest;

// ============================================
// Notification Center
// ============================================

// Load notifications
async function loadNotifications(type = 'all') {
    try {
        const url = type === 'all' 
            ? `${API_BASE_URL}/notifications` 
            : `${API_BASE_URL}/notifications?type=${type}`;
        
        const response = await fetch(url, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        const data = await response.json();
        
        if (response.ok && data.code === 200) {
            renderNotifications(data.data);
        }
    } catch (error) {
        console.error('Load notifications error:', error);
    }
}

// Render notifications
function renderNotifications(notifications) {
    const container = document.getElementById('notifications-list');
    const emptyState = document.getElementById('no-notifications');
    
    if (!notifications || notifications.length === 0) {
        container.innerHTML = '';
        emptyState.classList.remove('hidden');
        return;
    }
    
    emptyState.classList.add('hidden');
    container.innerHTML = '';
    
    notifications.forEach(notification => {
        const item = document.createElement('div');
        item.className = `notification-item ${notification.read ? '' : 'unread'}`;
        
        let icon = '🔔';
        switch (notification.type) {
            case 'FRIEND_REQUEST':
                icon = '👤';
                break;
            case 'FRIEND_ADDED':
                icon = '✅';
                break;
            case 'GROUP_INVITE':
                icon = '👥';
                break;
            case 'GROUP_MEMBER_JOINED':
                icon = '🎉';
                break;
            case 'SYSTEM':
                icon = '⚙️';
                break;
        }
        
        const time = new Date(notification.createTime).toLocaleString('zh-CN');
        
        item.innerHTML = `
            <div class="notification-icon">${icon}</div>
            <div class="notification-content">
                <div class="notification-title">${notification.title}</div>
                <div class="notification-desc">${notification.content}</div>
                <div class="notification-time">${time}</div>
            </div>
        `;
        
        item.addEventListener('click', () => markNotificationAsRead(notification.id));
        container.appendChild(item);
    });
}

// Mark notification as read
async function markNotificationAsRead(notificationId) {
    try {
        const response = await fetch(`${API_BASE_URL}/notifications/${notificationId}/read`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (response.ok) {
            loadNotifications();
            updateNotificationBadge();
        }
    } catch (error) {
        console.error('Mark notification as read error:', error);
    }
}

// Mark all notifications as read
async function markAllNotificationsAsRead() {
    try {
        const response = await fetch(`${API_BASE_URL}/notifications/read-all`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (response.ok) {
            showNotification('已全部标为已读', 'success');
            loadNotifications();
            updateNotificationBadge();
        }
    } catch (error) {
        console.error('Mark all notifications as read error:', error);
    }
}

// Update notification badge
async function updateNotificationBadge() {
    try {
        const response = await fetch(`${API_BASE_URL}/notifications/unread-count`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        const data = await response.json();
        
        if (response.ok && data.code === 200) {
            const badge = document.getElementById('notification-badge');
            const count = data.data;
            
            if (count > 0) {
                badge.textContent = count > 99 ? '99+' : count;
                badge.classList.remove('hidden');
            } else {
                badge.classList.add('hidden');
            }
        }
    } catch (error) {
        console.error('Update notification badge error:', error);
    }
}

// Update friend request unread count
async function updateFriendRequestUnreadCount() {
    try {
        const response = await fetch(`${API_BASE_URL}/friend-requests/unread-count`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        const data = await response.json();
        
        if (response.ok && data.code === 200) {
            // Update badge in requests tab if needed
        }
    } catch (error) {
        console.error('Update friend request count error:', error);
    }
}

// ============================================
// User Search
// ============================================

// Search users
async function searchUsers(keyword) {
    if (!keyword || keyword.trim() === '') {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/users/search?keyword=${encodeURIComponent(keyword)}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        const data = await response.json();
        
        if (response.ok && data.code === 200) {
            renderUserSearchResults(data.data);
        }
    } catch (error) {
        console.error('Search users error:', error);
    }
}

// Render user search results
function renderUserSearchResults(users) {
    const container = document.getElementById('user-search-results');
    const sentMsg = document.getElementById('friend-request-sent');
    
    sentMsg.classList.add('hidden');
    
    if (!users || users.length === 0) {
        container.innerHTML = '<div class="empty-state"><p>未找到用户</p></div>';
        return;
    }
    
    container.innerHTML = '';
    
    users.forEach(user => {
        const item = document.createElement('div');
        item.className = 'search-result-item';
        
        const avatarInitial = user.nickname ? user.nickname.charAt(0).toUpperCase() : user.username.charAt(0).toUpperCase();
        const avatarSvg = `data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 48 48"><circle cx="24" cy="24" r="24" fill="%231890ff"/><text x="24" y="30" text-anchor="middle" fill="white" font-size="18">${avatarInitial}</text></svg>`;
        
        const desc = user.nickname ? `@${user.username}` : (user.email || '');
        
        item.innerHTML = `
            <img class="avatar" src="${user.avatarUrl || avatarSvg}" alt="${user.username}">
            <div class="search-result-info">
                <div class="search-result-name">${user.nickname || user.username}</div>
                <div class="search-result-desc">${desc}</div>
            </div>
            <div class="search-result-actions">
                <button class="btn-primary" onclick="sendFriendRequest(${user.id})">添加</button>
            </div>
        `;
        
        container.appendChild(item);
    });
}

// Send friend request
async function sendFriendRequest(userId) {
    try {
        const response = await fetch(`${API_BASE_URL}/friend-requests`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ toUserId: userId })
        });
        
        const data = await response.json();
        
        if (response.ok && data.code === 200) {
            document.getElementById('friend-request-sent').classList.remove('hidden');
            document.getElementById('user-search-results').innerHTML = '';
            showNotification('好友请求已发送', 'success');
        } else {
            showNotification(data.message || '发送失败', 'error');
        }
    } catch (error) {
        console.error('Send friend request error:', error);
        showNotification('发送失败', 'error');
    }
}

// Make function global
window.sendFriendRequest = sendFriendRequest;

// ============================================
// Modal Management
// ============================================

// Setup modal event listeners
function setupModalListeners() {
    // Add friend modal
    document.getElementById('btn-add-friend').addEventListener('click', () => {
        document.getElementById('add-friend-modal').classList.remove('hidden');
    });
    
    // Notifications modal
    document.getElementById('btn-notifications').addEventListener('click', () => {
        document.getElementById('notifications-modal').classList.remove('hidden');
        loadNotifications();
    });
    
    // Friend requests tab
    document.querySelectorAll('.tab[data-tab="requests"]').forEach(tab => {
        tab.addEventListener('click', () => {
            document.getElementById('friend-requests-modal').classList.remove('hidden');
            loadFriendRequests();
        });
    });
    
    // Close buttons
    document.querySelectorAll('.modal-close').forEach(btn => {
        btn.addEventListener('click', () => {
            const modalId = btn.dataset.modal;
            document.getElementById(modalId).classList.add('hidden');
        });
    });
    
    // Close modal on backdrop click
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.classList.add('hidden');
            }
        });
    });
    
    // User search
    document.getElementById('btn-search-user').addEventListener('click', () => {
        const keyword = document.getElementById('user-search-input').value;
        searchUsers(keyword);
    });
    
    document.getElementById('user-search-input').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            const keyword = e.target.value;
            searchUsers(keyword);
        }
    });
    
    // Notification tabs
    document.querySelectorAll('.notification-tab').forEach(tab => {
        tab.addEventListener('click', () => {
            document.querySelectorAll('.notification-tab').forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            loadNotifications(tab.dataset.type);
        });
    });
    
    // Friend request tabs
    document.querySelectorAll('.friend-request-tab').forEach(tab => {
        tab.addEventListener('click', () => {
            document.querySelectorAll('.friend-request-tab').forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            
            if (tab.dataset.type === 'received') {
                loadFriendRequests();
            } else {
                loadSentFriendRequests();
            }
        });
    });
    
    // Mark all as read
    document.getElementById('btn-mark-all-read').addEventListener('click', markAllNotificationsAsRead);
}
