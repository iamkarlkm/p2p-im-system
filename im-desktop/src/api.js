// IM Desktop - API Service Module
// 处理所有与后端的 API 通信

const API_BASE_URL = 'http://localhost:8080/api';

// 请求超时时间
const REQUEST_TIMEOUT = 30000;

// Token 存储键名
const TOKEN_KEY = 'token';
const REFRESH_TOKEN_KEY = 'refresh_token';

// API 错误码
const ERROR_CODES = {
    SUCCESS: 200,
    UNAUTHORIZED: 401,
    FORBIDDEN: 403,
    NOT_FOUND: 404,
    SERVER_ERROR: 500
};

/**
 * 获取存储的 Token
 */
function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

/**
 * 获取存储的刷新 Token
 */
function getRefreshToken() {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
}

/**
 * 存储 Token
 */
function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

/**
 * 存储刷新 Token
 */
function setRefreshToken(refreshToken) {
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
}

/**
 * 清除 Token
 */
function clearToken() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
}

/**
 * 构建请求头
 */
function buildHeaders(includeAuth = true) {
    const headers = {
        'Content-Type': 'application/json'
    };
    
    if (includeAuth) {
        const token = getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
    }
    
    return headers;
}

/**
 * 通用请求方法
 */
async function request(url, options = {}) {
    const fullUrl = url.startsWith('http') ? url : API_BASE_URL + url;
    const token = getToken();
    
    const defaultOptions = {
        headers: buildHeaders(true),
        timeout: REQUEST_TIMEOUT
    };
    
    const mergedOptions = { ...defaultOptions, ...options };
    
    // 如果有 token，在 URL 中添加
    if (token && !url.includes('token=')) {
        const separator = url.includes('?') ? '&' : '?';
        // 某些端点需要在 URL 中传递 token（如 WebSocket）
    }
    
    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), REQUEST_TIMEOUT);
        
        mergedOptions.signal = controller.signal;
        
        const response = await fetch(fullUrl, mergedOptions);
        clearTimeout(timeoutId);
        
        // 处理 401 未授权错误
        if (response.status === ERROR_CODES.UNAUTHORIZED) {
            // 尝试刷新 token
            const refreshed = await refreshToken();
            if (refreshed) {
                // 重新构建请求头
                mergedOptions.headers = buildHeaders(true);
                const retryResponse = await fetch(fullUrl, mergedOptions);
                return handleResponse(retryResponse);
            } else {
                // 刷新失败，清除 token 并跳转到登录页
                clearToken();
                window.location.reload();
                return { code: ERROR_CODES.UNAUTHORIZED, message: '登录已过期' };
            }
        }
        
        return handleResponse(response);
    } catch (error) {
        if (error.name === 'AbortError') {
            return { code: -1, message: '请求超时' };
        }
        console.error('Request error:', error);
        return { code: -1, message: '网络错误: ' + error.message };
    }
}

/**
 * 处理响应
 */
async function handleResponse(response) {
    try {
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            const data = await response.json();
            return data;
        } else {
            const text = await response.text();
            return { code: response.status, message: text, data: text };
        }
    } catch (error) {
        return { code: response.status, message: '解析响应失败' };
    }
}

/**
 * 刷新 Token
 */
async function refreshToken() {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
        return false;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/refresh`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ refreshToken })
        });
        
        const data = await response.json();
        
        if (data.code === ERROR_CODES.SUCCESS && data.data) {
            setToken(data.data.token);
            setRefreshToken(data.data.refreshToken);
            return true;
        }
        
        return false;
    } catch (error) {
        console.error('Refresh token error:', error);
        return false;
    }
}

// ============ 认证 API ============

/**
 * 用户登录
 */
async function login(username, password) {
    return request('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username, password })
    });
}

/**
 * 用户注册
 */
async function register(username, password, nickname, email, phone) {
    return request('/auth/register', {
        method: 'POST',
        body: JSON.stringify({ username, password, nickname, email, phone })
    });
}

/**
 * 退出登录
 */
async function logout() {
    try {
        await request('/auth/logout', { method: 'POST' });
    } catch (error) {
        console.error('Logout error:', error);
    } finally {
        clearToken();
    }
}

// ============ 用户 API ============

/**
 * 获取当前用户信息
 */
async function getCurrentUser() {
    return request('/users/me');
}

/**
 * 获取用户信息
 */
async function getUserInfo(userId) {
    return request(`/users/${userId}`);
}

/**
 * 更新用户信息
 */
async function updateUser(userId, userData) {
    return request(`/users/${userId}`, {
        method: 'PUT',
        body: JSON.stringify(userData)
    });
}

/**
 * 更新用户头像
 */
async function updateUserAvatar(userId, avatarUrl) {
    return request(`/users/${userId}/avatar`, {
        method: 'PUT',
        body: JSON.stringify({ avatarUrl })
    });
}

/**
 * 修改密码
 */
async function changePassword(oldPassword, newPassword) {
    return request('/users/password', {
        method: 'PUT',
        body: JSON.stringify({ oldPassword, newPassword })
    });
}

/**
 * 搜索用户
 */
async function searchUsers(keyword, page = 0, size = 20) {
    return request(`/users/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
}

