import React, { useState, useEffect, useCallback } from 'react';
import {
  Web3AttestationService,
  MessageAttestation,
  AttestationStatus,
  BlockchainNetwork,
  VerificationResult,
  AttestationStatistics,
  NetworkConfig
} from '../services/web3AttestationService';
import '../styles/attestation.css';

// 状态标签组件
const StatusBadge: React.FC<{ status: AttestationStatus }> = ({ status }) => {
  const getStatusConfig = () => {
    switch (status) {
      case AttestationStatus.PENDING:
        return { class: 'pending', label: '待提交' };
      case AttestationStatus.SUBMITTING:
        return { class: 'submitting', label: '提交中' };
      case AttestationStatus.CONFIRMING:
        return { class: 'confirming', label: '确认中' };
      case AttestationStatus.CONFIRMED:
        return { class: 'confirmed', label: '已确认' };
      case AttestationStatus.FAILED:
        return { class: 'failed', label: '失败' };
      default:
        return { class: 'pending', label: status };
    }
  };

  const config = getStatusConfig();
  return (
    <span className={`attestation-status-badge attestation-status-${config.class}`}>
      {config.label}
    </span>
  );
};

// 网络图标组件
const NetworkIcon: React.FC<{ network: BlockchainNetwork }> = ({ network }) => {
  const getNetworkIcon = () => {
    switch (network) {
      case BlockchainNetwork.ETHEREUM: return '⬡';
      case BlockchainNetwork.SEPOLIA: return '⬢';
      case BlockchainNetwork.POLYGON: return '◈';
      case BlockchainNetwork.BSC: return '◉';
      case BlockchainNetwork.ARBITRUM: return '◆';
      case BlockchainNetwork.OPTIMISM: return '◇';
      default: return '⬡';
    }
  };

  return (
    <span className={`network-icon network-${network.toLowerCase()}`}>
      {getNetworkIcon()}
    </span>
  );
};

// 统计卡片组件
const StatCard: React.FC<{ label: string; value: number; type?: string }> = ({ 
  label, 
  value, 
  type 
}) => (
  <div className={`stat-card ${type || ''}`}>
    <div className="stat-value">{value.toLocaleString()}</div>
    <div className="stat-label">{label}</div>
  </div>
);

