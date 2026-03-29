/**
 * 消息引用/回复模块
 * 
 * 功能支持：
 * - 消息引用和回复
 * - 引用预览气泡
 * - 回复线程展示
 * - 引用链展开/折叠
 * - 回复统计
 * - 引用高亮
 */

const API_BASE_URL = 'http://localhost:8080/api';

// 当前选中的引用消息
let selectedReplyMessage = null;

// ============================================
// 消息引用数据模型
// ============================================

class MessageReply {
    constructor(data = {}) {
        this.id = data.id || null;
        this.originalMsgId = data.originalMsgId || '';
        this.replyMsgId = data.replyMsgId || '';
        this.originalSenderId = data.originalSenderId || 0;
        this.originalSenderNickname = data.originalSenderNickname || '';
        this.originalContentPreview = data.originalContentPreview || '';
        this.originalMsgType = data.originalMsgType || 1;
        this.originalMsgTime = data.originalMsgTime ? new Date(data.originalMsgTime) : null;
        this.replyDepth = data.replyDepth || 0;
        this.replyChainId = data.replyChainId || '';
        this.chainDepth = data.chainDepth || 1;
        this.originalRecalled = data.originalRecalled || false;
        this.replyRemark = data.replyRemark || '';
        this.highlight = data.highlight || false;
        this.chatType = data.chatType || 1;
        this.chatId = data.chatId || 0;
        this.replyUserId = data.replyUserId || 0;
        this.replyUserNickname = data.replyUserNickname || '';
        this.replyContent = data.replyContent || '';
        this.replyMsgType = data.replyMsgType || 1;
        this.deleted = data.deleted || false;
        this.createTime = data.createTime ? new Date(data.createTime) : null;
        this.updateTime = data.updateTime ? new Date(data.updateTime) : null;
    }

    static fromJson(json) {
        return new MessageReply(json);
    }

    toJson() {
        return {
            id: this.id,
            originalMsgId: this.originalMsgId,
            replyMsgId: this.replyMsgId,
            originalSenderId: this.originalSenderId,
            originalSenderNickname: this.originalSenderNickname,
            originalContentPreview: this.originalContentPreview,
            originalMsgType: this.originalMsgType,
            originalMsgTime: this.originalMsgTime?.toISOString(),
            replyDepth: this.replyDepth,
            replyChainId: this.replyChainId,
            chainDepth: this.chainDepth,
            originalRecalled: this.originalRecalled,
            replyRemark: this.replyRemark,
            highlight: this.highlight,
            chatType: this.chatType,
            chatId: this.chatId,
            replyUserId: this.replyUserId,
            replyUserNickname: this.replyUserNickname,
            replyContent: this.replyContent,
            replyMsgType: this.replyMsgType,
            deleted: this.deleted,
            createTime: this.createTime?.toISOString(),
            updateTime: this.updateTime?.toISOString(),
        };
    }

    /** 获取原消息类型的显示文本 */
    getOriginalMsgTypeText() {
        const types = { 1: '文本', 2: '图片', 3: '文件', 4: '语音', 5: '视频', 6: '表情包', 7: '位置' };
        return types[this.originalMsgType] || '文本';
    }

    /** 获取原消息类型的图标 */
    getOriginalMsgTypeIcon() {
        const icons = { 1: '💬', 2: '🖼️', 3: '📎', 4: '🎤', 5: '📹', 6: '😊', 7: '📍' };
        return icons[this.originalMsgType] || '💬';
    }

    /** 获取回复消息类型的图标 */
    getReplyMsgTypeIcon() {
        const icons = { 1: '💬', 2: '🖼️', 3: '📎', 4: '🎤', 5: '📹', 6: '😊', 7: '📍' };
        return icons[this.replyMsgType] || '💬';
    }

    /** 是否是私聊 */
    isPrivateChat() { return this.chatType === 1; }
    /** 是否是群聊 */
    isGroupChat() { return this.chatType === 2; }
}

// ============================================
// API 调用
// ============================================

/**
 * 获取认证头
 */
function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {})
    };
}

/**
 * 创建带引用的回复
 */
