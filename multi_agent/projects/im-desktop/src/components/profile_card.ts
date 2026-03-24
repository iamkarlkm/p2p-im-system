/**
 * 用户资料卡片组件 - 桌面端
 */

import { UserProfile, UserStatus, profileService } from '../services/profile_service';
import { html } from '../utils/template';

export interface ProfileCardOptions {
  profile: UserProfile;
  onEdit?: (profile: UserProfile) => void;
  onAvatarClick?: (profile: UserProfile) => void;
  onStatusChange?: (status: UserStatus) => void;
}

const STATUS_LABELS: Record<UserStatus, string> = {
  ONLINE: '在线',
  AWAY: '离开',
  BUSY: '忙碌',
  DO_NOT_DISTURB: '请勿打扰',
  INVISIBLE: '隐身',
  OFFLINE: '离线'
};

const STATUS_COLORS: Record<UserStatus, string> = {
  ONLINE: '#52c41a',
  AWAY: '#faad14',
  BUSY: '#f5222d',
  DO_NOT_DISTURB: '#f5222d',
  INVISIBLE: '#8c8c8c',
  OFFLINE: '#8c8c8c'
};

export function renderProfileCard(opts: ProfileCardOptions): string {
  const { profile, onEdit, onAvatarClick, onStatusChange } = opts;
  const statusColor = STATUS_COLORS[profile.status];
  const statusLabel = STATUS_LABELS[profile.status];
  const isOnline = profileService.isOnline(profile.userId);

  return html`
    <div class="profile-card" data-user-id="${profile.userId}">
      <div class="profile-header">
        <div class="profile-avatar-wrapper" ${onAvatarClick ? `onclick="window.__profileClick('${profile.userId}')"` : ''}>
          ${profile.avatarUrl 
            ? `<img src="${profile.avatarUrl}" class="profile-avatar" alt="${profile.nickname}" />`
            : `<div class="profile-avatar-placeholder">${profile.nickname.charAt(0).toUpperCase()}</div>`
          }
          <div class="profile-status-dot" style="background-color: ${isOnline ? statusColor : '#8c8c8c'}"></div>
        </div>
        <div class="profile-info">
          <h3 class="profile-nickname">${profile.nickname || profile.userId}</h3>
          <span class="profile-status-label" style="color: ${isOnline ? statusColor : '#8c8c8c'}">
            ${isOnline ? statusLabel : '离线'}
          </span>
        </div>
        ${onEdit ? `
          <button class="profile-edit-btn" onclick="window.__profileEdit('${profile.userId}')">
            编辑资料
          </button>
        ` : ''}
      </div>
      
      ${profile.signature ? `
        <div class="profile-signature">
          <span class="profile-signature-icon">✍️</span>
          <span>${profile.signature}</span>
        </div>
      ` : ''}
      
      <div class="profile-details">
        ${profile.email ? `
          <div class="profile-detail-item">
            <span class="profile-detail-label">邮箱</span>
            <span class="profile-detail-value">${profile.email}</span>
          </div>
        ` : ''}
        ${profile.region ? `
          <div class="profile-detail-item">
            <span class="profile-detail-label">地区</span>
            <span class="profile-detail-value">${profile.region}</span>
          </div>
        ` : ''}
        ${profile.gender ? `
          <div class="profile-detail-item">
            <span class="profile-detail-label">性别</span>
            <span class="profile-detail-value">${profile.gender === 'M' ? '男' : profile.gender === 'F' ? '女' : '未知'}</span>
          </div>
        ` : ''}
        ${profile.birthday ? `
          <div class="profile-detail-item">
            <span class="profile-detail-label">生日</span>
            <span class="profile-detail-value">${profile.birthday}</span>
          </div>
        ` : ''}
      </div>

      ${onStatusChange ? `
        <div class="profile-status-selector">
          <span class="profile-detail-label">设置状态</span>
          <div class="profile-status-options">
            ${(['ONLINE', 'AWAY', 'BUSY', 'DO_NOT_DISTURB', 'INVISIBLE'] as UserStatus[]).map(s => `
              <button class="profile-status-option ${profile.status === s ? 'active' : ''}"
                      onclick="window.__statusChange('${s}')"
                      style="--status-color: ${STATUS_COLORS[s]}">
                <span class="status-dot" style="background-color: ${STATUS_COLORS[s]}"></span>
                ${STATUS_LABELS[s]}
              </button>
            `).join('')}
          </div>
        </div>
      ` : ''}
    </div>
  `;
}

export function initProfileCardEvents(opts: ProfileCardOptions) {
  if (opts.onAvatarClick) {
    (window as any).__profileClick = (userId: string) => opts.onAvatarClick?.(opts.profile);
  }
  if (opts.onEdit) {
    (window as any).__profileEdit = (userId: string) => opts.onEdit?.(opts.profile);
  }
  if (opts.onStatusChange) {
    (window as any).__statusChange = (status: UserStatus) => opts.onStatusChange?.(status);
  }
}

// 状态选择器组件
export function renderStatusSelector(currentStatus: UserStatus, onChange: (s: UserStatus) => void): string {
  return html`
    <div class="status-selector-popup">
      ${(['ONLINE', 'AWAY', 'BUSY', 'DO_NOT_DISTURB', 'INVISIBLE'] as UserStatus[]).map(s => `
        <button class="status-selector-item ${currentStatus === s ? 'active' : ''}"
                onclick="window.__setStatus('${s}')">
          <span class="status-dot" style="background-color: ${STATUS_COLORS[s]}"></span>
          <span>${STATUS_LABELS[s]}</span>
          ${currentStatus === s ? '<span class="check">✓</span>' : ''}
        </button>
      `).join('')}
    </div>
  `;
}
