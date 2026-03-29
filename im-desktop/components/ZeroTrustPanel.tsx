/**
 * 零信任安全面板组件
 * ZeroTrustPanel
 * 
 * 桌面端零信任认证界面，包含：
 * - 设备状态和信任评分展示
 * - 合规性检查结果
 * - 访问请求历史
 * - 隔离状态管理
 */

import React, { useState, useEffect, useCallback } from 'react';
import {
  zeroTrustAuthService,
  DeviceInfo,
  DeviceTrustState,
  AccessRequest,
  ComplianceCheck,
  RiskLevel,
  IsolationStatus,
  AccessStatus
} from '../services/zeroTrustAuthService';
import '../styles/zeroTrust.css';

// ============================================================================
// 类型定义
// ============================================================================

interface ZeroTrustPanelProps {
  onClose?: () => void;
  onAccessGranted?: (request: AccessRequest) => void;
  onMFARequired?: (request: AccessRequest) => void;
}

interface TrustScoreRingProps {
  score: number;
  label: string;
  size?: number;
}

interface ComplianceCardProps {
  check: ComplianceCheck;
}

interface AccessRequestItemProps {
  request: AccessRequest;
  onClick?: (request: AccessRequest) => void;
}

// ============================================================================
// 信任评分环形组件
// ============================================================================

const TrustScoreRing: React.FC<TrustScoreRingProps> = ({ score, label, size = 120 }) => {
  const radius = (size - 12) / 2;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (score / 100) * circumference;
  
  const getScoreColor = (s: number): string => {
    if (s >= 80) return '#4CAF50';
    if (s >= 60) return '#FFC107';
    if (s >= 40) return '#FF9800';
    return '#F44336';
  };

  const color = getScoreColor(score);

  return (
    <div className="zt-score-ring-container" style={{ width: size, height: size }}>
      <svg className="zt-score-ring" width={size} height={size}>
        <circle
          className="zt-score-ring-bg"
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke="#2a2a2a"
          strokeWidth={8}
        />
        <circle
          className="zt-score-ring-progress"
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke={color}
          strokeWidth={8}
          strokeLinecap="round"
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          style={{ transform: 'rotate(-90deg)', transformOrigin: '50% 50%' }}
        />
      </svg>
      <div className="zt-score-content">
        <span className="zt-score-value" style={{ color }}>{score}</span>
        <span className="zt-score-label">{label}</span>
      </div>
    </div>
  );
};

// ============================================================================
// 合规性检查卡片组件
// ============================================================================

const ComplianceCard: React.FC<ComplianceCardProps> = ({ check }) => {
  const getStatusIcon = (status: string): string => {
    switch (status) {
      case 'PASS': return '✓';
      case 'FAIL': return '✗';
      case 'WARNING': return '!';
      default: return '?';
    }
  };

  const getStatusClass = (status: string): string => {
    switch (status) {
      case 'PASS': return 'zt-status-pass';
      case 'FAIL': return 'zt-status-fail';
      case 'WARNING': return 'zt-status-warning';
      default: return 'zt-status-unknown';
    }
  };

  return (
    <div className={`zt-compliance-card ${getStatusClass(check.status)}`}>
      <div className="zt-compliance-icon">{getStatusIcon(check.status)}</div>
      <div className="zt-compliance-info">
        <span className="zt-compliance-name">{check.checkName}</span>
        <span className="zt-compliance-details">{check.details}</span>
        <span className="zt-compliance-time">
          {new Date(check.lastChecked).toLocaleTimeString()}
        </span>
      </div>
    </div>
  );
};

// ============================================================================
// 访问请求列表项组件
// ============================================================================