/**
 * 获取用户在线状态
 */
async function getUserOnlineStatus(userId) {
    return request(`/users/${userId}/online`);
}

// ============ 好友 API ============

/**
 * 获取好友列表
 */
async function getFriends(page = 0, size = 100) {
    return request(`/friends?page=${page}&size=${size}`);
}

/**
 * 获取好友详情
 */
async function getFriendDetail(friendId) {
    return request(`/friends/${friendId}`);
}

/**
 * 添加好友
 */
async function addFriend(userId, remark) {
    return request('/friends', {
        method: 'POST',
        body: JSON.stringify({ userId, remark })
    });
}

/**
 * 删除好友
 */
async function deleteFriend(friendId) {
    return request(`/friends/${friendId}`, {
        method: 'DELETE'
    });
}

/**
 * 修改好友备注
 */
async function updateFriendRemark(friendId, remark) {
    return request(`/friends/${friendId}/remark`, {
        method: 'PUT',
        body: JSON.stringify({ remark })
    });
}

/**
 * 获取好友申请列表
 */
async function getFriendRequests(page = 0, size = 20) {
    return request(`/friends/requests?page=${page}&size=${size}`);
}

/**
 * 处理好友申请
 */
async function handleFriendRequest(requestId, accept) {
    return request(`/friends/requests/${requestId}`, {
        method: 'PUT',
        body: JSON.stringify({ accept })
    });
}

/**
 * 发送好友申请
 */
async function sendFriendRequest(userId, message) {
    return request('/friends/request', {
        method: 'POST',
        body: JSON.stringify({ userId, message })
    });
}

/**
 * 获取好友在线状态
 */
async function getFriendOnlineStatus(friendId) {
    return request(`/friends/${friendId}/online`);
}

// ============ 群组 API ============

/**
 * 获取我的群组列表
 */
async function getMyGroups(page = 0, size = 100) {
    return request(`/groups/my?page=${page}&size=${size}`);
}

/**
 * 获取群组详情
 */
async function getGroupDetail(groupId) {
    return request(`/groups/${groupId}`);
}

/**
 * 创建群组
 */
async function createGroup(groupName, description, avatarUrl, memberIds) {
    return request('/groups', {
        method: 'POST',
        body: JSON.stringify({ groupName, description, avatarUrl, memberIds })
    });
}

/**
 * 更新群组信息
 */
async function updateGroup(groupId, groupName, description, avatarUrl) {
    return request(`/groups/${groupId}`, {
        method: 'PUT',
        body: JSON.stringify({ groupName, description, avatarUrl })
    });
}

/**
 * 解散群组
 */
async function dissolveGroup(groupId) {
    return request(`/groups/${groupId}`, {
        method: 'DELETE'
    });
}

/**
 * 获取群成员列表
 */
async function getGroupMembers(groupId, page = 0, size = 100) {
    return request(`/groups/${groupId}/members?page=${page}&size=${size}`);
}

/**
 * 添加群成员
 */
async function addGroupMember(groupId, userIds) {
    return request(`/groups/${groupId}/members`, {
        method: 'POST',
        body: JSON.stringify({ userIds })
    });
}

/**
 * 移除群成员
 */
async function removeGroupMember(groupId, userId) {
    return request(`/groups/${groupId}/members/${userId}`, {
        method: 'DELETE'
    });
}

/**
 * 退出群组
 */
async function leaveGroup(groupId) {
    return request(`/groups/${groupId}/leave`, {
        method: 'POST'
    });
}

/**
 * 设置群成员备注
 */
async function setGroupMemberRemark(groupId, userId, remark) {
    return request(`/groups/${groupId}/members/${userId}/remark`, {
        method: 'PUT',
        body: JSON.stringify({ remark })
    });
}

/**
 * 获取群申请列表
 */
async function getGroupRequests(groupId, page = 0, size = 20) {
    return request(`/groups/${groupId}/requests?page=${page}&size=${size}`);
}

/**
 * 处理群申请
 */
async function handleGroupRequest(groupId, requestId, accept) {
    return request(`/groups/${groupId}/requests/${requestId}`, {
        method: 'PUT',
        body: JSON.stringify({ accept })
    });
}

/**
 * 搜索群组
 */
