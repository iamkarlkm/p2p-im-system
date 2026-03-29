/**
 * 群公告组件 - 桌面端 TypeScript 实现
 * 
 * @author IM System
 * @version 1.0
 */

import { announcementService, GroupAnnouncement } from '../services/announcement_service';
import './announcement.css';

// 公告卡片组件
export class AnnouncementCard {
  private announcement: GroupAnnouncement;
  private container: HTMLElement;
  private onReadCallback?: (announcementId: string) => void;
  private onEditCallback?: (announcement: GroupAnnouncement) => void;
  private onDeleteCallback?: (announcementId: string) => void;

  constructor(
    announcement: GroupAnnouncement,
    options: {
      onRead?: (announcementId: string) => void;
      onEdit?: (announcement: GroupAnnouncement) => void;
      onDelete?: (announcementId: string) => void;
    } = {}
  ) {
    this.announcement = announcement;
    this.container = document.createElement('div');
    this.onReadCallback = options.onRead;
    this.onEditCallback = options.onEdit;
    this.onDeleteCallback = options.onDelete;
  }

  public render(): HTMLElement {
    this.container.className = `announcement-card ${this.announcement.pinned ? 'pinned' : ''}`;
    this.container.dataset.announcementId = this.announcement.announcementId;

    // 置顶标签
    const pinnedBadge = this.announcement.pinned 
      ? '<span class="pinned-badge">置顶</span>' 
      : '';

    // 编辑标记
    const editedMark = this.announcement.edited 
      ? '<span class="edited-mark">(已编辑)</span>' 
      : '';

    // 时间格式化
    const timeStr = announcementService.formatTime(this.announcement.createdAt);

    this.container.innerHTML = `
      <div class="announcement-header">
        <div class="announcement-author">
          <img class="author-avatar" src="${this.announcement.authorAvatar || '/assets/default-avatar.png'}" alt="avatar" />
          <div class="author-info">
            <span class="author-name">${this.announcement.authorName}</span>
            <span class="announcement-time">${timeStr}${editedMark}</span>
          </div>
        </div>
        ${pinnedBadge}
      </div>
      <div class="announcement-title">${this.escapeHtml(this.announcement.title)}</div>
      <div class="announcement-content">
        ${this.renderContent()}
      </div>
      <div class="announcement-footer">
        <div class="announcement-stats">
          <span class="stat-item">
            <i class="icon-eye"></i>
            <span class="read-count">${this.announcement.readCount}</span> 已读
          </span>
          <span class="stat-item">
            <i class="icon-comment"></i>
            <span class="comment-count">${this.announcement.commentCount}</span> 评论
          </span>
        </div>
        <div class="announcement-actions">
          <button class="btn-read" title="标记已读">已读</button>
          <button class="btn-expand" title="展开全文">展开</button>
          ${this.renderAdminActions()}
        </div>
      </div>
    `;

    // 绑定事件
    this.bindEvents();

    return this.container;
  }

  private renderContent(): string {
    const content = this.announcement.content;
    // 如果内容过长，显示摘要
    if (content.length > 200) {
      return `
        <div class="content-preview">${this.escapeHtml(content.substring(0, 200))}...</div>
        <div class="content-full" style="display: none;">${this.parseMarkdown(content)}</div>
      `;
    }
    return `<div class="content-full">${this.parseMarkdown(content)}</div>`;
  }

  private renderAdminActions(): string {
    // 检查是否是管理员/发布者
    const isAdmin = true; // TODO: 从上下文获取
    if (!isAdmin) return '';
    
    return `
      <button class="btn-edit" title="编辑">编辑</button>
      <button class="btn-delete" title="删除">删除</button>
    `;
  }

