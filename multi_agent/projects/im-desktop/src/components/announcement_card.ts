/**
 * 群公告组件 - 桌面端
 */
import { Announcement, PagedAnnouncements } from '../services/announcement_service';
import { AnnouncementService } from '../services/announcement_service';

// ============ 公告卡片组件 ============

export class AnnouncementCard {
    private announcement: Announcement;
    private onEdit?: (a: Announcement) => void;
    private onDelete?: (a: Announcement) => void;
    private onPin?: (a: Announcement) => void;
    private onUnpin?: (a: Announcement) => void;
    private onConfirm?: (a: Announcement) => void;
    private onView?: (a: Announcement) => void;
    
    constructor(announcement: Announcement) {
        this.announcement = announcement;
    }
    
    setOnEdit(cb: (a: Announcement) => void) { this.onEdit = cb; return this; }
    setOnDelete(cb: (a: Announcement) => void) { this.onDelete = cb; return this; }
    setOnPin(cb: (a: Announcement) => void) { this.onPin = cb; return this; }
    setOnUnpin(cb: (a: Announcement) => void) { this.onUnpin = cb; return this; }
    setOnConfirm(cb: (a: Announcement) => void) { this.onConfirm = cb; return this; }
    setOnView(cb: (a: Announcement) => void) { this.onView = cb; return this; }
    
    render(): HTMLElement {
        const card = document.createElement('div');
        card.className = `announcement-card ${this.announcement.pinned ? 'pinned' : ''} ${this.announcement.type === 'IMPORTANT' ? 'important' : ''}`;
        card.dataset.id = this.announcement.announcementId;
        
        const header = this.renderHeader();
        const content = this.renderContent();
        const footer = this.renderFooter();
        
        card.append(header, content, footer);
        
        card.addEventListener('click', () => this.onView?.(this.announcement));
        
        return card;
    }
    
    private renderHeader(): HTMLElement {
        const header = document.createElement('div');
        header.className = 'announcement-header';
        
        // 类型标签
        const typeTag = document.createElement('span');
        typeTag.className = `announcement-type-tag type-${this.announcement.type.toLowerCase()}`;
        typeTag.textContent = this.getTypeLabel();
        header.appendChild(typeTag);
        
        // 置顶标签
        if (this.announcement.pinned) {
            const pinTag = document.createElement('span');
            pinTag.className = 'announcement-pinned-tag';
            pinTag.textContent = '📌 置顶';
            header.appendChild(pinTag);
        }
        
        // 标题
        const title = document.createElement('h3');
        title.className = 'announcement-title';
        title.textContent = this.announcement.title;
        header.appendChild(title);
        
        // 作者信息
        const meta = document.createElement('div');
        meta.className = 'announcement-meta';
        meta.innerHTML = `
            <span class="announcement-author">${this.announcement.authorName}</span>
            <span class="announcement-time">${this.formatTime(this.announcement.createdAt)}</span>
            ${this.announcement.edited ? '<span class="announcement-edited">(已编辑)</span>' : ''}
        `;
        header.appendChild(meta);
        
        // 操作按钮（仅作者可见）
        // ... 添加编辑/删除/置顶按钮
        
        return header;
    }
    
    private renderContent(): HTMLElement {
        const content = document.createElement('div');
        content.className = 'announcement-content';
        content.innerHTML = this.renderMarkdown(this.announcement.content);
        return content;
    }
    
    private renderFooter(): HTMLElement {
        const footer = document.createElement('div');
        footer.className = 'announcement-footer';
        
        // 查看数
        const views = document.createElement('span');
        views.className = 'announcement-views';
        views.textContent = `👁 ${this.announcement.viewCount}`;
        footer.appendChild(views);
        
        // 确认数
        const confirms = document.createElement('span');
        confirms.className = 'announcement-confirms';
        confirms.textContent = `✓ ${this.announcement.confirmCount}`;
        footer.appendChild(confirms);
        
        return footer;
    }
    
    private getTypeLabel(): string {
        switch (this.announcement.type) {
            case 'IMPORTANT': return '重要';
            case 'PINNED': return '置顶';
            default: return '公告';
        }
    }
    
