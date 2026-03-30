import React, { useState, useEffect, useCallback } from 'react';
import { 
  groupAnnouncementService, 
  GroupAnnouncement, 
  CreateAnnouncementRequest,
  UpdateAnnouncementRequest 
} from '../services/groupAnnouncementService';
import './GroupAnnouncementPanel.css';

/**
 * 群公告面板组件
 * 功能ID: #30
 * 功能名称: 群公告
 * 
 * @author developer-agent
 * @since 2026-03-30
 */

interface GroupAnnouncementPanelProps {
  groupId: number;
  currentUserId: number;
  isGroupOwnerOrAdmin: boolean;
}

export const GroupAnnouncementPanel: React.FC<GroupAnnouncementPanelProps> = ({
  groupId,
  currentUserId,
  isGroupOwnerOrAdmin
}) => {
  const [announcements, setAnnouncements] = useState<GroupAnnouncement[]>([]);
  const [latestAnnouncement, setLatestAnnouncement] = useState<GroupAnnouncement | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [editingAnnouncement, setEditingAnnouncement] = useState<GroupAnnouncement | null>(null);
  const [unreadCount, setUnreadCount] = useState(0);

  // 表单状态
  const [formData, setFormData] = useState<CreateAnnouncementRequest>({
    groupId,
    title: '',
    content: '',
    pinned: false,
    attachments: []
  });

  // 加载公告列表
  const loadAnnouncements = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [list, latest, unread] = await Promise.all([
        groupAnnouncementService.getGroupAnnouncements(groupId),
        groupAnnouncementService.getLatestAnnouncement(groupId),
        groupAnnouncementService.getUnreadCount(groupId)
      ]);
      setAnnouncements(list);
      setLatestAnnouncement(latest);
      setUnreadCount(unread);
    } catch (err: any) {
      setError(err.message || '加载公告失败');
    } finally {
      setLoading(false);
    }
  }, [groupId]);

  useEffect(() => {
    loadAnnouncements();
  }, [loadAnnouncements]);

  // 创建公告
  const handleCreate = async () => {
    if (!formData.title.trim() || !formData.content.trim()) {
      alert('标题和内容不能为空');
      return;
    }
    try {
      await groupAnnouncementService.createAnnouncement(formData);
      setShowCreateModal(false);
      setFormData({ groupId, title: '', content: '', pinned: false, attachments: [] });
      loadAnnouncements();
    } catch (err: any) {
      alert(err.message || '创建失败');
    }
  };

  // 更新公告
  const handleUpdate = async () => {
    if (!editingAnnouncement) return;
    if (!formData.title.trim() || !formData.content.trim()) {
      alert('标题和内容不能为空');
      return;
    }
    try {
      await groupAnnouncementService.updateAnnouncement(editingAnnouncement.id, {
        title: formData.title,
        content: formData.content,
        pinned: formData.pinned,
        attachments: formData.attachments
      });
      setEditingAnnouncement(null);
      setFormData({ groupId, title: '', content: '', pinned: false, attachments: [] });
      loadAnnouncements();
    } catch (err: any) {
      alert(err.message || '更新失败');
    }
  };

  // 删除公告
  const handleDelete = async (id: number) => {
    if (!confirm('确定要删除这条公告吗？')) return;
    try {
      await groupAnnouncementService.deleteAnnouncement(id);
      loadAnnouncements();
    } catch (err: any) {
      alert(err.message || '删除失败');
    }
  };

  // 置顶/取消置顶
  const handlePin = async (id: number, pinned: boolean) => {
    try {
      await groupAnnouncementService.pinAnnouncement(id, pinned);
      loadAnnouncements();
    } catch (err: any) {
      alert(err.message || '操作失败');
    }
  };

  // 标记已读
  const handleMarkAsRead = async (id: number) => {
    try {
      await groupAnnouncementService.markAsRead(id);
      loadAnnouncements();
    } catch (err: any) {
      console.error('标记已读失败:', err);
    }
  };

  // 批量标记已读
  const handleMarkAllAsRead = async () => {
    try {
      await groupAnnouncementService.markAllAsRead(groupId);
      loadAnnouncements();
    } catch (err: any) {
      alert(err.message || '操作失败');
    }
  };

  // 开始编辑
  const startEdit = (announcement: GroupAnnouncement) => {
    setEditingAnnouncement(announcement);
    setFormData({
      groupId,
      title: announcement.title,
      content: announcement.content,
      pinned: announcement.pinned,
      attachments: announcement.attachments || []
    });
    setShowCreateModal(true);
  };

  // 格式化时间
  const formatTime = (time: string) => {
    return new Date(time).toLocaleString('zh-CN');
  };

  // 渲染已读状态
  const renderReadStatus = (announcement: GroupAnnouncement) => {
    const percentage = Math.round(announcement.readPercentage || 0);
    return (
      <span className="read-status">
        <span className={`read-dot ${announcement.hasRead ? 'read' : 'unread'}`} />
        {announcement.hasRead ? '已读' : '未读'}
        <span className="read-count">
          ({announcement.readCount}/{announcement.totalMembers} · {percentage}%)
        </span>
        {announcement.confirmed && <span className="confirmed-badge">全员已读</span>}
      </span>
    );
  };

  return (
    <div className="group-announcement-panel">
      <div className="announcement-header">
        <h3>
          群公告
          {unreadCount > 0 && <span className="unread-badge">{unreadCount}</span>}
        </h3>
        <div className="header-actions">
          {unreadCount > 0 && (
            <button className="btn-mark-all" onClick={handleMarkAllAsRead}>
              全部已读
            </button>
          )}
          {isGroupOwnerOrAdmin && (
            <button className="btn-create" onClick={() => setShowCreateModal(true)}>
              + 发布公告
            </button>
          )}
        </div>
      </div>

      {/* 最新公告展示 */}
      {latestAnnouncement && (
        <div className="latest-announcement">
          <div className="latest-badge">最新</div>
          <div className="announcement-card pinned">
            <div className="announcement-title">{latestAnnouncement.title}</div>
            <div className="announcement-content">{latestAnnouncement.content}</div>
            {latestAnnouncement.attachments?.length > 0 && (
              <div className="announcement-attachments">
                {latestAnnouncement.attachments.map((url, idx) => (
                  <a key={idx} href={url} target="_blank" rel="noopener noreferrer">
                    附件{idx + 1}
                  </a>
                ))}
              </div>
            )}
            <div className="announcement-meta">
              <span className="creator">{latestAnnouncement.creatorNickname}</span>
              <span className="time">{formatTime(latestAnnouncement.createdAt)}</span>
              {renderReadStatus(latestAnnouncement)}
            </div>
            {!latestAnnouncement.hasRead && (
              <button 
                className="btn-read"
                onClick={() => handleMarkAsRead(latestAnnouncement.id)}
              >
                标记已读
              </button>
            )}
          </div>
        </div>
      )}

      {/* 公告列表 */}
      <div className="announcement-list">
        {loading ? (
          <div className="loading">加载中...</div>
        ) : error ? (
          <div className="error">{error}</div>
        ) : announcements.length === 0 ? (
          <div className="empty">暂无公告</div>
        ) : (
          announcements.map(announcement => (
            <div 
              key={announcement.id} 
              className={`announcement-card ${announcement.pinned ? 'pinned' : ''} ${!announcement.hasRead ? 'unread' : ''}`}
            >
              {announcement.pinned && <div className="pin-badge">置顶</div>}
              <div className="announcement-title">{announcement.title}</div>
              <div className="announcement-content">{announcement.content}</div>
              {announcement.attachments?.length > 0 && (
                <div className="announcement-attachments">
                  {announcement.attachments.map((url, idx) => (
                    <a key={idx} href={url} target="_blank" rel="noopener noreferrer">
                      附件{idx + 1}
                    </a>
                  ))}
                </div>
              )}
              <div className="announcement-meta">
                <span className="creator">{announcement.creatorNickname}</span>
                <span className="time">{formatTime(announcement.createdAt)}</span>
                {renderReadStatus(announcement)}
              </div>
              <div className="announcement-actions">
                {!announcement.hasRead && (
                  <button 
                    className="btn-read"
                    onClick={() => handleMarkAsRead(announcement.id)}
                  >
                    标记已读
                  </button>
                )}
                {isGroupOwnerOrAdmin && (
                  <>
                    <button 
                      className="btn-pin"
                      onClick={() => handlePin(announcement.id, !announcement.pinned)}
                    >
                      {announcement.pinned ? '取消置顶' : '置顶'}
                    </button>
                    {announcement.creatorId === currentUserId && (
                      <>
                        <button 
                          className="btn-edit"
                          onClick={() => startEdit(announcement)}
                        >
                          编辑
                        </button>
                        <button 
                          className="btn-delete"
                          onClick={() => handleDelete(announcement.id)}
                        >
                          删除
                        </button>
                      </>
                    )}
                  </>
                )}
              </div>
            </div>
          ))
        )}
      </div>

      {/* 创建/编辑模态框 */}
      {showCreateModal && (
        <div className="modal-overlay" onClick={() => setShowCreateModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h3>{editingAnnouncement ? '编辑公告' : '发布新公告'}</h3>
            <div className="form-group">
              <label>标题</label>
              <input
                type="text"
                value={formData.title}
                onChange={e => setFormData({ ...formData, title: e.target.value })}
                placeholder="请输入公告标题"
                maxLength={200}
              />
            </div>
            <div className="form-group">
              <label>内容</label>
              <textarea
                value={formData.content}
                onChange={e => setFormData({ ...formData, content: e.target.value })}
                placeholder="请输入公告内容"
                rows={6}
                maxLength={5000}
              />
            </div>
            <div className="form-group checkbox">
              <label>
                <input
                  type="checkbox"
                  checked={formData.pinned}
                  onChange={e => setFormData({ ...formData, pinned: e.target.checked })}
                />
                置顶此公告
              </label>
            </div>
            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => {
                setShowCreateModal(false);
                setEditingAnnouncement(null);
                setFormData({ groupId, title: '', content: '', pinned: false, attachments: [] });
              }}>
                取消
              </button>
              <button 
                className="btn-submit"
                onClick={editingAnnouncement ? handleUpdate : handleCreate}
              >
                {editingAnnouncement ? '保存' : '发布'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
