// 联系人列表组件
import { contactService, Contact, ContactGroup } from '../services/contact.js';

export class ContactListComponent {
    private container: HTMLElement;
    private contacts: Contact[] = [];
    private currentTab: 'all' | 'requests' = 'all';

    constructor(containerId: string) {
        const container = document.getElementById(containerId);
        if (!container) {
            throw new Error(`Container #${containerId} not found`);
        }
        this.container = container;
    }

    async init(): Promise<void> {
        await this.loadContacts();
        this.render();
        this.bindEvents();
    }

    private async loadContacts(): Promise<void> {
        this.contacts = await contactService.getContacts();
    }

    private render(): void {
        this.container.innerHTML = `
            <div class="contact-list-container">
                <div class="contact-list-header">
                    <h3>联系人</h3>
                    <button class="btn btn-icon" id="btn-add-contact" title="添加好友">➕</button>
                </div>
                
                <div class="contact-tabs">
                    <button class="tab-btn ${this.currentTab === 'all' ? 'active' : ''}" data-tab="all">
                        全部联系人
                        <span class="tab-count">${this.contacts.length}</span>
                    </button>
                    <button class="tab-btn ${this.currentTab === 'requests' ? 'active' : ''}" data-tab="requests">
                        新的朋友
                        <span class="tab-badge">1</span>
                    </button>
                </div>

                <div class="contact-search">
                    <input type="text" id="contact-search-input" placeholder="搜索联系人...">
                </div>

                <div class="contact-list-content" id="contact-list-content">
                    ${this.renderContactList()}
                </div>
            </div>
        `;
    }

    private renderContactList(): string {
        if (this.contacts.length === 0) {
            return `
                <div class="empty-state">
                    <div class="empty-icon">👥</div>
                    <p>暂无联系人</p>
                    <button class="btn btn-primary" id="btn-add-first">添加好友</button>
                </div>
            `;
        }

        const grouped = contactService.groupContactsByLetter(this.contacts);
        
        let html = `
            <div class="contact-stats">
                <span>共 ${this.contacts.length} 位联系人</span>
                <span class="online-count">${contactService.getOnlineCount()} 人在线</span>
            </div>
        `;

        grouped.forEach((contacts, letter) => {
            html += `
                <div class="contact-group">
                    <div class="group-header">${letter}</div>
                    ${contacts.map(contact => this.renderContactItem(contact)).join('')}
                </div>
            `;
        });

        return html;
    }

    private renderContactItem(contact: Contact): string {
        const displayName = contact.remark || contact.nickname;
        const statusClass = `status-${contact.status}`;
        const statusText = this.getStatusText(contact.status);

        return `
            <div class="contact-item" data-contact-id="${contact.id}">
                <div class="contact-avatar ${statusClass}">
                    <img src="${contact.avatar || '/assets/default-avatar.png'}" alt="${displayName}">
                    <span class="status-dot"></span>
                </div>
                <div class="contact-info">
                    <div class="contact-name">${displayName}</div>
                    <div class="contact-meta">
                        ${contact.signature ? `<span class="signature">${contact.signature}</span>` : ''}
                        <span class="status-text">${statusText}</span>
                    </div>
                </div>
                <div class="contact-actions">
                    <button class="btn btn-icon btn-chat" title="发消息">💬</button>
                    <button class="btn btn-icon btn-more" title="更多">⋮</button>
                </div>
            </div>
        `;
    }

    private getStatusText(status: string): string {
        const statusMap: Record<string, string> = {
            'online': '在线',
            'offline': '离线',
            'away': '离开',
            'busy': '忙碌'
        };
        return statusMap[status] || status;
    }

    private bindEvents(): void {
        // 搜索
        const searchInput = this.container.querySelector('#contact-search-input');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this.handleSearch((e.target as HTMLInputElement).value);
            });
        }

        // 添加好友按钮
        const addBtn = this.container.querySelector('#btn-add-contact');
        if (addBtn) {
            addBtn.addEventListener('click', () => {
                this.showAddContactDialog();
            });
        }

        // 标签切换
        this.container.querySelectorAll('.tab-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const tab = (e.currentTarget as HTMLElement).dataset.tab as 'all' | 'requests';
                this.switchTab(tab);
            });
        });

        // 联系人项点击
        this.container.querySelectorAll('.contact-item').forEach(item => {
            item.addEventListener('click', (e) => {
                const contactId = (e.currentTarget as HTMLElement).dataset.contactId;
                if (contactId) {
                    this.selectContact(contactId);
                }
            });
        });
    }

    private async handleSearch(keyword: string): Promise<void> {
        const filtered = await contactService.searchContacts(keyword);
        this.contacts = filtered;
        this.render();
        this.bindEvents();
    }

    private switchTab(tab: 'all' | 'requests'): void {
        this.currentTab = tab;
        this.render();
        this.bindEvents();
    }

    private selectContact(contactId: string): void {
        console.log('选择联系人:', contactId);
        // 高亮选中
        this.container.querySelectorAll('.contact-item').forEach(item => {
            item.classList.remove('selected');
        });
        const selected = this.container.querySelector(`[data-contact-id="${contactId}"]`);
        if (selected) {
            selected.classList.add('selected');
        }
        
        // 触发事件
        const event = new CustomEvent('contactSelected', { 
            detail: { contactId } 
        });
        document.dispatchEvent(event);
    }

    private showAddContactDialog(): void {
        const dialog = document.createElement('div');
        dialog.className = 'modal-dialog';
        dialog.innerHTML = `
            <div class="modal-overlay"></div>
            <div class="modal-content">
                <div class="modal-header">
                    <h3>添加好友</h3>
                    <button class="btn btn-icon btn-close">✕</button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label>用户名/手机号</label>
                        <input type="text" id="add-contact-input" placeholder="请输入用户名或手机号">
                    </div>
                    <div class="form-group">
                        <label>验证消息</label>
                        <textarea id="add-contact-message" rows="3" placeholder="我是...">你好，我想添加你为好友</textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-secondary" id="btn-cancel">取消</button>
                    <button class="btn btn-primary" id="btn-confirm">发送申请</button>
                </div>
            </div>
        `;

        document.body.appendChild(dialog);

        // 绑定事件
        dialog.querySelector('.btn-close')?.addEventListener('click', () => {
            dialog.remove();
        });
        dialog.querySelector('.btn-overlay')?.addEventListener('click', () => {
            dialog.remove();
        });
        dialog.querySelector('#btn-cancel')?.addEventListener('click', () => {
            dialog.remove();
        });
        dialog.querySelector('#btn-confirm')?.addEventListener('click', async () => {
            const input = dialog.querySelector('#add-contact-input') as HTMLInputElement;
            const message = dialog.querySelector('#add-contact-message') as HTMLTextAreaElement;
            
            if (input.value.trim()) {
                await contactService.addFriend(input.value.trim(), message.value);
                dialog.remove();
                // 刷新列表
                await this.loadContacts();
                this.render();
                this.bindEvents();
            }
        });
    }
}

export default ContactListComponent;
