import React, { useEffect, useState, useCallback } from 'react';
import { announcementService, type Announcement } from '../../services/announcementService';
import AnnouncementCard from './AnnouncementCard';
import AnnouncementCreateModal from './AnnouncementCreateModal';
import './AnnouncementPanel.css';

interface Props {
  groupId: number;
  currentUserId: number;
  isAdmin: boolean;
  memberCount: number;
}

const AnnouncementPanel: React.FC<Props> = ({ groupId, currentUserId, isAdmin, memberCount }) => {
  const [announcements, setAnnouncements] = useState<Announcement[]>([]);
  const [loading, setLoading] = useState(false);
  const [filter, setFilter] = useState<'all' | 'unread' | 'pinned'>('all');
  const [showCreate, setShowCreate] = useState(false);
  const [stats, setStats] = useState<Record<number, { readCount: number; totalMemberCount: number }>>({});

  const loadAnnouncements = useCallback(async () => {
    setLoading(true);
    try {
      const data = await announcementService.getGroupAnnouncements(groupId);
      setAnnouncements(data);
      // 加载每条公告的统计
      for (const ann of data.slice(0, 5)) {
        const s = await announcementService.getReadStats(ann.id, memberCount).catch(() => null);
        if (s) setStats(prev => ({ ...prev, [ann.id]: s }));
      }
    } catch (err) {
      console.error('加载公告失败', err);
    } finally {
      setLoading(false);
    }
  }, [groupId, memberCount]);

  useEffect(() => { loadAnnouncements(); }, [loadAnnouncements]);

  // 标记单条已读
  const handleRead = async (announcementId: number) => {
    await announcementService.markAsRead(announcementId, currentUserId, 'web');
    setAnnouncements(prev => prev.map(a => a.id === announcementId ? { ...a, isRead: true } : a));
  };

  // 确认紧急公告
  const handleConfirm = async (announcementId: number) => {
    await announcementService.confirmAnnouncement(announcementId, currentUserId);
    setAnnouncements(prev => prev.map(a => a.id === announcementId ? { ...a, isConfirmed: true } : a));
  };

  // 过滤
  const filtered = announcements.filter(a => {
    if (filter === 'unread') return !a.isRead;
    if (filter === 'pinned') return a.pinned;
    return true;
  });

  return (
    <div className="announcement-panel">
      <div className="announcement-header">
        <div className="announcement-title-row">
          <h3>群公告</h3>
          {isAdmin && (
            <button className="btn-create-ann" onClick={() => setShowCreate(true)}>+ 发布公告</button>
          )}
        </div>
        <div className="announcement-filters">
          {(['all', 'unread', 'pinned'] as const).map(f => (
            <button key={f} className={`filter-btn ${filter === f ? 'active' : ''}`} onClick={() => setFilter(f)}>
              {f === 'all' ? '全部' : f === 'unread' ? '未读' : '置顶'}
            </button>
          ))}
        </div>
      </div>

      <div className="announcement-list">
        {loading && <div className="ann-loading">加载中...</div>}
        {!loading && filtered.length === 0 && <div className="ann-empty">暂无公告</div>}
        {filtered.map(ann => (
          <AnnouncementCard
            key={ann.id}
            announcement={ann}
            stats={stats[ann.id]}
            onRead={handleRead}
            onConfirm={handleConfirm}
            isOwner={ann.authorId === currentUserId}
          />
        ))}
      </div>

      {showCreate && (
        <AnnouncementCreateModal
          groupId={groupId}
          onClose={() => setShowCreate(false)}
          onCreated={() => { setShowCreate(false); loadAnnouncements(); }}
        />
      )}
    </div>
  );
};

export default AnnouncementPanel;