const AccessRequestItem: React.FC<AccessRequestItemProps> = ({ request, onClick }) => {
  const getStatusClass = (status: AccessStatus): string => {
    switch (status) {
      case 'ALLOWED': return 'zt-access-allowed';
      case 'DENIED': return 'zt-access-denied';
      case 'MFA_REQUIRED': return 'zt-access-mfa';
      default: return 'zt-access-pending';
    }
  };

  const getStatusText = (status: AccessStatus): string => {
    switch (status) {
      case 'ALLOWED': return '已授权';
      case 'DENIED': return '已拒绝';
      case 'MFA_REQUIRED': return '需MFA';
      default: return '处理中';
    }
  };

  const getRiskClass = (level: RiskLevel): string => {
    switch (level) {
      case 'CRITICAL':
      case 'HIGH': return 'zt-risk-high';
      case 'MEDIUM': return 'zt-risk-medium';
      case 'LOW':
      case 'MINIMAL': return 'zt-risk-low';
      default: return 'zt-risk-unknown';
    }
  };

  return (
    <div className="zt-access-item" onClick={() => onClick?.(request)}>
      <div className="zt-access-header">
        <span className="zt-access-resource">{request.resourceName}</span>
        <span className={`zt-access-status ${getStatusClass(request.accessStatus)}`}>
          {getStatusText(request.accessStatus)}
        </span>
      </div>
      <div className="zt-access-details">
        <span className="zt-access-action">{request.action}</span>
        <span className={`zt-access-risk ${getRiskClass(request.riskLevel)}`}>
          风险: {request.riskScore}
        </span>
      </div>
      <div className="zt-access-meta">
        <span>{new Date(request.createdAt).toLocaleString()}</span>
        {request.decisionReason && (
          <span className="zt-access-reason">{request.decisionReason}</span>
        )}
      </div>
    </div>
  );
};

// ============================================================================
// 主面板组件
// ============================================================================