async function createReply(originalMsgId, replyContent, replyMsgType = 1, chatType = 1, chatId = 0, replyRemark = '') {
    try {
        const response = await fetch(`${API_BASE_URL}/message/reply/create`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({
                originalMsgId,
                replyContent,
                replyMsgType,
                chatType,
                chatId,
                replyRemark,
            })
        });
        const data = await response.json();
        if (data.code === 200) {
            return MessageReply.fromJson(data.data);
        }
        throw new Error(data.message || '创建回复失败');
    } catch (e) {
        console.error('创建回复错误:', e);
        throw e;
    }
}

/**
 * 获取消息的引用详情
 */
async function getReplyByOriginalMsgId(originalMsgId, replyUserId) {
    try {
        const response = await fetch(`${API_BASE_URL}/message/reply?originalMsgId=${originalMsgId}&replyUserId=${replyUserId}`, {
            headers: getAuthHeaders()
        });
        const data = await response.json();
        if (data.code === 200 && data.data) {
            return MessageReply.fromJson(data.data);
        }
        return null;
    } catch (e) {
        console.error('获取引用详情错误:', e);
        return null;
    }
}

/**
 * 获取消息的直接回复列表
 */
async function getDirectReplies(originalMsgId) {
    try {
        const response = await fetch(`${API_BASE_URL}/message/reply/direct/${originalMsgId}`, {
            headers: getAuthHeaders()
        });
        const data = await response.json();
        if (data.code === 200 && data.data) {
            return data.data.map(item => MessageReply.fromJson(item));
        }
        return [];
    } catch (e) {
        console.error('获取直接回复错误:', e);
        return [];
    }
}

/**
 * 获取消息的完整引用链
 */
async function getReplyChain(originalMsgId) {
    try {
        const response = await fetch(`${API_BASE_URL}/message/reply/chain/${originalMsgId}`, {
            headers: getAuthHeaders()
        });
        const data = await response.json();
        if (data.code === 200 && data.data) {
            return data.data.map(item => MessageReply.fromJson(item));
        }
        return [];
    } catch (e) {
        console.error('获取引用链错误:', e);
        return [];
    }
}

/**
 * 获取回复线程（包含原消息和所有回复）
 */
async function getReplyThread(originalMsgId) {
    try {
        const response = await fetch(`${API_BASE_URL}/message/reply/thread/${originalMsgId}`, {
            headers: getAuthHeaders()
        });
        const data = await response.json();
        if (data.code === 200 && data.data) {
            return {
                originalMessage: data.data.originalMessage,
                replies: (data.data.replies || []).map(item => MessageReply.fromJson(item))
            };
        }
        return null;
    } catch (e) {
        console.error('获取回复线程错误:', e);
        return null;
    }
}

/**
 * 获取回复统计
 */
async function getReplyStats(originalMsgId) {
    try {
        const response = await fetch(`${API_BASE_URL}/message/reply/stats/${originalMsgId}`, {
            headers: getAuthHeaders()
        });
        const data = await response.json();
        if (data.code === 200 && data.data) {
            return data.data;
        }
        return null;
    } catch (e) {
        console.error('获取回复统计错误:', e);
        return null;
    }
}

/**
 * 删除回复（软删除）
 */
async function deleteReply(replyId) {
    try {
        const response = await fetch(`${API_BASE_URL}/message/reply/${replyId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        return response.ok;
    } catch (e) {
        console.error('删除回复错误:', e);
        return false;
    }
}

/**
 * 高亮引用
 */
async function toggleHighlight(replyId, highlight) {
    try {
        const response = await fetch(`${API_BASE_URL}/message/reply/highlight/${replyId}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify({ highlight })
        });
        return response.ok;
    } catch (e) {
        console.error('高亮引用错误:', e);
        return false;
    }
}

/**
 * 获取会话中所有引用
 */
async function getRepliesInChat(chatType, chatId) {
    try {
        const response = await fetch(`${API_BASE_URL}/message/reply/chat?chatType=${chatType}&chatId=${chatId}`, {
            headers: getAuthHeaders()
        });
        const data = await response.json();
        if (data.code === 200 && data.data) {
            return data.data.map(item => MessageReply.fromJson(item));
        }
        return [];
    } catch (e) {
        console.error('获取会话引用错误:', e);
        return [];
    }
}

