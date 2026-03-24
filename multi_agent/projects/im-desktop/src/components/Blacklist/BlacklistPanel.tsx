import React, { useState, useEffect } from 'react';
import { blockedUserService, BlockedUserDTO } from '../../services/blockedUserService';
import './BlacklistPanel.css';

interface BlacklistPanelProps {
  onClose?: () => void;
  onUnblock?: (userId: number) => void;
}

const BlacklistPanel: React.FC<BlacklistPanelProps> = ({ onClose, onUnblock }) => {
  const [blockedUsers, setBlockedUsers] = useState<BlockedUserDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [processingId, setProcessingId] = useState<number | null>(null);
  const [searchKeyword, setSearchKeyword] = useState('');

  useEffect(() => {
    loadBlockedUsers();
  }, []);

  const loadBlockedUsers = async () => {
    setLoading(true);
    try {
      const users = await blockedUserService.getBlockedUsers();
      setBlockedUsers(users);
    } catch (error) {
      console.error('加载黑名单失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleUnblock = async (blockedId: number) => {
    setProcessingId(blockedId);
    try {
      await blockedUserService.unblockUser(blockedId);
      setBlockedUsers(prev => prev.filter(u => u.blockedId !== blockedId));
      onUnblock?.(blockedId);
    } catch (error) {
      console.error('解除拉黑失败:', error);
      alert('操作失败，请重试');
    } finally {
      setProcessingId(null);
    }
  };

  const filteredUsers = blockedUsers.filter(user => {
    if (!searchKeyword) return true;
    const keyword = searchKeyword.toLowerCase();
    return (
      user.blockedUsername?.toLowerCase().includes(keyword) ||
      user.blockedId.toString().includes(keyword) ||
      user.reason?.toLowerCase().includes(keyword)
    );
  });

  if (loading) {
    return (
      <div className="blacklist-panel">
        <div className="loading">加载中...</div>
      </div>
    );
  }

  return (
    <div className="blacklist-panel">
      <div className="panel-header">
        <h3>黑名单 ({blockedUsers.length})</h3>
        {onClose && (
          <button className="close-btn" onClick={onClose}>×</button>
        )}
      </div>
      
      <div className="search-bar">
        <input
          type="text"
          placeholder="搜索黑名单用户..."
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
        />
      </div>
      
      <div className="panel-content">
        {filteredUsers.length === 0 ? (
          <div className="empty-state">
            {searchKeyword ? '没有找到匹配的用户' : '黑名单为空'}
          </div>
        ) : (
          <ul className="user-list">
            {filteredUsers.map(user => (
              <li key={user.id} className="user-item">
                <div className="user-avatar">
                  {user.blockedAvatar ? (
                    <img src={user.blockedAvatar} alt="" />
                  ) : (
                    <div className="avatar-placeholder">
                      {user.blockedUsername?.[0]?.toUpperCase() || '?'}
                    </div>
                  )}
                </div>
                <div className="user-info">
                  <div className="user-name">
                    {user.blockedUsername || `用户 ${user.blockedId}`}
                  </div>
                  {user.reason && (
                    <div className="user-reason">{user.reason}</div>
                  )}
                  <div className="user-meta">
                    <span>拉黑时间: {blockedUserService.formatBlockedTime(user.blockedAt)}</span>
                    {user.hideOnlineStatus && <span className="tag">隐藏在线状态</span>}
                    {user.muteMessages && <span className="tag">静音消息</span>}
                  </div>
                </div>
                <button
                  className="unblock-btn"
                  onClick={() => handleUnblock(user.blockedId)}
                  disabled={processingId === user.blockedId}
                >
                  {processingId === user.blockedId ? '处理中...' : '解除拉黑'}
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
};

export default BlacklistPanel;
