/**
 * Web3消息存证服务
 * 提供消息哈希上链、存证查询、链上验证功能
 */

import { EventEmitter } from 'events';

// 存证状态枚举
export enum AttestationStatus {
  PENDING = 'PENDING',           // 待提交
  SUBMITTING = 'SUBMITTING',     // 提交中
  CONFIRMING = 'CONFIRMING',     // 确认中
  CONFIRMED = 'CONFIRMED',       // 已确认
  FAILED = 'FAILED'              // 失败
}

// 支持的网络
export enum BlockchainNetwork {
  ETHEREUM = 'ETHEREUM',
  SEPOLIA = 'SEPOLIA',
  POLYGON = 'POLYGON',
  BSC = 'BSC',
  ARBITRUM = 'ARBITRUM',
  OPTIMISM = 'OPTIMISM'
}

// 网络配置
export interface NetworkConfig {
  name: string;
  chainId: number;
  rpcUrl: string;
  explorerUrl: string;
  symbol: string;
  decimals: number;
}

// 消息存证数据
export interface MessageAttestation {
  id: string;
  messageId: string;
  contentHash: string;
  transactionHash?: string;
  blockNumber?: number;
  blockHash?: string;
  network: BlockchainNetwork;
  status: AttestationStatus;
  gasUsed?: number;
  gasPrice?: string;
  attestorAddress: string;
  merkleRoot?: string;
  merkleProof?: string[];
  metadata?: Record<string, any>;
  createdAt: Date;
  updatedAt: Date;
  confirmedAt?: Date;
  retryCount: number;
  errorMessage?: string;
}

// 存证创建请求
export interface CreateAttestationRequest {
  messageId: string;
  content: string;
  network?: BlockchainNetwork;
  metadata?: Record<string, any>;
}

// 存证查询选项
export interface AttestationQueryOptions {
  status?: AttestationStatus;
  network?: BlockchainNetwork;
  startDate?: Date;
  endDate?: Date;
  page?: number;
  size?: number;
}

// 验证结果
export interface VerificationResult {
  isValid: boolean;
  attestationId: string;
  messageId: string;
  contentHash: string;
  storedHash: string;
  matches: boolean;
  blockNumber?: number;
  timestamp?: Date;
  network: BlockchainNetwork;
  transactionHash?: string;
  merkleProofValid?: boolean;
  details: string;
}

// 统计信息
export interface AttestationStatistics {
  totalCount: number;
  pendingCount: number;
  confirmedCount: number;
  failedCount: number;
  byNetwork: Record<BlockchainNetwork, number>;
  recentActivity: MessageAttestation[];
}

// 存证列表结果
export interface AttestationListResult {
  items: MessageAttestation[];
  total: number;
  page: number;
  size: number;
  totalPages: number;
}

/**
 * Web3存证服务类
 */
export class Web3AttestationService extends EventEmitter {
  private static instance: Web3AttestationService;
  private apiUrl: string = '';
  private pollingInterval: number = 5000;
  private pollingTimers: Map<string, NodeJS.Timeout> = new Map();
  private currentNetwork: BlockchainNetwork = BlockchainNetwork.SEPOLIA;
  