/**
 * 检查消息是否被引用
 */
async function isMessageReplied(msgId) {
    try {
        const response = await fetch(`${API_BASE_URL}/message/reply/exists/${msgId}`, {
            headers: getAuthHeaders()
        });
        const data = await response.json();
        return data.data === true;
    } catch (e) {
        return false;
    }
}

// ============================================
// UI 渲染
// ============================================

/**
 * 渲染消息的引用预览气泡
 * @param {MessageReply} reply - 引用数据
 * @returns {string} HTML字符串
 */
function renderReplyBubble(reply) {
    if (!reply) return '';
    const icon = reply.getOriginalMsgTypeIcon();
    const senderName = reply.originalSenderNickname || '未知用户';
    const content = reply.originalContentPreview || '';
    const recalledText = reply.originalRecalled ? ' [该消息已撤回]' : '';
    const highlightClass = reply.highlight ? 'reply-bubble-highlight' : '';

    return `
        <div class="reply-bubble ${highlightClass}" data-original-msg-id="${reply.originalMsgId}">
            <div class="reply-bubble-header">
                <span class="reply-icon">${icon}</span>
                <span class="reply-sender">${senderName}${recalledText}</span>
            </div>
            <div class="reply-bubble-content">${content}</div>
            ${reply.replyRemark ? `<div class="reply-remark">📌 ${reply.replyRemark}</div>` : ''}
        </div>
    `;
}

/**
 * 渲染回复线程面板
 * @param {MessageReply[]} replies - 回复列表
 * @param {Object} originalMessage - 原消息
 */
function renderReplyThread(replies, originalMessage) {
    if (!replies || replies.length === 0) return '';
    
    let html = '<div class="reply-thread">';
    html += '<div class="reply-thread-header">';
    html += `<span class="reply-thread-count">${replies.length} 条回复</span>`;
    html += '<button class="reply-thread-toggle" onclick="toggleReplyThread(this)">收起</button>';
    html += '</div>';
    html += '<div class="reply-thread-content">';
    
    replies.forEach(reply => {
        const isOwn = reply.replyUserId === parseInt(currentUser?.id || 0);
        const time = reply.createTime ? new Date(reply.createTime).toLocaleString('zh-CN', { 
            month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' 
        }) : '';
        
        html += `
            <div class="reply-thread-item ${reply.highlight ? 'highlight' : ''}">
                <div class="reply-thread-avatar">
                    ${(reply.replyUserNickname || 'U').charAt(0).toUpperCase()}
                </div>
                <div class="reply-thread-body">
                    <div class="reply-thread-meta">
                        <span class="reply-thread-nickname">${reply.replyUserNickname || '未知'}</span>
                        <span class="reply-thread-time">${time}</span>
                    </div>
                    <div class="reply-thread-text">${reply.replyContent || '(仅引用)'}</div>
                </div>
                ${isOwn ? `<button class="reply-thread-delete" onclick="handleDeleteReply(${reply.id})" title="删除">🗑️</button>` : ''}
            </div>
        `;
    });
    
    html += '</div></div>';
    return html;
}

/**
 * 展开/收起回复线程
 */
function toggleReplyThread(btn) {
    const thread = btn.closest('.reply-thread');
    const content = thread.querySelector('.reply-thread-content');
    const isExpanded = !content.classList.contains('collapsed');
    
    if (isExpanded) {
        content.classList.add('collapsed');
        btn.textContent = '展开';
    } else {
        content.classList.remove('collapsed');
        btn.textContent = '收起';
    }
}

/**
 * 处理删除回复
 */
async function handleDeleteReply(replyId) {
    if (!confirm('确定删除此回复？')) return;
    const success = await deleteReply(replyId);
    if (success) {
        showNotification('回复已删除', 'success');
        // 重新加载消息
        if (window.loadMessages && currentChat) {
            loadMessages(currentChat);
        }
    } else {
        showNotification('删除失败', 'error');
    }
}

// ============================================
// 引用选择模式
// ============================================