  private parseMarkdown(text: string): string {
    // 简单的Markdown解析
    return text
      .replace(/\n/g, '<br>')
      .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.+?)\*/g, '<em>$1</em>')
      .replace(/`(.+?)`/g, '<code>$1</code>')
      .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank">$1</a>');
  }

  private escapeHtml(text: string): string {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  private bindEvents(): void {
    // 展开/折叠
    const expandBtn = this.container.querySelector('.btn-expand');
    expandBtn?.addEventListener('click', () => {
      const preview = this.container.querySelector('.content-preview') as HTMLElement;
      const full = this.container.querySelector('.content-full:last-child') as HTMLElement;
      if (preview && full) {
        const isExpanded = full.style.display !== 'none';
        preview.style.display = isExpanded ? 'block' : 'none';
        full.style.display = isExpanded ? 'none' : 'block';
        expandBtn.textContent = isExpanded ? '展开' : '收起';
      }
    });

    // 标记已读
    const readBtn = this.container.querySelector('.btn-read');
    readBtn?.addEventListener('click', () => {
      this.onReadCallback?.(this.announcement.announcementId);
      readBtn.textContent = '已读 ✓';
      readBtn.setAttribute('disabled', 'true');
    });

    // 编辑
    const editBtn = this.container.querySelector('.btn-edit');
    editBtn?.addEventListener('click', () => {
      this.onEditCallback?.(this.announcement);
    });

    // 删除
    const deleteBtn = this.container.querySelector('.btn-delete');
    deleteBtn?.addEventListener('click', () => {
      if (confirm('确定要删除这条公告吗？')) {
        this.onDeleteCallback?.(this.announcement.announcementId);
      }
    });
  }
}

// 创建公告对话框
export class CreateAnnouncementDialog {
  private dialog: HTMLElement;
  private onSubmitCallback?: (data: { title: string; content: string }) => void;
  private onCancelCallback?: () => void;

  constructor(options: {
    onSubmit?: (data: { title: string; content: string }) => void;
    onCancel?: () => void;
  } = {}) {
    this.onSubmitCallback = options.onSubmit;
    this.onCancelCallback = options.onCancel;
    this.dialog = this.createDialog();
  }

  private createDialog(): HTMLElement {
    const dialog = document.createElement('div');
    dialog.className = 'announcement-dialog-overlay';
    dialog.innerHTML = `
      <div class="announcement-dialog">
        <div class="dialog-header">
          <h3>发布群公告</h3>
          <button class="btn-close">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label for="announcement-title">标题</label>
            <input type="text" id="announcement-title" maxlength="100" placeholder="请输入公告标题" />
            <span class="char-count"><span class="current">0</span>/100</span>
          </div>
          <div class="form-group">
            <label for="announcement-content">内容</label>
            <textarea id="announcement-content" maxlength="10000" rows="10" placeholder="支持Markdown格式"></textarea>
            <div class="markdown-tips">
              <span>**粗体**</span>
              <span>*斜体*</span>
              <span>`代码`</span>
              <span>[链接](url)</span>
            </div>
            <span class="char-count"><span class="current">0</span>/10000</span>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel">取消</button>
          <button class="btn-submit">发布</button>
        </div>
      </div>
    `;
    return dialog;
  }

  public show(): void {
    document.body.appendChild(this.dialog);
    this.bindEvents();
  }

  public hide(): void {
    this.dialog.remove();
  }

  private bindEvents(): void {
    // 关闭
    const closeBtn = this.dialog.querySelector('.btn-close');
    const cancelBtn = this.dialog.querySelector('.btn-cancel');
    closeBtn?.addEventListener('click', () => this.hide());
    cancelBtn?.addEventListener('click', () => {
      this.onCancelCallback?.();
      this.hide();
    });

    // 标题字数统计
    const titleInput = this.dialog.querySelector('#announcement-title') as HTMLInputElement;
    const titleCount = this.dialog.querySelector('.form-group:first-child .char-count .current');
    titleInput?.addEventListener('input', () => {
      if (titleCount) titleCount.textContent = titleInput.value.length.toString();
    });

    // 内容字数统计
    const contentInput = this.dialog.querySelector('#announcement-content') as HTMLTextAreaElement;
    const contentCount = this.dialog.querySelector('.form-group:last-child .char-count .current');
    contentInput?.addEventListener('input', () => {
      if (contentCount) contentCount.textContent = contentInput.value.length.toString();
    });

    // 提交
    const submitBtn = this.dialog.querySelector('.btn-submit');
    submitBtn?.addEventListener('click', () => {
      const title = titleInput.value.trim();
      const content = contentInput.value.trim();
      
      if (!title) {
        alert('请输入公告标题');
        return;
      }
      if (!content) {
        alert('请输入公告内容');
        return;
      }
      
      this.onSubmitCallback?.({ title, content });
      this.hide();
    });

    // 点击遮罩关闭
    this.dialog.addEventListener('click', (e) => {
      if (e.target === this.dialog) {
        this.hide();
      }
    });
  }
}

