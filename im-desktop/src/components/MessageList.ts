import { EventEmitter } from 'events';
import { Message, MessageStatus, MessageType } from '../types/message';
import { User } from '../types/user';

/**
 * MessageList组件 - 消息列表渲染与管理
 * 功能#10: 桌面端聊天界面 - 核心组件
 */
export class MessageList extends EventEmitter {
  private container: HTMLElement;
  private messages: Message[] = [];
  private currentUser: User | null = null;
  private scrollContainer: HTMLElement | null = null;
  private isLoading: boolean = false;
  private hasMore: boolean = true;
  private readonly BATCH_SIZE: number = 20;

  constructor(containerId: string) {
    super();
    const element = document.getElementById(containerId);
    if (!element) {
      throw new Error(`Container element with id '${containerId}' not found`);
    }
    this.container = element;
    this.init();
  }

  /**
   * 初始化组件
   */
  private init(): void {
    this.renderStructure();
    this.bindEvents();
  }

  /**
   * 渲染组件结构
   */
  private renderStructure(): void {
    this.container.innerHTML = `
      <div class="message-list-container">
        <div class="message-list-header">
          <span class="message-count">0 条消息</span>
          <button class="scroll-to-bottom" title="滚动到底部">
            <svg viewBox="0 0 24 24"><path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z"/></svg>
          </button>
        </div>
        <div class="message-list-scroll" ref="scrollContainer">
          <div class="loading-indicator hidden">
            <span class="spinner"></span>
            <span>加载更多...</span>
          </div>
          <div class="messages-container"></div>
          <div class="typing-indicator hidden">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
        </div>
      </div>
    `;

    this.scrollContainer = this.container.querySelector('.message-list-scroll');
  }

  /**
   * 绑定事件
   */
  private bindEvents(): void {
    if (!this.scrollContainer) return;

    // 滚动加载更多
    this.scrollContainer.addEventListener('scroll', () => {
      this.handleScroll();
    });

    // 滚动到底部按钮
    const scrollBtn = this.container.querySelector('.scroll-to-bottom');
    scrollBtn?.addEventListener('click', () => {
      this.scrollToBottom();
    });

    // 消息操作事件委托
    const messagesContainer = this.container.querySelector('.messages-container');
    messagesContainer?.addEventListener('click', (e) => {
      this.handleMessageAction(e as MouseEvent);
    });
  }

  /**
   * 设置当前用户
   */
  public setCurrentUser(user: User): void {
    this.currentUser = user;
  }

  /**
   * 加载消息列表
   */
  public async loadMessages(messages: Message[]): Promise<void> {
    this.messages = messages;
    this.renderMessages();
    this.updateMessageCount();
  }

  /**
   * 追加消息（新消息）
   */
  public appendMessage(message: Message): void {
    this.messages.push(message);
    const html = this.renderMessageItem(message);
    const container = this.container.querySelector('.messages-container');
    if (container) {
      container.insertAdjacentHTML('beforeend', html);
      this.scrollToBottom();
    }
    this.updateMessageCount();
  }

  /**
   * 前置消息（历史消息）
   */
  public prependMessages(messages: Message[]): void {
    this.messages.unshift(...messages);
    const html = messages.map(m => this.renderMessageItem(m)).join('');
    const container = this.container.querySelector('.messages-container');
    if (container) {
      container.insertAdjacentHTML('afterbegin', html);
    }
    this.updateMessageCount();
  }

  /**
   * 更新消息状态
   */
  public updateMessageStatus(messageId: string, status: MessageStatus): void {
    const messageEl = this.container.querySelector(`[data-message-id="${messageId}"]`);
    if (messageEl) {
      const statusEl = messageEl.querySelector('.message-status');
      if (statusEl) {
        statusEl.className = `message-status ${status}`;
        statusEl.innerHTML = this.getStatusIcon(status);
      }
    }
  }

  /**
   * 渲染消息列表
   */
  private renderMessages(): void {
    const container = this.container.querySelector('.messages-container');
    if (!container) return;

    const html = this.messages.map(m => this.renderMessageItem(m)).join('');
    container.innerHTML = html;
  }

  /**
   * 渲染单条消息
   */
  private renderMessageItem(message: Message): string {
    const isSelf = this.currentUser?.id === message.senderId;
    const time = this.formatTime(message.timestamp);
    const statusIcon = this.getStatusIcon(message.status);

    return `
      <div class="message-item ${isSelf ? 'self' : 'other'}" data-message-id="${message.id}">
        <div class="message-avatar">
          <img src="${message.senderAvatar || '/assets/default-avatar.png'}" alt="${message.senderName}">
        </div>
        <div class="message-content-wrapper">
          <div class="message-header">
            <span class="sender-name">${message.senderName}</span>
            <span class="message-time">${time}</span>
          </div>
          <div class="message-content ${message.type}">
            ${this.renderMessageContent(message)}
          </div>
          <div class="message-actions">
            <button class="action-btn reply" title="回复">
              <svg viewBox="0 0 24 24"><path d="M10 9V5l-7 7 7 7v-4.1c5 0 8.5 1.6 11 5.1-1-5-4-10-11-11z"/></svg>
            </button>
            <button class="action-btn forward" title="转发">
              <svg viewBox="0 0 24 24"><path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/></svg>
            </button>
            <button class="action-btn delete" title="删除">
              <svg viewBox="0 0 24 24"><path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/></svg>
            </button>
          </div>
          ${isSelf ? `<div class="message-status ${message.status}">${statusIcon}</div>` : ''}
        </div>
      </div>
    `;
  }

