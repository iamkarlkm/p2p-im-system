import React from 'react';
import type { Announcement } from '../../services/announcementService';
import './AnnouncementCard.css';

interface Props {
  announcement: Announcement;
  stats?: { readCount: number; totalMemberCount: number };
  onRead: (id: number) => void;
  onConfirm: (id: number) => void;
  isOwner: boolean;
}

const typeLabels: Record<string, string> = {
  normal: '公告', rule: '群规', notice: '通知', event: '活动',
};

const typeColors: Record<string, string> = {
  normal: '#1890ff', rule: '#fa8c16', notice: '#52c41a', event: '#722ed1',
};

const AnnouncementCard: React.FC<Props> = ({ announcement: ann, stats, onRead, onConfirm, isOwner }) => {
  const unread = !ann.isRead;
  const publishDate = new Date(ann.publishTime).toLocaleDateString('zh-CN');

  const handleClick = () => {
    if (unread) onRead(ann.id);
  };

  return (
    <div
      className={`ann-card ${unread ? 'unread' : ''} ${ann.urgent ? 'urgent' : ''} ${ann.pinned ? 'pinned' : ''}`}
      onClick={handleClick}
    >
      <div className="ann-card-header">
        <div className="ann-meta">
          {ann.pinned && <span className="pin-badge">置顶</span>}
          {ann.urgent && <span className="urgent-badge">紧急</span>}
          <span className="type-badge" style={{ background: typeColors[ann.type] || '#1890ff' }}>
            {typeLabels[ann.type] || '公告'}
          </span>
          {ann.requiredRead && <span className="required-badge">必须阅读</span>}
        </div>
        <span className="ann-date">{publishDate}</span>
      </div>

      {ann.title && <div className="ann-title">{ann.title}</div>}

      <div className="ann-content">{ann.content}</div>

      <div className="ann-card-footer">
        {stats && (
          <div className="ann-stats">
            <span>已读 {stats.readCount}/{stats.totalMemberCount}</span>
            <div className="read-bar">
              <div
                className="read-fill"
                style={{ width: `${stats.totalMemberCount > 0 ? (stats.readCount / stats.totalMemberCount) * 100 : 0}%` }}
              />
            </div>
          </div>
        )}
        <div className="ann-actions">
          {ann.requiredRead && !ann.isConfirmed && (
            <button className="btn-confirm" onClick={(e) => { e.stopPropagation(); onConfirm(ann.id); }}>
              确认阅读
            </button>
          )}
          {ann.isConfirmed && <span className="confirmed-tag">已确认</span>}
          {unread && <span className="unread-dot" />}
        </div>
      </div>
    </div>
  );
};

export default AnnouncementCard;
