import React, { useState, useEffect } from 'react';
import './BlockchainExplorer.css';

/**
 * 区块链浏览器组件
 * 功能#69: 区块链浏览器集成
 * 
 * 核心功能:
 * - 区块浏览与搜索
 * - 交易详情查询
 * - 地址余额查看
 * - 智能合约交互
 * - 交易历史追踪
 */

interface Block {
  number: number;
  hash: string;
  timestamp: number;
  transactions: number;
  gasUsed: string;
  miner: string;
}

interface Transaction {
  hash: string;
  from: string;
  to: string;
  value: string;
  gasPrice: string;
  status: 'pending' | 'confirmed' | 'failed';
  blockNumber?: number;
}

interface AddressInfo {
  address: string;
  balance: string;
  transactionCount: number;
  isContract: boolean;
}

export const BlockchainExplorer: React.FC = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchType, setSearchType] = useState<'block' | 'tx' | 'address'>('tx');
  const [latestBlocks, setLatestBlocks] = useState<Block[]>([]);
  const [latestTransactions, setLatestTransactions] = useState<Transaction[]>([]);
  const [searchResult, setSearchResult] = useState<any>(null);
  const [loading, setLoading] = useState(false);

  // 模拟加载最新区块和交易
  useEffect(() => {
    loadLatestData();
    const interval = setInterval(loadLatestData, 15000); // 每15秒刷新
    return () => clearInterval(interval);
  }, []);

  const loadLatestData = () => {
    // 模拟数据 - 实际应该调用区块链节点API
    const mockBlocks: Block[] = Array.from({ length: 10 }, (_, i) => ({
      number: 18452300 + i,
      hash: `0x${Math.random().toString(16).substr(2, 64)}`,
      timestamp: Date.now() / 1000 - i * 12,
      transactions: Math.floor(Math.random() * 200) + 50,
      gasUsed: `${(Math.random() * 10 + 5).toFixed(2)}M`,
      miner: `0x${Math.random().toString(16).substr(2, 40)}`,
    }));

    const mockTransactions: Transaction[] = Array.from({ length: 10 }, (_, i) => ({
      hash: `0x${Math.random().toString(16).substr(2, 64)}`,
      from: `0x${Math.random().toString(16).substr(2, 40)}`,
      to: `0x${Math.random().toString(16).substr(2, 40)}`,
      value: `${(Math.random() * 10).toFixed(4)} ETH`,
      gasPrice: `${(Math.random() * 50 + 20).toFixed(0)} Gwei`,
      status: Math.random() > 0.1 ? 'confirmed' : 'pending',
      blockNumber: 18452300 + Math.floor(Math.random() * 10),
    }));

    setLatestBlocks(mockBlocks);
    setLatestTransactions(mockTransactions);
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) return;

    setLoading(true);
    
    // 模拟搜索 - 实际应该调用区块链API
    setTimeout(() => {
      if (searchType === 'tx') {
        setSearchResult({
          type: 'transaction',
          data: {
            hash: searchQuery,
            from: `0x${Math.random().toString(16).substr(2, 40)}`,
            to: `0x${Math.random().toString(16).substr(2, 40)}`,
            value: '1.5 ETH',
            gasLimit: '21000',
            gasUsed: '21000',
            gasPrice: '25 Gwei',
            status: 'confirmed',
            blockNumber: 18452300,
            timestamp: Date.now() / 1000 - 3600,
            confirmations: 12,
          }
        });
      } else if (searchType === 'address') {
        setSearchResult({
          type: 'address',
          data: {
            address: searchQuery,
            balance: '15.234 ETH',
            transactionCount: 156,
            isContract: false,
          }
        });
      } else if (searchType === 'block') {
        setSearchResult({
          type: 'block',
          data: {
            number: parseInt(searchQuery),
            hash: `0x${Math.random().toString(16).substr(2, 64)}`,
            timestamp: Date.now() / 1000 - 3600,
            transactions: 150,
            gasUsed: '12.5M',
            gasLimit: '30M',
            miner: `0x${Math.random().toString(16).substr(2, 40)}`,
          }
        });
      }
      setLoading(false);
    }, 1000);
  };

  const formatTime = (timestamp: number) => {
    const seconds = Math.floor(Date.now() / 1000 - timestamp);
    if (seconds < 60) return `${seconds}秒前`;
    if (seconds < 3600) return `${Math.floor(seconds / 60)}分钟前`;
    return `${Math.floor(seconds / 3600)}小时前`;
  };

  const truncateHash = (hash: string, length: number = 12) => {
    if (hash.length <= length * 2 + 2) return hash;
    return `${hash.substr(0, length + 2)}...${hash.substr(-length)}`;
  };

  return (
    <div className="blockchain-explorer">
      <div className="explorer-header">
        <h2>区块链浏览器</h2>
        <div className="search-container">
          <select 
            value={searchType} 
            onChange={(e) => setSearchType(e.target.value as any)}
            className="search-type-select"
          >
            <option value="tx">交易</option>
            <option value="block">区块</option>
            <option value="address">地址</option>
          </select>
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder={`搜索${searchType === 'tx' ? '交易哈希' : searchType === 'block' ? '区块高度' : '钱包地址'}...`}
            className="search-input"
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          />
          <button onClick={handleSearch} className="search-button" disabled={loading}>
            {loading ? '搜索中...' : '搜索'}
          </button>
        </div>
      </div>

      {searchResult && (
        <div className="search-result">
          <h3>搜索结果</h3>
          {searchResult.type === 'transaction' && (
            <div className="result-card">
              <h4>交易详情</h4>
              <div className="detail-row">
                <span>交易哈希:</span>
                <code>{searchResult.data.hash}</code>
              </div>
              <div className="detail-row">
                <span>状态:</span>
                <span className={`status ${searchResult.data.status}`}>
                  {searchResult.data.status === 'confirmed' ? '已确认' : '待确认'}
                </span>
              </div>
              <div className="detail-row">
                <span>区块:</span>
                <span>#{searchResult.data.blockNumber}</span>
              </div>
              <div className="detail-row">
                <span>发送方:</span>
                <code>{truncateHash(searchResult.data.from)}</code>
              </div>
              <div className="detail-row">
                <span>接收方:</span>
                <code>{truncateHash(searchResult.data.to)}</code>
              </div>
              <div className="detail-row">
                <span>金额:</span>
                <span className="value">{searchResult.data.value}</span>
              </div>
            </div>
          )}
          {searchResult.type === 'address' && (
            <div className="result-card">
              <h4>地址详情</h4>
              <div className="detail-row">
                <span>地址:</span>
                <code>{searchResult.data.address}</code>
              </div>
              <div className="detail-row">
                <span>余额:</span>
                <span className="value">{searchResult.data.balance}</span>
              </div>
              <div className="detail-row">
                <span>交易数:</span>
                <span>{searchResult.data.transactionCount}</span>
              </div>
              <div className="detail-row">
                <span>类型:</span>
                <span>{searchResult.data.isContract ? '合约地址' : '钱包地址'}</span>
              </div>
            </div>
          )}
          {searchResult.type === 'block' && (
            <div className="result-card">
              <h4>区块详情</h4>
              <div className="detail-row">
                <span>区块高度:</span>
                <span>#{searchResult.data.number}</span>
              </div>
              <div className="detail-row">
                <span>区块哈希:</span>
                <code>{truncateHash(searchResult.data.hash)}</code>
              </div>
              <div className="detail-row">
                <span>交易数:</span>
                <span>{searchResult.data.transactions}</span>
              </div>
              <div className="detail-row">
                <span>Gas使用:</span>
                <span>{searchResult.data.gasUsed} / {searchResult.data.gasLimit}</span>
              </div>
            </div>
          )}
        </div>
      )}

      <div className="explorer-content">
        <div className="latest-blocks">
          <h3>最新区块</h3>
          <div className="blocks-list">
            {latestBlocks.map((block) => (
              <div key={block.number} className="block-card">
                <div className="block-header">
                  <span className="block-number">#{block.number}</span>
                  <span className="block-time">{formatTime(block.timestamp)}</span>
                </div>
                <div className="block-info">
                  <div className="info-row">
                    <span>矿工:</span>
                    <code>{truncateHash(block.miner, 8)}</code>
                  </div>
                  <div className="info-row">
                    <span>交易:</span>
                    <span>{block.transactions}笔</span>
                  </div>
                  <div className="info-row">
                    <span>Gas:</span>
                    <span>{block.gasUsed}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="latest-transactions">
          <h3>最新交易</h3>
          <div className="transactions-list">
            {latestTransactions.map((tx) => (
              <div key={tx.hash} className="transaction-card">
                <div className="tx-header">
                  <code className="tx-hash">{truncateHash(tx.hash, 10)}</code>
                  <span className={`tx-status ${tx.status}`}>
                    {tx.status === 'confirmed' ? '已确认' : '待确认'}
                  </span>
                </div>
                <div className="tx-info">
                  <div className="info-row">
                    <span>从:</span>
                    <code>{truncateHash(tx.from, 8)}</code>
                  </div>
                  <div className="info-row">
                    <span>到:</span>
                    <code>{truncateHash(tx.to, 8)}</code>
                  </div>
                  <div className="info-row">
                    <span>金额:</span>
                    <span className="tx-value">{tx.value}</span>
                  </div>
                  <div className="info-row">
                    <span>Gas价格:</span>
                    <span>{tx.gasPrice}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default BlockchainExplorer;