    private formatTime(isoString: string): string {
        const date = new Date(isoString);
        const now = new Date();
        const diff = now.getTime() - date.getTime();
        if (diff < 60000) return '刚刚';
        if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`;
        if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`;
        if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`;
        return date.toLocaleDateString('zh-CN');
    }
    
    private renderMarkdown(text: string): string {
        // 简单的Markdown渲染
        return text
            .replace(/^### (.+)$/gm, '<h3>$1</h3>')
            .replace(/^## (.+)$/gm, '<h2>$1</h2>')
            .replace(/^# (.+)$/gm, '<h1>$1</h1>')
            .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
            .replace(/\*(.+?)\*/g, '<em>$1</em>')
            .replace(/`(.+?)`/g, '<code>$1</code>')
            .replace(/\n/g, '<br>');
    }
}

// ============ 创建公告对话框 ============

export class CreateAnnouncementDialog {
    private dialog: HTMLElement | null = null;
    private onSubmit?: (data: { title: string; content: string; type: string }) => void;
    private currentGroupId: string = '';
    
    show(groupId: string, onSubmit: (data: { title: string; content: string; type: string }) => void): void {
        this.currentGroupId = groupId;
        this.onSubmit = onSubmit;
        
        this.dialog = document.createElement('div');
        this.dialog.className = 'announcement-dialog-overlay';
        this.dialog.innerHTML = `
            <div class="announcement-dialog">
                <div class="dialog-header">
                    <h2>发布群公告</h2>
                    <button class="dialog-close-btn">&times;</button>
                </div>
                <div class="dialog-body">
                    <div class="form-group">
                        <label>标题</label>
                        <input type="text" class="announcement-title-input" placeholder="公告标题" maxlength="200" />
                    </div>
                    <div class="form-group">
                        <label>类型</label>
                        <select class="announcement-type-select">
                            <option value="NORMAL">普通公告</option>
                            <option value="IMPORTANT">重要公告</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>内容 (支持Markdown)</label>
                        <textarea class="announcement-content-input" placeholder="公告内容..." maxlength="50000" rows="10"></textarea>
                        <div class="markdown-hint">支持 Markdown 格式：# 标题、**粗体**、*斜体*、`代码`</div>
                    </div>
                </div>
                <div class="dialog-footer">
                    <button class="btn-cancel">取消</button>
                    <button class="btn-submit">发布</button>
                </div>
            </div>
        `;
        
        document.body.appendChild(this.dialog);
        
        // 事件绑定
        this.dialog.querySelector('.dialog-close-btn')?.addEventListener('click', () => this.hide());
        this.dialog.querySelector('.btn-cancel')?.addEventListener('click', () => this.hide());
        this.dialog.querySelector('.btn-submit')?.addEventListener('click', () => this.handleSubmit());
        this.dialog.addEventListener('click', (e) => {
            if (e.target === this.dialog) this.hide();
        });
    }
    
    private handleSubmit(): void {
        const title = (this.dialog?.querySelector('.announcement-title-input') as HTMLInputElement)?.value?.trim();
        const content = (this.dialog?.querySelector('.announcement-content-input') as HTMLTextAreaElement)?.value?.trim();
        const type = (this.dialog?.querySelector('.announcement-type-select') as HTMLSelectElement)?.value || 'NORMAL';
        
        if (!title || !content) {
            alert('请填写标题和内容');
            return;
        }
        
        this.onSubmit?.({ title, content, type });
        this.hide();
    }
    
    hide(): void {
        this.dialog?.remove();
        this.dialog = null;
    }
}

// ============ 公告面板 ============

export class AnnouncementPanel {
    private container: HTMLElement;
    private groupId: string;
    private service: AnnouncementService;
    private currentPage = 1;
    private totalPages = 1;
    
    constructor(container: HTMLElement, groupId: string, service: AnnouncementService) {
        this.container = container;
        this.groupId = groupId;
        this.service = service;
        this.setupWebSocket();
    }
    
    private setupWebSocket(): void {
        this.service.subscribeToGroup(this.groupId);
        
        this.service.onAnnouncementCreated((announcement) => {
            this.prependAnnouncement(announcement);
            this.showToast('新公告发布');
        });
        
        this.service.onAnnouncementUpdated((announcement) => {
            this.updateAnnouncement(announcement);
        });
        
        this.service.onAnnouncementDeleted(({ announcementId }) => {
            this.removeAnnouncement(announcementId);
        });
        
        this.service.onAnnouncementPinned((announcement) => {
            this.updateAnnouncement(announcement);
        });
    }
    
    async load(): Promise<void> {
        try {
            const data = await this.service.getAnnouncementsPaged(this.groupId, this.currentPage, 20);
            this.totalPages = data.totalPages;
            this.renderAnnouncements(data.announcements);
            this.renderPagination();
        } catch (e) {
            console.error('Failed to load announcements', e);
        }
    }
    
    private renderAnnouncements(announcements: Announcement[]): void {
        const list = this.container.querySelector('.announcement-list');
        if (!list) return;
        
        list.innerHTML = '';
        for (const a of announcements) {
            const card = new AnnouncementCard(a)
                .setOnView((ann) => this.viewAnnouncement(ann))
                .render();
            list.appendChild(card);
        }
    }
    
    private prependAnnouncement(announcement: Announcement): void {
        const list = this.container.querySelector('.announcement-list');
        if (!list) return;
        
        const card = new AnnouncementCard(announcement)
            .setOnView((ann) => this.viewAnnouncement(ann))
            .render();
        list.prepend(card);
    }
    
    private updateAnnouncement(announcement: Announcement): void {
        const existing = this.container.querySelector(`[data-id="${announcement.announcementId}"]`);
        if (existing) {
            const card = new AnnouncementCard(announcement)
                .setOnView((ann) => this.viewAnnouncement(ann))
                .render();
            existing.replaceWith(card);
        }
    }
    
    private removeAnnouncement(announcementId: string): void {
        this.container.querySelector(`[data-id="${announcementId}"]`)?.remove();
    }
    
    private viewAnnouncement(announcement: Announcement): void {
        // 显示公告详情弹窗
        console.log('View announcement:', announcement);
    }
    
    private renderPagination(): void {
        const pagination = this.container.querySelector('.announcement-pagination');
        if (!pagination) return;
        
        pagination.innerHTML = '';
        for (let i = 1; i <= this.totalPages; i++) {
            const btn = document.createElement('button');
            btn.textContent = String(i);
            btn.className = i === this.currentPage ? 'active' : '';
            btn.addEventListener('click', async () => {
                this.currentPage = i;
                await this.load();
            });
            pagination.appendChild(btn);
        }
    }
    
    private showToast(message: string): void {
        const toast = document.createElement('div');
        toast.className = 'announcement-toast';
        toast.textContent = message;
        document.body.appendChild(toast);
        setTimeout(() => toast.remove(), 3000);
    }
    
    destroy(): void {
        this.service.unsubscribeFromGroup(this.groupId);
    }
}