  // 网络配置映射
  private networkConfigs: Map<BlockchainNetwork, NetworkConfig> = new Map([
    [BlockchainNetwork.ETHEREUM, {
      name: 'Ethereum Mainnet',
      chainId: 1,
      rpcUrl: 'https://eth-mainnet.g.alchemy.com/v2/demo',
      explorerUrl: 'https://etherscan.io',
      symbol: 'ETH',
      decimals: 18
    }],
    [BlockchainNetwork.SEPOLIA, {
      name: 'Sepolia Testnet',
      chainId: 11155111,
      rpcUrl: 'https://eth-sepolia.g.alchemy.com/v2/demo',
      explorerUrl: 'https://sepolia.etherscan.io',
      symbol: 'ETH',
      decimals: 18
    }],
    [BlockchainNetwork.POLYGON, {
      name: 'Polygon Mainnet',
      chainId: 137,
      rpcUrl: 'https://polygon-rpc.com',
      explorerUrl: 'https://polygonscan.com',
      symbol: 'MATIC',
      decimals: 18
    }],
    [BlockchainNetwork.BSC, {
      name: 'BNB Smart Chain',
      chainId: 56,
      rpcUrl: 'https://bsc-dataseed.binance.org',
      explorerUrl: 'https://bscscan.com',
      symbol: 'BNB',
      decimals: 18
    }],
    [BlockchainNetwork.ARBITRUM, {
      name: 'Arbitrum One',
      chainId: 42161,
      rpcUrl: 'https://arb1.arbitrum.io/rpc',
      explorerUrl: 'https://arbiscan.io',
      symbol: 'ETH',
      decimals: 18
    }],
    [BlockchainNetwork.OPTIMISM, {
      name: 'Optimism',
      chainId: 10,
      rpcUrl: 'https://mainnet.optimism.io',
      explorerUrl: 'https://optimistic.etherscan.io',
      symbol: 'ETH',
      decimals: 18
    }]
  ]);

  private constructor() {
    super();
    this.loadConfig();
  }

  static getInstance(): Web3AttestationService {
    if (!Web3AttestationService.instance) {
      Web3AttestationService.instance = new Web3AttestationService();
    }
    return Web3AttestationService.instance;
  }

  private loadConfig(): void {
    const config = localStorage.getItem('web3AttestationConfig');
    if (config) {
      try {
        const parsed = JSON.parse(config);
        this.apiUrl = parsed.apiUrl || '';
        this.pollingInterval = parsed.pollingInterval || 5000;
        this.currentNetwork = parsed.currentNetwork || BlockchainNetwork.SEPOLIA;
      } catch (e) {
        console.error('Failed to load Web3 attestation config:', e);
      }
    }
  }

  private saveConfig(): void {
    localStorage.setItem('web3AttestationConfig', JSON.stringify({
      apiUrl: this.apiUrl,
      pollingInterval: this.pollingInterval,
      currentNetwork: this.currentNetwork
    }));
  }

  /**
   * 设置API地址
   */
  setApiUrl(url: string): void {
    this.apiUrl = url;
    this.saveConfig();
  }

  /**
   * 获取API地址
   */
  getApiUrl(): string {
    return this.apiUrl;
  }

  /**
   * 设置当前网络
   */
  setCurrentNetwork(network: BlockchainNetwork): void {
    this.currentNetwork = network;
    this.saveConfig();
    this.emit('networkChanged', network);
  }

  /**
   * 获取当前网络
   */
  getCurrentNetwork(): BlockchainNetwork {
    return this.currentNetwork;
  }

  /**
   * 获取网络配置
   */
  getNetworkConfig(network: BlockchainNetwork): NetworkConfig | undefined {
    return this.networkConfigs.get(network);
  }

  /**
   * 获取所有网络配置
   */
  getAllNetworks(): BlockchainNetwork[] {
    return Array.from(this.networkConfigs.keys());
  }

