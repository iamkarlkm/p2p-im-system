// IM Desktop - WebSocket Service Module
// 处理 WebSocket 连接和消息收发

const WS_URL = 'ws://localhost:9000/ws';

// WebSocket 状态
const WS_STATE = {
    CONNECTING: 0,
    OPEN: 1,
    CLOSING: 2,
    CLOSED: 3
};

// 消息类型命令
const WS_CMD = {
    // 消息
    PRIVATE_MSG: 1001,      // 私聊消息
    GROUP_MSG: 1002,        // 群组消息
    MSG_ACK: 1003,          // 消息ACK
    MSG_RECALL: 1004,       // 消息撤回
    
    // 用户状态
    USER_ONLINE: 2001,      // 用户上线
    USER_OFFLINE: 2002,    // 用户下线
    USER_STATUS_CHANGE: 2003, // 用户状态变更
    
    // 好友
    FRIEND_REQUEST: 3001,  // 好友请求
    FRIEND_REQUEST_ACK: 3002, // 好友请求响应
    FRIEND_ADDED: 3003,    // 好友添加成功
    FRIEND_REMOVED: 3004,  // 好友被删除
    
    // 群组
    GROUP_REQUEST: 4001,    // 入群申请
    GROUP_REQUEST_ACK: 4002, // 入群申请响应
    GROUP_INVITE: 4003,     // 群邀请
    GROUP_INVITE_ACK: 4004, // 群邀请响应
    MEMBER_JOINED: 4005,   // 成员加入群
    MEMBER_LEFT: 4006,     // 成员离开群
    MEMBER_REMOVED: 4007,  // 成员被移除
    GROUP_DISSOLVED: 4008, // 群被解散
    
    // 心跳
    PING: 3001,
    PONG: 3002
};

// 重连配置
const RECONNECT_CONFIG = {
    enabled: true,
    maxReconnectAttempts: 10,
    initialDelay: 1000,
    maxDelay: 30000,
    delayMultiplier: 1.5
};

class WebSocketService {
    constructor() {
        this.ws = null;
        this.token = null;
        this.reconnectAttempts = 0;
        this.reconnectTimer = null;
        this.pingTimer = null;
        this.messageHandlers = new Map();
        this.connectionState = 'disconnected';
        this.listeners = {
            onOpen: [],
            onClose: [],
            onError: [],
            onMessage: [],
            onStateChange: []
        };
    }
    
    /**
     * 连接到 WebSocket 服务器
     */
    connect(token) {
        if (this.ws && this.ws.readyState === WS_STATE.OPEN) {
            console.log('WebSocket already connected');
            return;
        }
        
        this.token = token;
        this.connectionState = 'connecting';
        this.notifyStateChange();
        
        try {
            const wsUrl = `${WS_URL}?token=${token}`;
            this.ws = new WebSocket(wsUrl);
            
            this.ws.onopen = (event) => this.handleOpen(event);
            this.ws.onclose = (event) => this.handleClose(event);
            this.ws.onerror = (event) => this.handleError(event);
            this.ws.onmessage = (event) => this.handleMessage(event);
        } catch (error) {
            console.error('WebSocket connection error:', error);
            this.handleError(error);
        }
    }
    
    /**
     * 断开连接
     */
    disconnect() {
        this.stopReconnect();
        this.stopPing();
        
        if (this.ws) {
            this.ws.onopen = null;
            this.ws.onclose = null;
            this.ws.onerror = null;
            this.ws.onmessage = null;
            
            if (this.ws.readyState === WS_STATE.OPEN || this.ws.readyState === WS_STATE.CONNECTING) {
                this.ws.close();
            }
            
            this.ws = null;
        }
        
        this.connectionState = 'disconnected';
        this.notifyStateChange();
    }
    
    /**
     * 处理连接打开
     */
    handleOpen(event) {
        console.log('WebSocket connected');
        this.reconnectAttempts = 0;
        this.connectionState = 'connected';
        this.notifyStateChange();
        
        // 启动心跳
        this.startPing();
        
        // 通知监听器
        this.listeners.onOpen.forEach(handler => handler(event));
        
        // 发送上线通知
        this.sendUserStatus(1);
    }
    
    /**
     * 处理连接关闭
     */
    handleClose(event) {
        console.log('WebSocket closed', event.code, event.reason);
        this.stopPing();
        this.connectionState = 'disconnected';
        this.notifyStateChange();
        
        // 通知监听器
        this.listeners.onClose.forEach(handler => handler(event));
        
        // 尝试重连
        if (RECONNECT_CONFIG.enabled) {
            this.scheduleReconnect();
        }
    }
    
    /**
     * 处理错误
     */
    handleError(error) {
        console.error('WebSocket error:', error);
        this.connectionState = 'error';
        this.notifyStateChange();
        
        // 通知监听器
        this.listeners.onError.forEach(handler => handler(error));
    }
    