  /**
   * 根据消息类型渲染内容
   */
  private renderMessageContent(message: Message): string {
    switch (message.type) {
      case MessageType.TEXT:
        return `<div class="text-content">${this.escapeHtml(message.content)}</div>`;

      case MessageType.IMAGE:
        return `
          <div class="image-content">
            <img src="${message.content}" loading="lazy" onclick="window.previewImage('${message.content}')">
          </div>
        `;

      case MessageType.FILE:
        const fileInfo = JSON.parse(message.content || '{}');
        return `
          <div class="file-content" onclick="window.downloadFile('${fileInfo.url}')">
            <div class="file-icon">
              <svg viewBox="0 0 24 24"><path d="M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6z"/></svg>
            </div>
            <div class="file-info">
              <div class="file-name">${fileInfo.name}</div>
              <div class="file-size">${this.formatFileSize(fileInfo.size)}</div>
            </div>
          </div>
        `;

      case MessageType.VOICE:
        const voiceInfo = JSON.parse(message.content || '{}');
        return `
          <div class="voice-content" onclick="this.querySelector('audio').play()">
            <div class="voice-wave"></div>
            <span class="voice-duration">${voiceInfo.duration}"</span>
            <audio src="${voiceInfo.url}" preload="none"></audio>
          </div>
        `;

      default:
        return `<div class="text-content">${this.escapeHtml(message.content)}</div>`;
    }
  }

  /**
   * 获取状态图标
   */
  private getStatusIcon(status: MessageStatus): string {
    switch (status) {
      case MessageStatus.SENDING:
        return '<span class="spinner-small"></span>';
      case MessageStatus.SENT:
        return '<svg viewBox="0 0 24 24"><path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg>';
      case MessageStatus.DELIVERED:
        return '<svg viewBox="0 0 24 24"><path d="M18 7l-1.41-1.41-6.34 6.34 1.41 1.41L18 7zm4.24-1.41L11.66 16.17 7.48 12l-1.41 1.41L11.66 19l12-12-1.42-1.41zM.41 13.41L6 19l1.41-1.41L1.83 12 .41 13.41z"/></svg>';
      case MessageStatus.READ:
        return '<svg viewBox="0 0 24 24" class="read"><path d="M18 7l-1.41-1.41-6.34 6.34 1.41 1.41L18 7zm4.24-1.41L11.66 16.17 7.48 12l-1.41 1.41L11.66 19l12-12-1.42-1.41zM.41 13.41L6 19l1.41-1.41L1.83 12 .41 13.41z"/></svg>';
      case MessageStatus.FAILED:
        return '<svg viewBox="0 0 24 24" class="error"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/></svg>';
      default:
        return '';
    }
  }

  /**
   * 格式化时间
   */
  private formatTime(timestamp: number): string {
    const date = new Date(timestamp);
    const now = new Date();
    const isToday = date.toDateString() === now.toDateString();

    if (isToday) {
      return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
    }
    return date.toLocaleString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
  }

  /**
   * 格式化文件大小
   */
  private formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  /**
   * HTML转义
   */
  private escapeHtml(text: string): string {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  /**
   * 处理滚动事件
   */
  private handleScroll(): void {
    if (!this.scrollContainer || this.isLoading || !this.hasMore) return;

    const { scrollTop } = this.scrollContainer;
    if (scrollTop < 50) {
      this.emit('loadMore');
    }

    // 显示/隐藏滚动到底部按钮
    const scrollBtn = this.container.querySelector('.scroll-to-bottom');
    const isAtBottom = this.isScrolledToBottom();
    scrollBtn?.classList.toggle('visible', !isAtBottom);
  }

  /**
   * 处理消息操作
   */
  private handleMessageAction(e: MouseEvent): void {
    const target = e.target as HTMLElement;
    const actionBtn = target.closest('.action-btn');
    if (!actionBtn) return;

    const messageEl = actionBtn.closest('.message-item');
    const messageId = messageEl?.getAttribute('data-message-id');
    if (!messageId) return;

    if (actionBtn.classList.contains('reply')) {
      this.emit('reply', messageId);
    } else if (actionBtn.classList.contains('forward')) {
      this.emit('forward', messageId);
    } else if (actionBtn.classList.contains('delete')) {
      this.emit('delete', messageId);
    }
  }

  /**
   * 滚动到底部
   */
  public scrollToBottom(): void {
    if (!this.scrollContainer) return;
    this.scrollContainer.scrollTop = this.scrollContainer.scrollHeight;
  }

  /**
   * 判断是否滚动到底部
   */
  private isScrolledToBottom(): boolean {
    if (!this.scrollContainer) return true;
    const { scrollTop, scrollHeight, clientHeight } = this.scrollContainer;
    return scrollHeight - scrollTop - clientHeight < 50;
  }

  /**
   * 显示/隐藏加载指示器
   */
  public showLoading(show: boolean): void {
    const indicator = this.container.querySelector('.loading-indicator');
    indicator?.classList.toggle('hidden', !show);
    this.isLoading = show;
  }

  /**
   * 显示/隐藏输入指示器
   */
  public showTyping(show: boolean): void {
    const indicator = this.container.querySelector('.typing-indicator');
    indicator?.classList.toggle('hidden', !show);
    if (show) {
      this.scrollToBottom();
    }
  }

  /**
   * 更新消息计数
   */
  private updateMessageCount(): void {
    const countEl = this.container.querySelector('.message-count');
    if (countEl) {
      countEl.textContent = `${this.messages.length} 条消息`;
    }
  }

  /**
   * 清空消息列表
   */
  public clear(): void {
    this.messages = [];
    this.renderMessages();
    this.updateMessageCount();
  }

  /**
   * 销毁组件
   */
  public destroy(): void {
    this.removeAllListeners();
    this.container.innerHTML = '';
  }
}

export default MessageList;