  /**
   * 创建消息存证
   */
  async createAttestation(request: CreateAttestationRequest): Promise<MessageAttestation> {
    if (!this.apiUrl) {
      throw new Error('API URL not configured');
    }

    const response = await fetch(`${this.apiUrl}/api/attestation/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        ...request,
        network: request.network || this.currentNetwork
      })
    });

    if (!response.ok) {
      const error = await response.text();
      throw new Error(`Failed to create attestation: ${error}`);
    }

    const attestation = await response.json();
    this.startPolling(attestation.id);
    this.emit('attestationCreated', attestation);
    return attestation;
  }

  /**
   * 批量创建存证
   */
  async batchCreateAttestations(requests: CreateAttestationRequest[]): Promise<MessageAttestation[]> {
    if (!this.apiUrl) {
      throw new Error('API URL not configured');
    }

    const response = await fetch(`${this.apiUrl}/api/attestation/batch-create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        requests: requests.map(r => ({
          ...r,
          network: r.network || this.currentNetwork
        }))
      })
    });

    if (!response.ok) {
      const error = await response.text();
      throw new Error(`Failed to batch create attestations: ${error}`);
    }

    const attestations = await response.json();
    attestations.forEach((att: MessageAttestation) => {
      this.startPolling(att.id);
    });
    this.emit('attestationsBatchCreated', attestations);
    return attestations;
  }

  /**
   * 获取存证详情
   */
  async getAttestation(id: string): Promise<MessageAttestation | null> {
    if (!this.apiUrl) {
      throw new Error('API URL not configured');
    }

    const response = await fetch(`${this.apiUrl}/api/attestation/${id}`);
    
    if (response.status === 404) {
      return null;
    }

    if (!response.ok) {
      throw new Error(`Failed to get attestation: ${response.statusText}`);
    }

    return await response.json();
  }

  /**
   * 按消息ID查询存证
   */
  async getAttestationByMessageId(messageId: string): Promise<MessageAttestation | null> {
    if (!this.apiUrl) {
      throw new Error('API URL not configured');
    }

    const response = await fetch(`${this.apiUrl}/api/attestation/message/${messageId}`);
    
    if (response.status === 404) {
      return null;
    }

    if (!response.ok) {
      throw new Error(`Failed to get attestation by message ID: ${response.statusText}`);
    }

    return await response.json();
  }

  /**
   * 按交易哈希查询存证
   */
  async getAttestationByTxHash(txHash: string): Promise<MessageAttestation | null> {
    if (!this.apiUrl) {
      throw new Error('API URL not configured');
    }

    const response = await fetch(`${this.apiUrl}/api/attestation/tx/${txHash}`);
    
    if (response.status === 404) {
      return null;
    }

    if (!response.ok) {
      throw new Error(`Failed to get attestation by tx hash: ${response.statusText}`);
    }

    return await response.json();
  }

  /**
   * 查询存证列表
   */
  async queryAttestations(options: AttestationQueryOptions = {}): Promise<AttestationListResult> {
    if (!this.apiUrl) {
      throw new Error('API URL not configured');
    }

    const params = new URLSearchParams();
    if (options.status) params.append('status', options.status);
    if (options.network) params.append('network', options.network);
    if (options.startDate) params.append('startDate', options.startDate.toISOString());
    if (options.endDate) params.append('endDate', options.endDate.toISOString());
    if (options.page !== undefined) params.append('page', options.page.toString());
    if (options.size !== undefined) params.append('size', options.size.toString());

    const response = await fetch(`${this.apiUrl}/api/attestation/list?${params}`);

    if (!response.ok) {
      throw new Error(`Failed to query attestations: ${response.statusText}`);
    }

    return await response.json();
  }

  /**
   * 验证存证
   */
  async verifyAttestation(id: string): Promise<VerificationResult> {
    if (!this.apiUrl) {
      throw new Error('API URL not configured');
    }

    const response = await fetch(`${this.apiUrl}/api/attestation/${id}/verify`, {
      method: 'POST'
    });

    if (!response.ok) {
      throw new Error(`Failed to verify attestation: ${response.statusText}`);
    }

    const result = await response.json();
    this.emit('attestationVerified', result);
    return result;
  }

  /**
   * 批量验证存证
   */
  async batchVerifyAttestations(ids: string[]): Promise<VerificationResult[]> {
    const results: VerificationResult[] = [];
    for (const id of ids) {
      try {
        const result = await this.verifyAttestation(id);
        results.push(result);
      } catch (e) {
        console.error(`Failed to verify attestation ${id}:`, e);
        results.push({
          isValid: false,
          attestationId: id,
          messageId: '',
          contentHash: '',
          storedHash: '',
          matches: false,
          network: this.currentNetwork,
          details: `Verification failed: ${e}`
        });
      }
    }
    return results;
  }

  /**
   * 获取统计信息
   */
  async getStatistics(): Promise<AttestationStatistics> {
    if (!this.apiUrl) {
      throw new Error('API URL not configured');
    }

    const response = await fetch(`${this.apiUrl}/api/attestation/statistics`);

    if (!response.ok) {
      throw new Error(`Failed to get statistics: ${response.statusText}`);
    }

    return await response.json();
  }

  /**
   * 获取区块浏览器链接
   */
  getExplorerUrl(network: BlockchainNetwork, txHash: string): string {
    const config = this.networkConfigs.get(network);
    if (!config) return '';
    return `${config.explorerUrl}/tx/${txHash}`;
  }

  /**
   * 获取地址浏览器链接
   */
  getAddressExplorerUrl(network: BlockchainNetwork, address: string): string {
    const config = this.networkConfigs.get(network);
    if (!config) return '';
    return `${config.explorerUrl}/address/${address}`;
  }

  /**
   * 计算内容哈希（本地预览）
   */
  async computeHash(content: string): Promise<string> {
    const encoder = new TextEncoder();
    const data = encoder.encode(content);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return '0x' + hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
  }

  /**
   * 开始轮询存证状态
   */
  startPolling(attestationId: string): void {
    this.stopPolling(attestationId);
    
    const timer = setInterval(async () => {
      try {
        const attestation = await this.getAttestation(attestationId);
        if (!attestation) return;

        this.emit('attestationUpdated', attestation);

        // 如果状态已经是最终状态，停止轮询
        if (attestation.status === AttestationStatus.CONFIRMED || 
            attestation.status === AttestationStatus.FAILED) {
          this.stopPolling(attestationId);
        }
      } catch (e) {
        console.error(`Polling error for attestation ${attestationId}:`, e);
      }
    }, this.pollingInterval);

    this.pollingTimers.set(attestationId, timer);
  }

  /**
   * 停止轮询
   */
  stopPolling(attestationId: string): void {
    const timer = this.pollingTimers.get(attestationId);
    if (timer) {
      clearInterval(timer);
      this.pollingTimers.delete(attestationId);
    }
  }

  /**
   * 停止所有轮询
   */
  stopAllPolling(): void {
    this.pollingTimers.forEach((timer, id) => {
      clearInterval(timer);
    });
    this.pollingTimers.clear();
  }

  /**
   * 重试失败的存证
   */
  async retryAttestation(id: string): Promise<MessageAttestation> {
    if (!this.apiUrl) {
      throw new Error('API URL not configured');
    }

    const response = await fetch(`${this.apiUrl}/api/attestation/${id}/retry`, {
      method: 'POST'
    });

    if (!response.ok) {
      throw new Error(`Failed to retry attestation: ${response.statusText}`);
    }

    const attestation = await response.json();
    this.startPolling(attestation.id);
    this.emit('attestationRetried', attestation);
    return attestation;
  }

  /**
   * 导出存证证明
   */
  exportAttestationProof(attestation: MessageAttestation): string {
    const proof = {
      version: '1.0',
      attestation: {
        id: attestation.id,
        messageId: attestation.messageId,
        contentHash: attestation.contentHash,
        transactionHash: attestation.transactionHash,
        blockNumber: attestation.blockNumber,
        blockHash: attestation.blockHash,
        network: attestation.network,
        attestorAddress: attestation.attestorAddress,
        merkleRoot: attestation.merkleRoot,
        merkleProof: attestation.merkleProof,
        createdAt: attestation.createdAt,
        confirmedAt: attestation.confirmedAt
      },
      network: this.networkConfigs.get(attestation.network),
      explorerUrl: attestation.transactionHash ? 
        this.getExplorerUrl(attestation.network, attestation.transactionHash) : undefined
    };

    return JSON.stringify(proof, null, 2);
  }

  /**
   * 销毁服务
   */
  destroy(): void {
    this.stopAllPolling();
    this.removeAllListeners();
  }
}

export default Web3AttestationService.getInstance();