    /**
     * 处理消息
     */
    handleMessage(event) {
        try {
            const data = JSON.parse(event.data);
            const cmd = data.cmd;
            
            console.log('WebSocket message:', cmd, data);
            
            // 优先调用特定命令的处理器
            if (this.messageHandlers.has(cmd)) {
                this.messageHandlers.get(cmd).forEach(handler => {
                    handler(data);
                });
            }
            
            // 然后调用通用消息监听器
            this.listeners.onMessage.forEach(handler => handler(data));
            
            // 根据命令类型处理
            switch (cmd) {
                case WS_CMD.PRIVATE_MSG:
                    this.handlePrivateMessage(data);
                    break;
                case WS_CMD.GROUP_MSG:
                    this.handleGroupMessage(data);
                    break;
                case WS_CMD.MSG_ACK:
                    this.handleMessageAck(data);
                    break;
                case WS_CMD.MSG_RECALL:
                    this.handleMessageRecall(data);
                    break;
                case WS_CMD.USER_ONLINE:
                    this.handleUserOnline(data);
                    break;
                case WS_CMD.USER_OFFLINE:
                    this.handleUserOffline(data);
                    break;
                case WS_CMD.USER_STATUS_CHANGE:
                    this.handleUserStatusChange(data);
                    break;
                case WS_CMD.FRIEND_REQUEST:
                    this.handleFriendRequest(data);
                    break;
                case WS_CMD.FRIEND_ADDED:
                    this.handleFriendAdded(data);
                    break;
                case WS_CMD.FRIEND_REMOVED:
                    this.handleFriendRemoved(data);
                    break;
                case WS_CMD.GROUP_INVITE:
                    this.handleGroupInvite(data);
                    break;
                case WS_CMD.MEMBER_JOINED:
                    this.handleMemberJoined(data);
                    break;
                case WS_CMD.MEMBER_LEFT:
                    this.handleMemberLeft(data);
                    break;
                case WS_CMD.MEMBER_REMOVED:
                    this.handleMemberRemoved(data);
                    break;
                case WS_CMD.GROUP_DISSOLVED:
                    this.handleGroupDissolved(data);
                    break;
                case WS_CMD.PONG:
                    // 心跳响应
                    break;
            }
        } catch (error) {
            console.error('WebSocket message parse error:', error);
        }
    }
    
    /**
     * 发送消息
     */
    send(data) {
        if (!this.ws || this.ws.readyState !== WS_STATE.OPEN) {
            console.error('WebSocket not connected');
            return false;
        }
        
        try {
            this.ws.send(JSON.stringify(data));
            return true;
        } catch (error) {
            console.error('WebSocket send error:', error);
            return false;
        }
    }
    
    /**
     * 发送私聊消息
     */
    sendPrivateMessage(toUserId, content, msgType = 1) {
        const message = {
            cmd: WS_CMD.PRIVATE_MSG,
            seq: this.generateSeq(),
            timestamp: Date.now(),
            data: {
                toUserId: toUserId,
                msgType: msgType,
                content: content
            }
        };
        return this.send(message);
    }
    
    /**
     * 发送群组消息
     */
    sendGroupMessage(groupId, content, msgType = 1) {
        const message = {
            cmd: WS_CMD.GROUP_MSG,
            seq: this.generateSeq(),
            timestamp: Date.now(),
            data: {
                groupId: groupId,
                msgType: msgType,
                content: content
            }
        };
        return this.send(message);
    }
    
    /**
     * 发送消息 ACK
     */
    sendMessageAck(seq, msgId) {
        const message = {
            cmd: WS_CMD.MSG_ACK,
            seq: this.generateSeq(),
            timestamp: Date.now(),
            data: {
                seq: seq,
                msgId: msgId
            }
        };
        return this.send(message);
    }
    
    /**
     * 撤回消息
     */
    sendMessageRecall(msgId) {
        const message = {
            cmd: WS_CMD.MSG_RECALL,
            seq: this.generateSeq(),
            timestamp: Date.now(),
            data: {
                msgId: msgId
            }
        };
        return this.send(message);
    }
    
    /**
     * 发送用户状态
     */
    sendUserStatus(status) {
        const message = {
            cmd: WS_CMD.USER_STATUS_CHANGE,
            seq: this.generateSeq(),
            timestamp: Date.now(),
            data: {
                status: status
            }
        };
        return this.send(message);
    }
    
    /**
     * 发送心跳
     */
    sendPing() {
        const message = {
            cmd: WS_CMD.PING,
            timestamp: Date.now()
        };
        return this.send(message);
    }
    
    /**
     * 注册消息处理器
     */
    on(cmd, handler) {
        if (!this.messageHandlers.has(cmd)) {
            this.messageHandlers.set(cmd, []);
        }
        this.messageHandlers.get(cmd).push(handler);
    }
    