/**
 * 进入引用模式：用户点击消息右键或引用按钮后调用
 * @param {Object} message - 被引用的消息对象
 */
function enterReplyMode(message) {
    selectedReplyMessage = message;
    
    const input = document.getElementById('message-input');
    if (!input) return;
    
    // 在输入框上方显示引用预览
    let replyPreview = document.getElementById('reply-preview');
    if (!replyPreview) {
        replyPreview = document.createElement('div');
        replyPreview.id = 'reply-preview';
        replyPreview.className = 'reply-preview';
        input.parentElement.insertBefore(replyPreview, input);
    }
    
    const senderName = message.fromNickname || message.fromUsername || '对方';
    const content = message.content ? (message.content.length > 50 ? message.content.substring(0, 50) + '...' : message.content) : '[媒体消息]';
    
    replyPreview.innerHTML = `
        <div class="reply-preview-bubble">
            <span class="reply-preview-sender">${senderName}:</span>
            <span class="reply-preview-content">${content}</span>
        </div>
        <button class="reply-preview-cancel" onclick="cancelReplyMode()">×</button>
    `;
    replyPreview.classList.remove('hidden');
    
    // 修改输入框提示
    input.placeholder = '回复 ' + senderName + '...';
    input.focus();
    
    console.log('进入引用模式，引用消息:', message.msgId);
}

/**
 * 取消引用模式
 */
function cancelReplyMode() {
    selectedReplyMessage = null;
    
    const replyPreview = document.getElementById('reply-preview');
    if (replyPreview) {
        replyPreview.classList.add('hidden');
        replyPreview.innerHTML = '';
    }
    
    const input = document.getElementById('message-input');
    if (input) {
        input.placeholder = '输入消息...';
    }
}

/**
 * 获取当前选中的引用消息
 */
function getSelectedReplyMessage() {
    return selectedReplyMessage;
}

// ============================================
// 消息发送集成
// ============================================

/**
 * 发送带引用的消息（覆盖原sendMessage逻辑）
 * 在原sendMessage中检查是否有选中的引用，有则调用createReply
 */
async function sendReplyMessage() {
    if (!currentChat) {
        showNotification('请选择会话', 'warning');
        return;
    }
    
    const input = document.getElementById('message-input');
    const content = input.value.trim();
    
    if (!content) return;
    
    try {
        // 如果有选中的引用消息，先创建引用记录
        if (selectedReplyMessage) {
            await createReply(
                selectedReplyMessage.msgId,
                content,
                1, // 文本类型
                currentChat.type === 'private' ? 1 : 2,
                currentChat.id
            );
            cancelReplyMode();
        }
        
        // 然后发送消息
        let url, body;
        if (currentChat.type === 'private') {
            url = `${API_BASE_URL}/messages`;
            body = {
                toUserId: currentChat.id,
                chatType: 1,
                chatId: currentChat.id,
                msgType: 1,
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
            headers: getAuthHeaders(),
            body: JSON.stringify(body)
        });
        
        const data = await response.json();
        
        if (data.code === 200) {
            input.value = '';
            if (loadMessages && currentChat) {
                await loadMessages(currentChat);
            }
        } else {
            showNotification(data.message || '发送失败', 'error');
        }
    } catch (error) {
        console.error('发送引用回复错误:', error);
        showNotification('发送失败', 'error');
    }
}

// 导出给全局使用
window.MessageReply = MessageReply;
window.createReply = createReply;
window.getDirectReplies = getDirectReplies;
window.getReplyChain = getReplyChain;
window.getReplyThread = getReplyThread;
window.getReplyStats = getReplyStats;
window.deleteReply = deleteReply;
window.toggleHighlight = toggleHighlight;
window.getRepliesInChat = getRepliesInChat;
window.isMessageReplied = isMessageReplied;
window.renderReplyBubble = renderReplyBubble;
window.renderReplyThread = renderReplyThread;
window.toggleReplyThread = toggleReplyThread;
window.handleDeleteReply = handleDeleteReply;
window.enterReplyMode = enterReplyMode;
window.cancelReplyMode = cancelReplyMode;
window.getSelectedReplyMessage = getSelectedReplyMessage;
window.sendReplyMessage = sendReplyMessage;