async function searchGroups(keyword, page = 0, size = 20) {
    return request(`/groups/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
}

/**
 * 申请加入群组
 */
async function applyJoinGroup(groupId, message) {
    return request(`/groups/${groupId}/apply`, {
        method: 'POST',
        body: JSON.stringify({ message })
    });
}

// ============ 消息 API ============

/**
 * 获取私聊消息历史
 */
async function getPrivateMessages(userId, page = 0, size = 50, beforeMsgId) {
    let url = `/messages/private/${userId}?page=${page}&size=${size}`;
    if (beforeMsgId) {
        url += `&beforeMsgId=${beforeMsgId}`;
    }
    return request(url);
}

/**
 * 获取群聊消息历史
 */
async function getGroupMessages(groupId, page = 0, size = 50, beforeMsgId) {
    let url = `/messages/group/${groupId}?page=${page}&size=${size}`;
    if (beforeMsgId) {
        url += `&beforeMsgId=${beforeMsgId}`;
    }
    return request(url);
}

/**
 * 发送消息
 */
async function sendMessage(toUserId, chatType, chatId, msgType, content) {
    return request('/messages', {
        method: 'POST',
        body: JSON.stringify({
            toUserId,
            chatType,
            chatId,
            msgType,
            content
        })
    });
}

/**
 * 撤回消息
 */
async function recallMessage(msgId) {
    return request(`/messages/${msgId}/recall`, {
        method: 'POST'
    });
}

/**
 * 删除消息
 */
async function deleteMessage(msgId) {
    return request(`/messages/${msgId}`, {
        method: 'DELETE'
    });
}

/**
 * 标记消息为已读
 */
async function markMessageAsRead(chatId, chatType) {
    return request('/messages/read', {
        method: 'PUT',
        body: JSON.stringify({ chatId, chatType })
    });
}

/**
 * 获取未读消息数
 */
async function getUnreadCount() {
    return request('/messages/unread/count');
}

/**
 * 获取会话列表（包含最后一条消息）
 */
async function getConversationList(page = 0, size = 50) {
    return request(`/messages/conversations?page=${page}&size=${size}`);
}

/**
 * 搜索消息
 */
async function searchMessages(keyword, chatId, chatType, page = 0, size = 20) {
    let url = `/messages/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`;
    if (chatId) {
        url += `&chatId=${chatId}&chatType=${chatType}`;
    }
    return request(url);
}

// ============ 文件上传 API ============

/**
 * 上传文件
 */
async function uploadFile(file, fileType = 'file') {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('type', fileType);
    
    const token = getToken();
    
    try {
        const response = await fetch(`${API_BASE_URL}/upload`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });
        
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Upload file error:', error);
        return { code: -1, message: '上传失败: ' + error.message };
    }
}

/**
 * 上传图片
 */
async function uploadImage(file) {
    return uploadFile(file, 'image');
}

/**
 * 上传音频
 */
async function uploadAudio(file) {
    return uploadFile(file, 'audio');
}

/**
 * 上传文件
 */
async function uploadGeneralFile(file) {
    return uploadFile(file, 'file');
}

// ============ 通知设置 API ============

/**
 * 获取通知设置
 */
async function getNotificationSettings() {
    return request('/settings/notification');
}

/**
 * 更新通知设置
 */
async function updateNotificationSettings(settings) {
    return request('/settings/notification', {
        method: 'PUT',
        body: JSON.stringify(settings)
    });
}

/**
 * 获取隐私设置
 */
async function getPrivacySettings() {
    return request('/settings/privacy');
}

/**
 * 更新隐私设置
 */
async function updatePrivacySettings(settings) {
    return request('/settings/privacy', {
        method: 'PUT',
        body: JSON.stringify(settings)
    });
}

// 导出模块
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        getToken,
        getRefreshToken,
        setToken,
        setRefreshToken,
        clearToken,
        request,
        refreshToken,
        login,
        register,
        logout,
        getCurrentUser,
        getUserInfo,
        updateUser,
        updateUserAvatar,
        changePassword,
        searchUsers,
        getUserOnlineStatus,
        getFriends,
        getFriendDetail,
        addFriend,
        deleteFriend,
        updateFriendRemark,
        getFriendRequests,
        handleFriendRequest,
        sendFriendRequest,
        getFriendOnlineStatus,
        getMyGroups,
        getGroupDetail,
        createGroup,
        updateGroup,
        dissolveGroup,
        getGroupMembers,
        addGroupMember,
        removeGroupMember,
        leaveGroup,
        setGroupMemberRemark,
        getGroupRequests,
        handleGroupRequest,
        searchGroups,
        applyJoinGroup,
        getPrivateMessages,
        getGroupMessages,
        sendMessage,
        recallMessage,
        deleteMessage,
        markMessageAsRead,
        getUnreadCount,
        getConversationList,
        searchMessages,
        uploadFile,
        uploadImage,
        uploadAudio,
        uploadGeneralFile,
        getNotificationSettings,
        updateNotificationSettings,
        getPrivacySettings,
        updatePrivacySettings,
        API_BASE_URL,
        ERROR_CODES
    };
}