    /**
     * 移除消息处理器
     */
    off(cmd, handler) {
        if (this.messageHandlers.has(cmd)) {
            const handlers = this.messageHandlers.get(cmd);
            const index = handlers.indexOf(handler);
            if (index > -1) {
                handlers.splice(index, 1);
            }
        }
    }
    
    /**
     * 添加监听器
     */
    addEventListener(event, handler) {
        if (this.listeners.hasOwnProperty(event)) {
            this.listeners[event].push(handler);
        }
    }
    
    /**
     * 移除监听器
     */
    removeEventListener(event, handler) {
        if (this.listeners.hasOwnProperty(event)) {
            const handlers = this.listeners[event];
            const index = handlers.indexOf(handler);
            if (index > -1) {
                handlers.splice(index, 1);
            }
        }
    }
    
    /**
     * 通知状态变化
     */
    notifyStateChange() {
        this.listeners.onStateChange.forEach(handler => handler(this.connectionState));
    }
    
    /**
     * 启动心跳
     */
    startPing() {
        this.stopPing();
        this.pingTimer = setInterval(() => {
            this.sendPing();
        }, 30000); // 30秒心跳一次
    }
    
    /**
     * 停止心跳
     */
    stopPing() {
        if (this.pingTimer) {
            clearInterval(this.pingTimer);
            this.pingTimer = null;
        }
    }
    
    /**
     * 计划重连
     */
    scheduleReconnect() {
        if (this.reconnectAttempts >= RECONNECT_CONFIG.maxReconnectAttempts) {
            console.log('Max reconnect attempts reached');
            return;
        }
        
        if (this.reconnectTimer) {
            return;
        }
        
        const delay = Math.min(
            RECONNECT_CONFIG.initialDelay * Math.pow(RECONNECT_CONFIG.delayMultiplier, this.reconnectAttempts),
            RECONNECT_CONFIG.maxDelay
        );
        
        console.log(`Scheduling reconnect in ${delay}ms (attempt ${this.reconnectAttempts + 1})`);
        
        this.reconnectTimer = setTimeout(() => {
            this.reconnectTimer = null;
            this.reconnectAttempts++;
            this.connect(this.token);
        }, delay);
    }
    
    /**
     * 停止重连
     */
    stopReconnect() {
        if (this.reconnectTimer) {
            clearTimeout(this.reconnectTimer);
            this.reconnectTimer = null;
        }
        this.reconnectAttempts = 0;
    }
    
    /**
     * 生成序列号
     */
    generateSeq() {
        return `${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
    }
    
    /**
     * 获取连接状态
     */
    getState() {
        return this.connectionState;
    }
    
    /**
     * 是否已连接
     */
    isConnected() {
        return this.ws && this.ws.readyState === WS_STATE.OPEN;
    }
    
    // ============ 消息处理方法 ============
    
    handlePrivateMessage(data) {
        // 处理私聊消息
        console.log('Private message:', data);
    }
    
    handleGroupMessage(data) {
        // 处理群组消息
        console.log('Group message:', data);
    }
    
    handleMessageAck(data) {
        // 处理消息 ACK
        console.log('Message ack:', data);
    }
    
    handleMessageRecall(data) {
        // 处理消息撤回
        console.log('Message recall:', data);
    }
    
    handleUserOnline(data) {
        // 处理用户上线
        console.log('User online:', data);
    }
    
    handleUserOffline(data) {
        // 处理用户离线
        console.log('User offline:', data);
    }
    
    handleUserStatusChange(data) {
        // 处理用户状态变更
        console.log('User status change:', data);
    }
    
    handleFriendRequest(data) {
        // 处理好友请求
        console.log('Friend request:', data);
    }
    
    handleFriendAdded(data) {
        // 处理好友添加成功
        console.log('Friend added:', data);
    }
    
    handleFriendRemoved(data) {
        // 处理好友被删除
        console.log('Friend removed:', data);
    }
    
    handleGroupInvite(data) {
        // 处理群邀请
        console.log('Group invite:', data);
    }
    
    handleMemberJoined(data) {
        // 处理成员加入
        console.log('Member joined:', data);
    }
    
    handleMemberLeft(data) {
        // 处理成员离开
        console.log('Member left:', data);
    }
    
    handleMemberRemoved(data) {
        // 处理成员被移除
        console.log('Member removed:', data);
    }
    
    handleGroupDissolved(data) {
        // 处理群解散
        console.log('Group dissolved:', data);
    }
}

// 创建全局实例
const websocketService = new WebSocketService();

// 导出模块
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        WebSocketService,
        websocketService,
        WS_STATE,
        WS_CMD,
        RECONNECT_CONFIG
    };
}
