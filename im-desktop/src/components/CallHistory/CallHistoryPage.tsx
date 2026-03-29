import React, { useEffect, useState } from 'react';
import { callService, CallRecord } from '../../services/callService';
import './CallHistoryPage.css';

const CallHistoryPage: React.FC = () => {
  const [records, setRecords] = useState<CallRecord[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [filter, setFilter] = useState<'ALL' | 'MISSED'>('ALL');

  useEffect(() => {
    loadHistory();
  }, [page, filter]);

  async function loadHistory() {
    setLoading(true);
    try {
      const data = await callService.getCallHistory(page, 20);
      const filtered = filter === 'MISSED'
        ? data.content.filter(r => r.status === 'MISSED')
        : data.content;
      setRecords(filtered);
      setTotalPages(data.totalPages);
    } finally {
      setLoading(false);
    }
  }

  async function handleDelete(id: number) {
    await callService.deleteCall(id);
    setRecords(records.filter(r => r.id !== id));
  }

  function formatTime(iso: string): string {
    const d = new Date(iso);
    const now = new Date();
    const diff = now.getTime() - d.getTime();
    const days = Math.floor(diff / 86400000);
    if (days === 0) return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
    if (days === 1) return '昨天 ' + d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
    if (days < 7) return `${days}天前`;
    return d.toLocaleDateString('zh-CN');
  }

  return (
    <div className="call-history-page">
      <div className="call-history-header">
        <h2>通话记录</h2>
        <div className="filter-tabs">
          <button className={filter === 'ALL' ? 'active' : ''} onClick={() => { setFilter('ALL'); setPage(0); }}>全部</button>
          <button className={filter === 'MISSED' ? 'active' : ''} onClick={() => { setFilter('MISSED'); setPage(0); }}>未接来电</button>
        </div>
      </div>
      <div className="call-list">
        {loading ? <div className="loading">加载中...</div> : records.length === 0 ? (
          <div className="empty">暂无通话记录</div>
        ) : records.map(record => (
          <div key={record.id} className="call-item">
            <div className={`call-icon ${record.callType.toLowerCase()}`}>
              {record.callType === 'AUDIO' ? '📞' : '📹'}
            </div>
            <div className="call-info">
              <div className="call-peer">
                {record.callerName || record.callerId} → {record.calleeName || record.calleeId}
              </div>
              <div className="call-meta">
                <span className={`call-status status-${record.status.toLowerCase()}`}>
                  {callService.getStatusLabel(record.status)}
                </span>
                <span className="call-time">{formatTime(record.startTime)}</span>
                {record.duration && (
                  <span className="call-duration">{callService.formatDuration(record.duration)}</span>
                )}
              </div>
            </div>
            <button className="delete-btn" onClick={() => handleDelete(record.id)}>×</button>
          </div>
        ))}
      </div>
      {totalPages > 1 && (
        <div className="pagination">
          <button disabled={page === 0} onClick={() => setPage(p => p - 1)}>上一页</button>
          <span>{page + 1} / {totalPages}</span>
          <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)}>下一页</button>
        </div>
      )}
    </div>
  );
};

export default CallHistoryPage;
