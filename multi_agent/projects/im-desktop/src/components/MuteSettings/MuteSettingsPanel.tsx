import React, { useState, useEffect } from 'react';
import { muteService, MuteSettingDTO } from '../../services/muteService';
import './MuteSettingsPanel.css';

interface MuteSettingsPanelProps {
  conversationId?: number;
  onClose?: () => void;
}

const DAYS = [
  { key: 'Monday', label: '周一', short: 'MON' },
  { key: 'Tuesday', label: '周二', short: 'TUE' },
  { key: 'Wednesday', label: '周三', short: 'WED' },
  { key: 'Thursday', label: '周四', short: 'THU' },
  { key: 'Friday', label: '周五', short: 'FRI' },
  { key: 'Saturday', label: '周六', short: 'SAT' },
  { key: 'Sunday', label: '周日', short: 'SUN' },
];

const MuteSettingsPanel: React.FC<MuteSettingsPanelProps> = ({ conversationId, onClose }) => {
  // 全局免打扰设置
  const [dndEnabled, setDndEnabled] = useState(false);
  const [dndStartTime, setDndStartTime] = useState('22:00');
  const [dndEndTime, setDndEndTime] = useState('08:00');
  const [dndRepeatDays, setDndRepeatDays] = useState<string[]>(['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday']);
  
  // 会话静音设置
  const [isConversationMuted, setIsConversationMuted] = useState(false);
  
  // 加载状态
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  // 加载设置
  useEffect(() => {
    loadSettings();
  }, []);

  const loadSettings = async () => {
    setLoading(true);
    try {
      // 加载全局免打扰设置
      const globalDnd = await muteService.getGlobalDnd();
      if (globalDnd) {
        setDndEnabled(globalDnd.dndEnabled);
        setDndStartTime(globalDnd.dndStartTime || '22:00');
        setDndEndTime(globalDnd.dndEndTime || '08:00');
        if (globalDnd.dndRepeatDays) {
          // 将 MON,TUE,WED 转换为 ['Monday', 'Tuesday', 'Wednesday']
          const dayMap: Record<string, string> = {
            'MON': 'Monday',
            'TUE': 'Tuesday',
            'WED': 'Wednesday',
            'THU': 'Thursday',
            'FRI': 'Friday',
            'SAT': 'Saturday',
            'SUN': 'Sunday',
          };
          setDndRepeatDays(
            globalDnd.dndRepeatDays.split(',').map(day => dayMap[day] || day)
          );
        }
      }
      
      // 如果有会话ID，加载会话静音设置
      if (conversationId) {
        const muted = await muteService.isConversationMuted(conversationId);
        setIsConversationMuted(muted);
      }
    } catch (error) {
      console.error('加载免打扰设置失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 保存全局免打扰设置
  const handleSaveDnd = async () => {
    setSaving(true);
    try {
      const request = {
        dndEnabled,
        dndStartTime,
        dndEndTime,
        dndRepeatDays: muteService.parseDndRepeatDays(dndRepeatDays),
      };
      await muteService.setGlobalDnd(request);
      alert('免打扰设置已保存');
    } catch (error) {
      console.error('保存免打扰设置失败:', error);
      alert('保存失败，请重试');
    } finally {
      setSaving(false);
    }
  };

  // 切换会话静音
  const handleToggleConversationMute = async () => {
    if (!conversationId) return;
    
    setSaving(true);
    try {
      if (isConversationMuted) {
        await muteService.unmuteConversation(conversationId);
        setIsConversationMuted(false);
      } else {
        await muteService.muteConversation(conversationId);
        setIsConversationMuted(true);
      }
    } catch (error) {
      console.error('切换静音状态失败:', error);
      alert('操作失败，请重试');
    } finally {
      setSaving(false);
    }
  };

  // 切换重复日期
  const handleToggleDay = (day: string) => {
    setDndRepeatDays(prev => 
      prev.includes(day) 
        ? prev.filter(d => d !== day)
        : [...prev, day]
    );
  };

  // 全选工作日
  const handleSelectWeekdays = () => {
    setDndRepeatDays(['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday']);
  };

  // 全选周末
  const handleSelectWeekends = () => {
    setDndRepeatDays(['Saturday', 'Sunday']);
  };

  // 全选所有
  const handleSelectAll = () => {
    setDndRepeatDays(['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']);
  };

  if (loading) {
    return (
      <div className="mute-settings-panel">
        <div className="loading">加载中...</div>
      </div>
    );
  }

  return (
    <div className="mute-settings-panel">
      <div className="panel-header">
        <h3>消息通知设置</h3>
        {onClose && (
          <button className="close-btn" onClick={onClose}>×</button>
        )}
      </div>
      
      <div className="panel-content">
        {/* 会话静音 */}
        {conversationId && (
          <div className="setting-section">
            <h4>会话静音</h4>
            <div className="setting-item">
              <span className="setting-label">静音此会话</span>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={isConversationMuted}
                  onChange={handleToggleConversationMute}
                  disabled={saving}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>
            <p className="setting-hint">
              开启后，该会话的新消息不会触发通知提醒
            </p>
          </div>
        )}
        
        {/* 全局免打扰 */}
        <div className="setting-section">
          <h4>全局免打扰</h4>
          <div className="setting-item">
            <span className="setting-label">启用免打扰</span>
            <label className="toggle-switch">
              <input
                type="checkbox"
                checked={dndEnabled}
                onChange={(e) => setDndEnabled(e.target.checked)}
                disabled={saving}
              />
              <span className="toggle-slider"></span>
            </label>
          </div>
          
          {dndEnabled && (
            <>
              <div className="time-range">
                <div className="time-input-group">
                  <label>开始时间</label>
                  <input
                    type="time"
                    value={dndStartTime}
                    onChange={(e) => setDndStartTime(e.target.value)}
                  />
                </div>
                <span className="time-separator">至</span>
                <div className="time-input-group">
                  <label>结束时间</label>
                  <input
                    type="time"
                    value={dndEndTime}
                    onChange={(e) => setDndEndTime(e.target.value)}
                  />
                </div>
              </div>
              
              <div className="repeat-days">
                <label>重复周期</label>
                <div className="day-buttons">
                  <button onClick={handleSelectWeekdays}>工作日</button>
                  <button onClick={handleSelectWeekends}>周末</button>
                  <button onClick={handleSelectAll}>每天</button>
                </div>
                <div className="day-selector">
                  {DAYS.map(day => (
                    <button
                      key={day.key}
                      className={`day-btn ${dndRepeatDays.includes(day.key) ? 'selected' : ''}`}
                      onClick={() => handleToggleDay(day.key)}
                    >
                      {day.label}
                    </button>
                  ))}
                </div>
              </div>
            </>
          )}
          
          <p className="setting-hint">
            开启免打扰后，在指定时间段内收到的新消息不会触发通知
          </p>
        </div>
        
        <button 
          className="save-btn" 
          onClick={handleSaveDnd}
          disabled={saving}
        >
          {saving ? '保存中...' : '保存设置'}
        </button>
      </div>
    </div>
  );
};

export default MuteSettingsPanel;