// 公告面板组件
export class AnnouncementPanel {
  private container: HTMLElement;
  private groupId: string;
  private userId: string;
  private currentPage: number = 0;
  private pageSize: number = 20;
  private isLoading: boolean = false;

  constructor(groupId: string, userId: string) {
    this.groupId = groupId;
    this.userId = userId;
    this.container = this.createContainer();
  }

  private createContainer(): HTMLElement {
    const container = document.createElement('div');
    container.className = 'announcement-panel';
    container.innerHTML = `
      <div class="panel-header">
        <h2>群公告</h2>
        <div class="header-actions">
          <button class="btn-refresh">刷新</button>
          <button class="btn-create">发布公告</button>
        </div>
      </div>
      <div class="panel-tabs">
        <button class="tab active" data-tab="all">全部</button>
        <button class="tab" data-tab="pinned">置顶</button>
        <button class="tab" data-tab="unread">未读</button>
      </div>
      <div class="panel-content">
        <div class="announcement-list"></div>
        <div class="loading-indicator" style="display: none;">
          <div class="spinner"></div>
          <span>加载中...</span>
        </div>
      </div>
      <div class="panel-footer">
        <button class="btn-more" style="display: none;">加载更多</button>
      </div>
    `;
    return container;
  }

  public render(): HTMLElement {
    this.bindEvents();
    this.loadAnnouncements();
    this.setupWebSocketListeners();
    return this.container;
  }

  private bindEvents(): void {
    // 刷新
    const refreshBtn = this.container.querySelector('.btn-refresh');
    refreshBtn?.addEventListener('click', () => {
      this.currentPage = 0;
      this.loadAnnouncements();
    });

    // 创建公告
    const createBtn = this.container.querySelector('.btn-create');
    createBtn?.addEventListener('click', () => {
      this.showCreateDialog();
    });

    // Tab切换
    const tabs = this.container.querySelectorAll('.tab');
    tabs.forEach(tab => {
      tab.addEventListener('click', () => {
        tabs.forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        this.currentPage = 0;
        this.loadAnnouncements();
      });
    });

    // 加载更多
    const moreBtn = this.container.querySelector('.btn-more');
    moreBtn?.addEventListener('click', () => {
      this.currentPage++;
      this.loadAnnouncements(true);
    });
  }

  private setupWebSocketListeners(): void {
    // 新公告
    announcementService.on('new', (announcement: GroupAnnouncement) => {
      if (announcement.groupId === this.groupId) {
        this.prependAnnouncement(announcement);
      }
    });

    // 公告更新
    announcementService.on('updated', (announcement: GroupAnnouncement) => {
      if (announcement.groupId === this.groupId) {
        this.updateAnnouncement(announcement);
      }
    });

    // 公告删除
    announcementService.on('deleted', (announcementId: string) => {
      this.removeAnnouncement(announcementId);
    });
  }

