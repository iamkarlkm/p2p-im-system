import React, { useState } from 'react';
import { announcementService } from '../../services/announcementService';
import './AnnouncementCreateModal.css';

interface Props {
  groupId: number;
  onClose: () => void;
  onCreated: () => void;
}

const AnnouncementCreateModal: React.FC<Props> = ({ groupId, onClose, onCreated }) => {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [type, setType] = useState<string>('normal');
  const [pinned, setPinned] = useState(false);
  const [urgent, setUrgent] = useState(false);
  const [requiredRead, setRequiredRead] = useState(false);
  const [expireTime, setExpireTime] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!content.trim()) return;
    setSubmitting(true);
    try {
      const storedUserId = localStorage.getItem('userId') || '0';
      await announcementService.publish({
        groupId,
        authorId: parseInt(storedUserId),
        title: title || undefined,
        content,
        type,
        pinned,
        urgent,
        requiredRead,
        expireTime: expireTime || undefined,
      });
      onCreated();
    } catch (err) {
      alert('发布失败');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <h3>发布群公告</h3>
        <form onSubmit={handleSubmit}>
          <input
            className="form-input"
            placeholder="公告标题（可选）"
            value={title}
            onChange={e => setTitle(e.target.value)}
            maxLength={200}
          />
          <textarea
            className="form-textarea"
            placeholder="公告内容（支持 Markdown）"
            value={content}
            onChange={e => setContent(e.target.value)}
            rows={6}
            required
          />
          <div className="form-row">
            <label>
              类型：
              <select value={type} onChange={e => setType(e.target.value)}>
                <option value="normal">普通公告</option>
                <option value="rule">群规</option>
                <option value="notice">通知</option>
                <option value="event">活动</option>
              </select>
            </label>
          </div>
          <div className="form-checkboxes">
            <label><input type="checkbox" checked={pinned} onChange={e => setPinned(e.target.checked)} /> 置顶</label>
            <label><input type="checkbox" checked={urgent} onChange={e => setUrgent(e.target.checked)} /> 紧急</label>
            <label><input type="checkbox" checked={requiredRead} onChange={e => setRequiredRead(e.target.checked)} /> 必须阅读</label>
          </div>
          <div className="form-row">
            <label>过期时间：<input type="datetime-local" value={expireTime} onChange={e => setExpireTime(e.target.value)} /></label>
          </div>
          <div className="form-actions">
            <button type="button" className="btn-cancel" onClick={onClose}>取消</button>
            <button type="submit" className="btn-submit" disabled={submitting || !content.trim()}>
              {submitting ? '发布中...' : '发布'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AnnouncementCreateModal;