// 创建存证对话框
const CreateAttestationDialog: React.FC<{
  isOpen: boolean;
  onClose: () => void;
  onCreate: (messageId: string, content: string, network: BlockchainNetwork) => void;
  defaultNetwork: BlockchainNetwork;
}> = ({ isOpen, onClose, onCreate, defaultNetwork }) => {
  const [messageId, setMessageId] = useState('');
  const [content, setContent] = useState('');
  const [selectedNetwork, setSelectedNetwork] = useState(defaultNetwork);
  const [computedHash, setComputedHash] = useState('');

  const service = Web3AttestationService.getInstance();

  useEffect(() => {
    const computeHash = async () => {
      if (content) {
        const hash = await service.computeHash(content);
        setComputedHash(hash);
      } else {
        setComputedHash('');
      }
    };
    computeHash();
  }, [content]);

  useEffect(() => {
    setSelectedNetwork(defaultNetwork);
  }, [defaultNetwork]);

  if (!isOpen) return null;

  const networks = service.getAllNetworks();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (messageId && content) {
      onCreate(messageId, content, selectedNetwork);
      setMessageId('');
      setContent('');
      onClose();
    }
  };

  return (
    <div className="dialog-overlay">
      <div className="create-attestation-dialog">
        <h3>创建消息存证</h3>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>消息ID</label>
            <input
              type="text"
              value={messageId}
              onChange={(e) => setMessageId(e.target.value)}
              placeholder="输入消息唯一标识"
              required
            />
          </div>
          <div className="form-group">
            <label>消息内容</label>
            <textarea
              value={content}
              onChange={(e) => setContent(e.target.value)}
              placeholder="输入要存证的消息内容"
              required
            />
            {computedHash && (
              <div className="hash-preview">
                SHA-256: {computedHash.slice(0, 20)}...{computedHash.slice(-8)}
              </div>
            )}
          </div>
          <div className="form-group">
            <label>区块链网络</label>
            <div className="network-selector">
              {networks.map((network) => (
                <div
                  key={network}
                  className={`network-option ${selectedNetwork === network ? 'selected' : ''}`}
                  onClick={() => setSelectedNetwork(network)}
                >
                  <NetworkIcon network={network} />
                  {network}
                </div>
              ))}
            </div>
          </div>
          <div className="form-actions">
            <button type="button" className="attestation-btn attestation-btn-secondary" onClick={onClose}>
              取消
            </button>
            <button type="submit" className="attestation-btn attestation-btn-primary">
              创建存证
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

// 验证结果组件
const VerificationResultCard: React.FC<{ result: VerificationResult | null }> = ({ result }) => {
  if (!result) return null;

  return (
    <div className={`verification-card ${result.isValid ? 'valid' : 'invalid'}`}>
      <div className="verification-header">
        <span className="verification-icon">{result.isValid ? '✅' : '❌'}</span>
        <h4 className="verification-title">
          {result.isValid ? '验证通过' : '验证失败'}
        </h4>
      </div>
      <div className="verification-details">
        <div className="verification-detail-row">
          <span className="verification-detail-label">消息ID</span>
          <span className="verification-detail-value">{result.messageId}</span>
        </div>
        <div className="verification-detail-row">
          <span className="verification-detail-label">当前哈希</span>
          <span className="verification-detail-value">{result.contentHash.slice(0, 30)}...</span>
        </div>
        <div className="verification-detail-row">
          <span className="verification-detail-label">链上哈希</span>
          <span className="verification-detail-value">{result.storedHash.slice(0, 30)}...</span>
        </div>
        <div className="verification-detail-row">
          <span className="verification-detail-label">匹配状态</span>
          <span className={`verification-detail-value ${result.matches ? 'match' : 'mismatch'}`}>
            {result.matches ? '✓ 匹配' : '✗ 不匹配'}
          </span>
        </div>
        <div className="verification-detail-row">
          <span className="verification-detail-label">区块高度</span>
          <span className="verification-detail-value">{result.blockNumber || '-'}</span>
        </div>
        <div className="verification-detail-row">
          <span className="verification-detail-label">网络</span>
          <span className="verification-detail-value">{result.network}</span>
        </div>
        <div className="verification-detail-row">
          <span className="verification-detail-label">详情</span>
          <span className="verification-detail-value">{result.details}</span>
        </div>
      </div>
    </div>
  );
};

// 主组件
const MessageAttestationPanel: React.FC = () => {
  const [attestations, setAttestations] = useState<MessageAttestation[]>([]);
  const [selectedAttestation, setSelectedAttestation] = useState<MessageAttestation | null>(null);
  const [statistics, setStatistics] = useState<AttestationStatistics | null>(null);
  const [filter, setFilter] = useState<AttestationStatus | 'ALL'>('ALL');
  const [searchTerm, setSearchTerm] = useState('');
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [verificationResult, setVerificationResult] = useState<VerificationResult | null>(null);
  const [isVerifying, setIsVerifying] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  const service = Web3AttestationService.getInstance();

  // 加载统计数据
  const loadStatistics = useCallback(async () => {
    try {
      const stats = await service.getStatistics();
      setStatistics(stats);
    } catch (e) {
      console.error('Failed to load statistics:', e);
    }
  }, []);

  // 加载存证列表
  const loadAttestations = useCallback(async () => {
    try {
      const options: any = { page: 0, size: 50 };
      if (filter !== 'ALL') {
        options.status = filter;
      }
      const result = await service.queryAttestations(options);
      setAttestations(result.items);
    } catch (e) {
      console.error('Failed to load attestations:', e);
    } finally {
      setIsLoading(false);
    }
  }, [filter]);

  // 初始加载
  useEffect(() => {
    loadStatistics();
    loadAttestations();

    // 监听更新事件
    service.on('attestationCreated', loadAttestations);
    service.on('attestationUpdated', (att: MessageAttestation) => {
      setAttestations(prev => prev.map(a => a.id === att.id ? att : a));
      if (selectedAttestation?.id === att.id) {
        setSelectedAttestation(att);
      }
      loadStatistics();
    });

    return () => {
      service.off('attestationCreated', loadAttestations);
      service.off('attestationUpdated');
    };
  }, [loadAttestations, loadStatistics, selectedAttestation]);

  // 过滤存证
  const filteredAttestations = attestations.filter(att => {
    if (searchTerm) {
      return (
        att.messageId.toLowerCase().includes(searchTerm.toLowerCase()) ||
        att.contentHash.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }
    return true;
  });

  // 创建存证
  const handleCreate = async (messageId: string, content: string, network: BlockchainNetwork) => {
    try {
      await service.createAttestation({
        messageId,
        content,
        network,
        metadata: { source: 'desktop' }
      });
      loadAttestations();
      loadStatistics();
    } catch (e) {
      console.error('Failed to create attestation:', e);
      alert(`创建存证失败: ${e}`);
    }
  };

  // 验证存证
  const handleVerify = async () => {
    if (!selectedAttestation) return;

    setIsVerifying(true);
    try {
      const result = await service.verifyAttestation(selectedAttestation.id);
      setVerificationResult(result);
    } catch (e) {
      console.error('Verification failed:', e);
      alert(`验证失败: ${e}`);
    } finally {
      setIsVerifying(false);
    }
  };

  // 导出证明
  const handleExport = () => {
    if (!selectedAttestation) return;

    const proof = service.exportAttestationProof(selectedAttestation);
    const blob = new Blob([proof], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `attestation-${selectedAttestation.id}.json`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  // 在浏览器查看
  const handleViewOnExplorer = () => {
    if (!selectedAttestation?.transactionHash) return;

    const url = service.getExplorerUrl(selectedAttestation.network, selectedAttestation.transactionHash);
    window.open(url, '_blank');
  };

  // 重试失败存证
  const handleRetry = async () => {
    if (!selectedAttestation) return;

    try {
      await service.retryAttestation(selectedAttestation.id);
      loadAttestations();
    } catch (e) {
      console.error('Retry failed:', e);
      alert(`重试失败: ${e}`);
    }
  };

  // 格式化时间
  const formatTime = (date: Date | string | undefined) => {
    if (!date) return '-';
    const d = new Date(date);
    return d.toLocaleString('zh-CN', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <div className="message-attestation-panel">
      <div className="attestation-header">
        <h2>消息存证</h2>
        <div className="attestation-actions">
          <button
            className="attestation-btn attestation-btn-primary"
            onClick={() => setIsCreateDialogOpen(true)}
          >
            ➕ 创建存证
          </button>
        </div>
      </div>

      {/* 统计面板 */}
      {statistics && (
        <div className="attestation-stats">
          <StatCard label="总计" value={statistics.totalCount} />
          <StatCard label="已确认" value={statistics.confirmedCount} type="confirmed" />
          <StatCard label="待处理" value={statistics.pendingCount} type="pending" />
          <StatCard label="失败" value={statistics.failedCount} type="failed" />
        </div>
      )}

      <div className="attestation-content">
        {/* 左侧列表 */}
        <div className="attestation-list">
          <div className="attestation-list-header">
            <input
              type="text"
              className="attestation-search"
              placeholder="搜索消息ID或哈希..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <div className="attestation-filter-tabs">
              {['ALL', 'PENDING', 'CONFIRMED', 'FAILED'].map((f) => (
                <button
                  key={f}
                  className={`filter-tab ${filter === f ? 'active' : ''}`}
                  onClick={() => setFilter(f as any)}
                >
                  {f === 'ALL' ? '全部' : 
                   f === 'PENDING' ? '待处理' :
                   f === 'CONFIRMED' ? '已确认' : '失败'}
                </button>
              ))}
            </div>
          </div>

          <div className="attestation-items">
            {isLoading ? (
              <div className="attestation-loading">
                <div className="attestation-spinner"></div>
                加载中...
              </div>
            ) : filteredAttestations.length === 0 ? (
              <div className="attestation-detail-empty">
                <div className="attestation-detail-empty-icon">📭</div>
                <div>暂无存证数据</div>
              </div>
            ) : (
              filteredAttestations.map((att) => (
                <div
                  key={att.id}
                  className={`attestation-item ${selectedAttestation?.id === att.id ? 'selected' : ''}`}
                  onClick={() => {
                    setSelectedAttestation(att);
                    setVerificationResult(null);
                  }}
                >
                  <div className="attestation-item-header">
                    <span className="attestation-item-id">{att.id.slice(0, 8)}...</span>
                    <StatusBadge status={att.status} />
                  </div>
                  <div className="attestation-item-hash">{att.contentHash.slice(0, 30)}...</div>
                  <div className="attestation-item-meta">
                    <span className="attestation-item-network">
                      <NetworkIcon network={att.network} />
                      {att.network}
                    </span>
                    <span className="attestation-item-time">{formatTime(att.createdAt)}</span>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* 右侧详情 */}
        <div className="attestation-detail">
          {!selectedAttestation ? (
            <div className="attestation-detail-empty">
              <div className="attestation-detail-empty-icon">⛓️</div>
              <div>选择一个存证查看详情</div>
            </div>
          ) : (
            <>
              <div className="attestation-detail-header">
                <div className="attestation-detail-title">
                  <h3>存证详情</h3>
                  <StatusBadge status={selectedAttestation.status} />
                </div>
                <div className="attestation-detail-actions">
                  {selectedAttestation.transactionHash && (
                    <button
                      className="attestation-icon-btn"
                      title="在区块浏览器查看"
                      onClick={handleViewOnExplorer}
                    >
                      🔗
                    </button>
                  )}
                  {selectedAttestation.status === AttestationStatus.FAILED && (
                    <button
                      className="attestation-icon-btn"
                      title="重试"
                      onClick={handleRetry}
                    >
                      🔄
                    </button>
                  )}
                  <button
                    className="attestation-icon-btn"
                    title="导出证明"
                    onClick={handleExport}
                  >
                    📥
                  </button>
                  <button
                    className="attestation-icon-btn"
                    title="验证存证"
                    onClick={handleVerify}
                    disabled={isVerifying}
                  >
                    {isVerifying ? '⏳' : '🔐'}
                  </button>
                </div>
              </div>

              {/* 基本信息 */}
              <div className="attestation-info-card">
                <h4>基本信息</h4>
                <div className="attestation-info-row">
                  <span className="attestation-info-label">存证ID</span>
                  <span className="attestation-info-value">{selectedAttestation.id}</span>
                </div>
                <div className="attestation-info-row">
                  <span className="attestation-info-label">消息ID</span>
                  <span className="attestation-info-value">{selectedAttestation.messageId}</span>
                </div>
                <div className="attestation-info-row">
                  <span className="attestation-info-label">内容哈希</span>
                  <span className="attestation-info-value hash">{selectedAttestation.contentHash}</span>
                </div>
                <div className="attestation-info-row">
                  <span className="attestation-info-label">网络</span>
                  <span className="attestation-info-value">
                    <NetworkIcon network={selectedAttestation.network} />
                    {selectedAttestation.network}
                  </span>
                </div>
                <div className="attestation-info-row">
                  <span className="attestation-info-label">存证地址</span>
                  <span className="attestation-info-value address">{selectedAttestation.attestorAddress}</span>
                </div>
                <div className="attestation-info-row">
                  <span className="attestation-info-label">创建时间</span>
                  <span className="attestation-info-value">{formatTime(selectedAttestation.createdAt)}</span>
                </div>
              </div>

              {/* 链上信息 */}
              {selectedAttestation.transactionHash && (
                <div className="attestation-info-card">
                  <h4>链上信息</h4>
                  <div className="attestation-info-row">
                    <span className="attestation-info-label">交易哈希</span>
                    <span 
                      className="attestation-info-value tx-hash"
                      onClick={handleViewOnExplorer}
                    >
                      {selectedAttestation.transactionHash}
                    </span>
                  </div>
                  <div className="attestation-info-row">
                    <span className="attestation-info-label">区块高度</span>
                    <span className="attestation-info-value">{selectedAttestation.blockNumber || '-'}</span>
                  </div>
                  <div className="attestation-info-row">
                    <span className="attestation-info-label">区块哈希</span>
                    <span className="attestation-info-value">{selectedAttestation.blockHash || '-'}</span>
                  </div>
                  <div className="attestation-info-row">
                    <span className="attestation-info-label">Gas消耗</span>
                    <span className="attestation-info-value">{selectedAttestation.gasUsed || '-'}</span>
                  </div>
                  <div className="attestation-info-row">
                    <span className="attestation-info-label">确认时间</span>
                    <span className="attestation-info-value">{formatTime(selectedAttestation.confirmedAt)}</span>
                  </div>
                </div>
              )}

              {/* Merkle证明 */}
              {selectedAttestation.merkleRoot && (
                <div className="attestation-info-card">
                  <h4>Merkle证明</h4>
                  <div className="attestation-info-row">
                    <span className="attestation-info-label">Merkle根</span>
                    <span className="attestation-info-value hash">{selectedAttestation.merkleRoot}</span>
                  </div>
                  {selectedAttestation.merkleProof && (
                    <div className="attestation-info-row">
                      <span className="attestation-info-label">证明路径</span>
                      <span className="attestation-info-value">
                        {selectedAttestation.merkleProof.length} 层
                      </span>
                    </div>
                  )}
                </div>
              )}

              {/* 验证结果 */}
              <VerificationResultCard result={verificationResult} />
            </>
          )}
        </div>
      </div>

      <CreateAttestationDialog
        isOpen={isCreateDialogOpen}
        onClose={() => setIsCreateDialogOpen(false)}
        onCreate={handleCreate}
        defaultNetwork={service.getCurrentNetwork()}
      />
    </div>
  );
};

export default MessageAttestationPanel;