  private async loadAnnouncements(append: boolean = false): Promise<void> {
    if (this.isLoading) return;
    this.isLoading = true;

    const loadingIndicator = this.container.querySelector('.loading-indicator') as HTMLElement;
    const listContainer = this.container.querySelector('.announcement-list') as HTMLElement;
    const moreBtn = this.container.querySelector('.btn-more') as HTMLElement;

    loadingIndicator.style.display = 'flex';

    try {
      const activeTab = this.container.querySelector('.tab.active')?.getAttribute('data-tab');
      let announcements: GroupAnnouncement[];

      switch (activeTab) {
        case 'pinned':
          announcements = await announcementService.getPinnedAnnouncements(this.groupId);
          break;
        case 'unread':
          announcements = await announcementService.getUnreadAnnouncements(this.groupId, this.userId);
          break;
        default:
          const result = await announcementService.getAnnouncements(this.groupId, this.currentPage, this.pageSize);
          announcements = result.announcements;
          moreBtn.style.display = result.announcements.length < result.total ? 'block' : 'none';
      }

      if (append) {
        // 追加
        announcements.forEach(a => {
          const card = new AnnouncementCard(a, {
            onRead: (id) => this.handleMarkAsRead(id),
            onEdit: (a) => this.handleEdit(a),
            onDelete: (id) => this.handleDelete(id)
          });
          listContainer.appendChild(card.render());
        });
      } else {
        // 替换
        listContainer.innerHTML = '';
        announcements.forEach(a => {
          const card = new AnnouncementCard(a, {
            onRead: (id) => this.handleMarkAsRead(id),
            onEdit: (a) => this.handleEdit(a),
            onDelete: (id) => this.handleDelete(id)
          });
          listContainer.appendChild(card.render());
        });
      }
    } catch (error) {
      console.error('加载公告失败:', error);
      listContainer.innerHTML = '<div class="error-message">加载失败，请重试</div>';
    } finally {
      this.isLoading = false;
      loadingIndicator.style.display = 'none';
    }
  }

  private prependAnnouncement(announcement: GroupAnnouncement): void {
    const listContainer = this.container.querySelector('.announcement-list') as HTMLElement;
    const card = new AnnouncementCard(announcement, {
      onRead: (id) => this.handleMarkAsRead(id),
      onEdit: (a) => this.handleEdit(a),
      onDelete: (id) => this.handleDelete(id)
    });
    listContainer.insertBefore(card.render(), listContainer.firstChild);
  }

  private updateAnnouncement(announcement: GroupAnnouncement): void {
    const oldCard = this.container.querySelector(`[data-announcement-id="${announcement.announcementId}"]`);
    if (oldCard) {
      const card = new AnnouncementCard(announcement, {
        onRead: (id) => this.handleMarkAsRead(id),
        onEdit: (a) => this.handleEdit(a),
        onDelete: (id) => this.handleDelete(id)
      });
      oldCard.replaceWith(card.render());
    }
  }

  private removeAnnouncement(announcementId: string): void {
    const card = this.container.querySelector(`[data-announcement-id="${announcementId}"]`);
    if (card) {
      card.remove();
    }
  }

  private showCreateDialog(): void {
    const dialog = new CreateAnnouncementDialog({
      onSubmit: async (data) => {
        try {
          await announcementService.publishAnnouncement({
            groupId: this.groupId,
            authorId: this.userId,
            authorName: '当前用户', // TODO: 从上下文获取
            authorAvatar: '',
            title: data.title,
            content: data.content
          });
          alert('发布成功');
          this.loadAnnouncements();
        } catch (error) {
          alert('发布失败: ' + (error as Error).message);
        }
      }
    });
    dialog.show();
  }

  private async handleMarkAsRead(announcementId: string): Promise<void> {
    try {
      await announcementService.markAsRead(announcementId, this.userId);
    } catch (error) {
      console.error('标记已读失败:', error);
    }
  }

  private handleEdit(announcement: GroupAnnouncement): void {
    console.log('编辑公告:', announcement);
    // TODO: 显示编辑对话框
  }

  private async handleDelete(announcementId: string): Promise<void> {
    try {
      await announcementService.deleteAnnouncement(announcementId, this.userId);
      alert('删除成功');
      this.removeAnnouncement(announcementId);
    } catch (error) {
      alert('删除失败: ' + (error as Error).message);
    }
  }
}

// 导出
export default {
  AnnouncementCard,
  CreateAnnouncementDialog,
  AnnouncementPanel
};