export const ZeroTrustPanel: React.FC<ZeroTrustPanelProps> = ({
  onClose,
  onAccessGranted,
  onMFARequired
}) => {
  const [deviceInfo, setDeviceInfo] = useState<DeviceInfo | null>(null);
  const [trustState, setTrustState] = useState<DeviceTrustState | null>(null);
  const [accessHistory, setAccessHistory] = useState<AccessRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'overview' | 'compliance' | 'history'>('overview');
  const [showIsolateConfirm, setShowIsolateConfirm] = useState(false);
  const [isolateReason, setIsolateReason] = useState('');

  // 初始化数据
  useEffect(() => {
    const init = async () => {
      setLoading(true);
      
      // 获取设备信息
      const device = zeroTrustAuthService.getDeviceInfo();
      setDeviceInfo(device);
      
      // 获取信任状态
      const state = await zeroTrustAuthService.refreshDeviceTrustState();
      setTrustState(state);
      
      // 获取访问历史
      const history = await zeroTrustAuthService.getAccessHistory(20);
      setAccessHistory(history);
      
      setLoading(false);
    };

    init();

    // 设置事件监听
    const handleTrustUpdate = (state: DeviceTrustState) => setTrustState(state);
    const handleAccessRequest = (request: AccessRequest) => {
      setAccessHistory(prev => [request, ...prev].slice(0, 100));
      onAccessGranted?.(request);
    };
    const handleMFARequired = (request: AccessRequest) => {
      onMFARequired?.(request);
    };

    zeroTrustAuthService.on('trustStateUpdated', handleTrustUpdate);
    zeroTrustAuthService.on('accessRequested', handleAccessRequest);
    zeroTrustAuthService.on('mfaRequired', handleMFARequired);

    // 定时刷新
    const refreshInterval = setInterval(() => {
      zeroTrustAuthService.refreshDeviceTrustState();
    }, 30000);

    return () => {
      zeroTrustAuthService.off('trustStateUpdated', handleTrustUpdate);
      zeroTrustAuthService.off('accessRequested', handleAccessRequest);
      zeroTrustAuthService.off('mfaRequired', handleMFARequired);
      clearInterval(refreshInterval);
    };
  }, [onAccessGranted, onMFARequired]);

  // 隔离设备
  const handleIsolate = useCallback(async () => {
    if (!isolateReason.trim()) return;
    
    const success = await zeroTrustAuthService.isolateDevice(isolateReason);
    if (success) {
      setShowIsolateConfirm(false);
      setIsolateReason('');
      await zeroTrustAuthService.refreshDeviceTrustState();
    }
  }, [isolateReason]);

  // 解除隔离
  const handleUnisolate = useCallback(async () => {
    const success = await zeroTrustAuthService.unisolateDevice();
    if (success) {
      await zeroTrustAuthService.refreshDeviceTrustState();
    }
  }, []);

  // 刷新数据
  const handleRefresh = useCallback(async () => {
    setLoading(true);
    const state = await zeroTrustAuthService.refreshDeviceTrustState();
    setTrustState(state);
    const history = await zeroTrustAuthService.getAccessHistory(20);
    setAccessHistory(history);
    setLoading(false);
  }, []);

  // 获取隔离状态样式
  const getIsolationClass = (status: IsolationStatus): string => {
    switch (status) {
      case 'CLEAN': return 'zt-isolation-clean';
      case 'QUARANTINE': return 'zt-isolation-quarantine';
      case 'ISOLATED': return 'zt-isolation-isolated';
      case 'RESTRICTED': return 'zt-isolation-restricted';
      default: return 'zt-isolation-unknown';
    }
  };

  const getIsolationText = (status: IsolationStatus): string => {
    switch (status) {
      case 'CLEAN': return '正常';
      case 'QUARANTINE': return '隔离观察';
      case 'ISOLATED': return '已隔离';
      case 'RESTRICTED': return '受限访问';
      default: return '未知';
    }
  };

  if (loading) {
    return (
      <div className="zt-panel zt-loading">
        <div className="zt-spinner"></div>
        <span>加载零信任安全状态...</span>
      </div>
    );
  }

  return (
    <div className="zt-panel">
      {/* 面板头部 */}
      <div className="zt-panel-header">
        <div className="zt-header-title">
          <span className="zt-shield-icon">🛡️</span>
          <h2>零信任安全中心</h2>
        </div>
        <div className="zt-header-actions">
          <button className="zt-btn-icon" onClick={handleRefresh} title="刷新">
            🔄
          </button>
          {onClose && (
            <button className="zt-btn-icon" onClick={onClose} title="关闭">
              ✕
            </button>
          )}
        </div>
      </div>

      {/* 设备信息栏 */}
      {deviceInfo && (
        <div className="zt-device-bar">
          <div className="zt-device-info">
            <span className="zt-device-name">{deviceInfo.deviceName}</span>
            <span className="zt-device-type">{deviceInfo.deviceType}</span>
            <span className={`zt-isolation-badge ${getIsolationClass(deviceInfo.isolationStatus)}`}>
              {getIsolationText(deviceInfo.isolationStatus)}
            </span>
          </div>
          <div className="zt-device-meta">
            <span>{deviceInfo.osType} {deviceInfo.osVersion}</span>
            <span>最后活跃: {new Date(deviceInfo.lastSeen).toLocaleString()}</span>
          </div>
        </div>
      )}

      {/* 标签导航 */}
      <div className="zt-tabs">
        <button
          className={`zt-tab ${activeTab === 'overview' ? 'zt-tab-active' : ''}`}
          onClick={() => setActiveTab('overview')}
        >
          总览
        </button>
        <button
          className={`zt-tab ${activeTab === 'compliance' ? 'zt-tab-active' : ''}`}
          onClick={() => setActiveTab('compliance')}
        >
          合规检查
        </button>
        <button
          className={`zt-tab ${activeTab === 'history' ? 'zt-tab-active' : ''}`}
          onClick={() => setActiveTab('history')}
        >
          访问历史
        </button>
      </div>

      {/* 内容区域 */}
      <div className="zt-content">
        {/* 总览标签 */}
        {activeTab === 'overview' && trustState && (
          <div className="zt-overview">
            <div className="zt-scores-row">
              <TrustScoreRing score={trustState.trustScore} label="信任评分" />
              <TrustScoreRing score={trustState.healthScore} label="健康评分" />
            </div>

            <div className="zt-status-cards">
              <div className="zt-status-card">
                <span className="zt-status-label">加密状态</span>
                <span className={`zt-status-value ${trustState.encryptionEnabled ? 'zt-status-ok' : 'zt-status-fail'}`}>
                  {trustState.encryptionEnabled ? '已启用' : '未启用'}
                </span>
              </div>
              <div className="zt-status-card">
                <span className="zt-status-label">防火墙</span>
                <span className={`zt-status-value ${trustState.firewallEnabled ? 'zt-status-ok' : 'zt-status-fail'}`}>
                  {trustState.firewallEnabled ? '已启用' : '未启用'}
                </span>
              </div>
              <div className="zt-status-card">
                <span className="zt-status-label">杀毒软件</span>
                <span className={`zt-status-value ${trustState.antivirusEnabled ? 'zt-status-ok' : 'zt-status-fail'}`}>
                  {trustState.antivirusEnabled ? '已启用' : '未启用'}
                </span>
              </div>
              <div className="zt-status-card">
                <span className="zt-status-label">漏洞数量</span>
                <span className={`zt-status-value ${trustState.vulnerabilities.length === 0 ? 'zt-status-ok' : 'zt-status-warning'}`}>
                  {trustState.vulnerabilities.length} 个
                </span>
              </div>
            </div>

            {trustState.vulnerabilities.length > 0 && (
              <div className="zt-vulnerabilities">
                <h4>⚠️ 检测到的漏洞</h4>
                <ul>
                  {trustState.vulnerabilities.map((vuln, idx) => (
                    <li key={idx}>{vuln}</li>
                  ))}
                </ul>
              </div>
            )}

            {/* 隔离控制 */}
            <div className="zt-isolation-controls">
              {trustState.isolationStatus === 'CLEAN' ? (
                <button
                  className="zt-btn-danger"
                  onClick={() => setShowIsolateConfirm(true)}
                >
                  隔离此设备
                </button>
              ) : (
                <button
                  className="zt-btn-primary"
                  onClick={handleUnisolate}
                >
                  解除隔离
                </button>
              )}
            </div>
          </div>
        )}

        {/* 合规检查标签 */}
        {activeTab === 'compliance' && trustState && (
          <div className="zt-compliance-list">
            <h3>合规性检查项</h3>
            <div className="zt-compliance-grid">
              {trustState.complianceChecks.map((check, idx) => (
                <ComplianceCard key={idx} check={check} />
              ))}
            </div>
            
            <div className="zt-assessment-info">
              <p>上次评估: {new Date(trustState.lastAssessmentTime).toLocaleString()}</p>
              <p>下次评估: {new Date(trustState.nextAssessmentTime).toLocaleString()}</p>
            </div>
          </div>
        )}

        {/* 访问历史标签 */}
        {activeTab === 'history' && (
          <div className="zt-history-list">
            <h3>最近访问请求</h3>
            {accessHistory.length === 0 ? (
              <div className="zt-empty-state">暂无访问记录</div>
            ) : (
              <div className="zt-access-list">
                {accessHistory.map((request) => (
                  <AccessRequestItem
                    key={request.requestId}
                    request={request}
                    onClick={(req) => console.log('Access request clicked:', req)}
                  />
                ))}
              </div>
            )}
          </div>
        )}
      </div>

      {/* 隔离确认对话框 */}
      {showIsolateConfirm && (
        <div className="zt-modal-overlay">
          <div className="zt-modal">
            <h3>⚠️ 确认隔离设备</h3>
            <p>隔离设备将限制其对网络资源的访问。请提供隔离原因：</p>
            <textarea
              className="zt-textarea"
              value={isolateReason}
              onChange={(e) => setIsolateReason(e.target.value)}
              placeholder="输入隔离原因..."
              rows={3}
            />
            <div className="zt-modal-actions">
              <button
                className="zt-btn-secondary"
                onClick={() => setShowIsolateConfirm(false)}
              >
                取消
              </button>
              <button
                className="zt-btn-danger"
                onClick={handleIsolate}
                disabled={!isolateReason.trim()}
              >
                确认隔离
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

// ============================================================================
// MFA验证对话框组件
// ============================================================================

interface MFADialogProps {
  request: AccessRequest;
  onVerify: (code: string) => void;
  onCancel: () => void;
}

export const MFADialog: React.FC<MFADialogProps> = ({ request, onVerify, onCancel }) => {
  const [code, setCode] = useState('');
  const [countdown, setCountdown] = useState(300); // 5分钟倒计时

  useEffect(() => {
    const timer = setInterval(() => {
      setCountdown(c => {
        if (c <= 1) {
          onCancel();
          return 0;
        }
        return c - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [onCancel]);

  const formatTime = (seconds: number): string => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="zt-modal-overlay">
      <div className="zt-modal zt-mfa-modal">
        <h3>🔐 多因素认证</h3>
        <div className="zt-mfa-info">
          <p>访问资源: <strong>{request.resourceName}</strong></p>
          <p>风险评分: <strong>{request.riskScore}</strong></p>
          <p>剩余时间: <strong className="zt-countdown">{formatTime(countdown)}</strong></p>
        </div>
        
        {request.mfaMethods && request.mfaMethods.length > 0 && (
          <div className="zt-mfa-methods">
            <p>可用验证方式:</p>
            <div className="zt-mfa-badges">
              {request.mfaMethods.map((method, idx) => (
                <span key={idx} className="zt-mfa-badge">{method}</span>
              ))}
            </div>
          </div>
        )}

        <div className="zt-mfa-input-group">
          <input
            type="text"
            className="zt-input"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            placeholder="输入验证码"
            maxLength={6}
          />
        </div>

        <div className="zt-modal-actions">
          <button className="zt-btn-secondary" onClick={onCancel}>
            取消
          </button>
          <button
            className="zt-btn-primary"
            onClick={() => onVerify(code)}
            disabled={code.length < 4}
          >
            验证
          </button>
        </div>
      </div>
    </div>
  );
};

export default ZeroTrustPanel;
