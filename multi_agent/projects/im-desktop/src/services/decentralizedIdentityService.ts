/**
 * 去中心化身份系统服务
 * 
 * 基于区块链和W3C DID标准的身份验证系统前端服务
 * 
 * @version 1.0.0
 * @since 2026-03-24
 */

import { 
  DecentralizedIdentity,
  VerifiableCredential,
  ZKPProofResult,
  ReputationScore,
  CrossChainSyncResult,
  DIDSystemStatistics,
  RegisterIdentityRequest,
  SignatureVerificationRequest,
  VerificationResult,
  CreateCredentialRequest,
  CredentialVerificationResult,
  GenerateZKPRequest,
  VerifyZKPRequest,
  ZKPVerificationResult,
  UpdateReputationRequest,
  CrossChainSyncRequest,
  ApiResponse,
  PaginatedResponse,
  BlockchainType,
  IdentityStatus,
  CredentialType,
  ZKPProofType,
  PrivacyLevel,
  CrossChainSyncStatus
} from '../types/decentralizedIdentity';

/**
 * 去中心化身份系统API服务
 */
export class DecentralizedIdentityService {
  private baseUrl = '/api/did';

  /**
   * 注册新的去中心化身份
   */
  async registerIdentity(request: RegisterIdentityRequest): Promise<ApiResponse<DecentralizedIdentity>> {
    try {
      const response = await fetch(`${this.baseUrl}/identities/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.getAuthToken()}`
        },
        body: JSON.stringify(request)
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '身份注册失败');
      }

      const result: ApiResponse<DecentralizedIdentity> = await response.json();
      return result;
    } catch (error) {
      console.error('注册身份失败:', error);
      return {
        success: false,
        error: {
          code: 'REGISTRATION_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 获取身份详情
   */
  async getIdentity(identityId: string): Promise<ApiResponse<DecentralizedIdentity>> {
    try {
      const response = await fetch(`${this.baseUrl}/identities/${encodeURIComponent(identityId)}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${this.getAuthToken()}`
        }
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '获取身份详情失败');
      }

      const result: ApiResponse<DecentralizedIdentity> = await response.json();
      return result;
    } catch (error) {
      console.error('获取身份详情失败:', error);
      return {
        success: false,
        error: {
          code: 'GET_IDENTITY_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 获取用户的所有身份
   */
  async getUserIdentities(userId: string): Promise<ApiResponse<DecentralizedIdentity[]>> {
    try {
      const response = await fetch(`${this.baseUrl}/users/${encodeURIComponent(userId)}/identities`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${this.getAuthToken()}`
        }
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '获取用户身份失败');
      }

      const result: ApiResponse<DecentralizedIdentity[]> = await response.json();
      return result;
    } catch (error) {
      console.error('获取用户身份失败:', error);
      return {
        success: false,
        error: {
          code: 'GET_USER_IDENTITIES_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 验证身份签名
   */
  async verifyIdentity(identityId: string, request: SignatureVerificationRequest): Promise<ApiResponse<VerificationResult>> {
    try {
      const response = await fetch(`${this.baseUrl}/identities/${encodeURIComponent(identityId)}/verify`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.getAuthToken()}`
        },
        body: JSON.stringify(request)
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '身份验证失败');
      }

      const result: ApiResponse<VerificationResult> = await response.json();
      return result;
    } catch (error) {
      console.error('验证身份失败:', error);
      return {
        success: false,
        error: {
          code: 'VERIFICATION_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 创建可验证凭证
   */
  async createVerifiableCredential(request: CreateCredentialRequest): Promise<ApiResponse<VerifiableCredential>> {
    try {
      const response = await fetch(`${this.baseUrl}/verifiable-credentials/create`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.getAuthToken()}`
        },
        body: JSON.stringify(request)
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '创建凭证失败');
      }

      const result: ApiResponse<VerifiableCredential> = await response.json();
      return result;
    } catch (error) {
      console.error('创建凭证失败:', error);
      return {
        success: false,
        error: {
          code: 'CREATE_CREDENTIAL_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 验证可验证凭证
   */
  async verifyVerifiableCredential(credentialId: string): Promise<ApiResponse<CredentialVerificationResult>> {
    try {
      const response = await fetch(`${this.baseUrl}/verifiable-credentials/${encodeURIComponent(credentialId)}/verify`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.getAuthToken()}`
        }
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '验证凭证失败');
      }

      const result: ApiResponse<CredentialVerificationResult> = await response.json();
      return result;
    } catch (error) {
      console.error('验证凭证失败:', error);
      return {
        success: false,
        error: {
          code: 'VERIFY_CREDENTIAL_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 生成零知识证明
   */
  async generateZeroKnowledgeProof(request: GenerateZKPRequest): Promise<ApiResponse<ZKPProofResult>> {
    try {
      const response = await fetch(`${this.baseUrl}/zk-proof/generate`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.getAuthToken()}`
        },
        body: JSON.stringify(request)
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '生成零知识证明失败');
      }

      const result: ApiResponse<ZKPProofResult> = await response.json();
      return result;
    } catch (error) {
      console.error('生成零知识证明失败:', error);
      return {
        success: false,
        error: {
          code: 'GENERATE_ZKP_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 验证零知识证明
   */
  async verifyZeroKnowledgeProof(request: VerifyZKPRequest): Promise<ApiResponse<ZKPVerificationResult>> {
    try {
      const response = await fetch(`${this.baseUrl}/zk-proof/verify`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.getAuthToken()}`
        },
        body: JSON.stringify(request)
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '验证零知识证明失败');
      }

      const result: ApiResponse<ZKPVerificationResult> = await response.json();
      return result;
    } catch (error) {
      console.error('验证零知识证明失败:', error);
      return {
        success: false,
        error: {
          code: 'VERIFY_ZKP_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 更新声誉评分
   */
  async updateReputationScore(identityId: string, request: UpdateReputationRequest): Promise<ApiResponse<ReputationScore>> {
    try {
      const response = await fetch(`${this.baseUrl}/identities/${encodeURIComponent(identityId)}/reputation/update`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.getAuthToken()}`
        },
        body: JSON.stringify(request)
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '更新声誉评分失败');
      }

      const result: ApiResponse<ReputationScore> = await response.json();
      return result;
    } catch (error) {
      console.error('更新声誉评分失败:', error);
      return {
        success: false,
        error: {
          code: 'UPDATE_REPUTATION_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 同步跨链身份
   */
  async syncCrossChainIdentity(identityId: string, request: CrossChainSyncRequest): Promise<ApiResponse<CrossChainSyncResult>> {
    try {
      const response = await fetch(`${this.baseUrl}/identities/${encodeURIComponent(identityId)}/cross-chain/sync`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.getAuthToken()}`
        },
        body: JSON.stringify(request)
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '跨链同步失败');
      }

      const result: ApiResponse<CrossChainSyncResult> = await response.json();
      return result;
    } catch (error) {
      console.error('跨链同步失败:', error);
      return {
        success: false,
        error: {
          code: 'CROSS_CHAIN_SYNC_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 获取系统统计信息
   */
  async getSystemStatistics(): Promise<ApiResponse<DIDSystemStatistics>> {
    try {
      const response = await fetch(`${this.baseUrl}/statistics/overview`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${this.getAuthToken()}`
        }
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '获取系统统计失败');
      }

      const result: ApiResponse<DIDSystemStatistics> = await response.json();
      return result;
    } catch (error) {
      console.error('获取系统统计失败:', error);
      return {
        success: false,
        error: {
          code: 'GET_STATISTICS_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 分页查询身份列表
   */
  async getIdentitiesByPage(page: number = 1, pageSize: number = 20): Promise<ApiResponse<PaginatedResponse<DecentralizedIdentity>>> {
    try {
      const response = await fetch(`${this.baseUrl}/identities?page=${page}&pageSize=${pageSize}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${this.getAuthToken()}`
        }
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '分页查询身份失败');
      }

      const result: ApiResponse<PaginatedResponse<DecentralizedIdentity>> = await response.json();
      return result;
    } catch (error) {
      console.error('分页查询身份失败:', error);
      return {
        success: false,
        error: {
          code: 'GET_IDENTITIES_PAGINATED_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 根据区块链类型筛选身份
   */
  async getIdentitiesByBlockchain(blockchainType: BlockchainType): Promise<ApiResponse<DecentralizedIdentity[]>> {
    try {
      const response = await fetch(`${this.baseUrl}/identities/blockchain/${blockchainType}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${this.getAuthToken()}`
        }
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '按区块链查询身份失败');
      }

      const result: ApiResponse<DecentralizedIdentity[]> = await response.json();
      return result;
    } catch (error) {
      console.error('按区块链查询身份失败:', error);
      return {
        success: false,
        error: {
          code: 'GET_IDENTITIES_BY_BLOCKCHAIN_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 导出身份数据
   */
  async exportIdentityData(identityId: string): Promise<ApiResponse<{ data: string; format: string }>> {
    try {
      const response = await fetch(`${this.baseUrl}/identities/${encodeURIComponent(identityId)}/export`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${this.getAuthToken()}`
        }
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '导出身份数据失败');
      }

      const result: ApiResponse<{ data: string; format: string }> = await response.json();
      return result;
    } catch (error) {
      console.error('导出身份数据失败:', error);
      return {
        success: false,
        error: {
          code: 'EXPORT_IDENTITY_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 导入身份数据
   */
  async importIdentityData(importData: string, format: string): Promise<ApiResponse<DecentralizedIdentity>> {
    try {
      const response = await fetch(`${this.baseUrl}/identities/import`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.getAuthToken()}`
        },
        body: JSON.stringify({ data: importData, format })
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '导入身份数据失败');
      }

      const result: ApiResponse<DecentralizedIdentity> = await response.json();
      return result;
    } catch (error) {
      console.error('导入身份数据失败:', error);
      return {
        success: false,
        error: {
          code: 'IMPORT_IDENTITY_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 批量验证身份
   */
  async batchVerifyIdentities(identityIds: string[]): Promise<ApiResponse<VerificationResult[]>> {
    try {
      const response = await fetch(`${this.baseUrl}/identities/batch-verify`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.getAuthToken()}`
        },
        body: JSON.stringify({ identityIds })
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '批量验证身份失败');
      }

      const result: ApiResponse<VerificationResult[]> = await response.json();
      return result;
    } catch (error) {
      console.error('批量验证身份失败:', error);
      return {
        success: false,
        error: {
          code: 'BATCH_VERIFY_FAILED',
          message: error instanceof Error ? error.message : '未知错误'
        },
        timestamp: Date.now()
      };
    }
  }

  /**
   * 获取认证令牌（简化实现）
   */
  private getAuthToken(): string {
    // 实际应用中应从安全存储获取
    return localStorage.getItem('auth_token') || '';
  }

  /**
   * 生成DID标识符
   */
  generateDIDIdentifier(blockchainType: BlockchainType, address: string): string {
    switch (blockchainType) {
      case BlockchainType.ETHEREUM:
        return `did:eth:${address}`;
      case BlockchainType.POLYGON:
        return `did:polygon:${address}`;
      case BlockchainType.SOLANA:
        return `did:sol:${address}`;
      case BlockchainType.BINANCE_SMART_CHAIN:
        return `did:bsc:${address}`;
      case BlockchainType.ARBITRUM:
        return `did:arbitrum:${address}`;
      case BlockchainType.OPTIMISM:
        return `did:optimism:${address}`;
      case BlockchainType.BASE:
        return `did:base:${address}`;
      case BlockchainType.AVALANCHE:
        return `did:avax:${address}`;
      case BlockchainType.NEAR:
        return `did:near:${address}`;
      case BlockchainType.COSMOS:
        return `did:cosmos:${address}`;
      case BlockchainType.CELESTIA:
        return `did:celestia:${address}`;
      case BlockchainType.APTOS:
        return `did:aptos:${address}`;
      case BlockchainType.SUI:
        return `did:sui:${address}`;
      case BlockchainType.CARDANO:
        return `did:cardano:${address}`;
      case BlockchainType.TEZOS:
        return `did:tezos:${address}`;
      case BlockchainType.ALGORAND:
        return `did:algo:${address}`;
      case BlockchainType.HEDERA:
        return `did:hedera:${address}`;
      case BlockchainType.STARKNET:
        return `did:starknet:${address}`;
      case BlockchainType.ZKSYNC:
        return `did:zksync:${address}`;
      default:
        return `did:unknown:${address}`;
    }
  }

  /**
   * 格式化区块链地址
   */
  formatBlockchainAddress(address: string, blockchainType: BlockchainType): string {
    const prefix = {
      [BlockchainType.ETHEREUM]: '0x',
      [BlockchainType.POLYGON]: '0x',
      [BlockchainType.BINANCE_SMART_CHAIN]: '0x',
      [BlockchainType.ARBITRUM]: '0x',
      [BlockchainType.OPTIMISM]: '0x',
      [BlockchainType.BASE]: '0x',
      [BlockchainType.AVALANCHE]: '0x',
      [BlockchainType.SOLANA]: '',
      [BlockchainType.NEAR]: '',
      [BlockchainType.COSMOS]: 'cosmos1',
      [BlockchainType.CELESTIA]: 'celestia1',
      [BlockchainType.APTOS]: '0x',
      [BlockchainType.SUI]: '0x',
      [BlockchainType.CARDANO]: 'addr1',
      [BlockchainType.TEZOS]: 'tz1',
      [BlockchainType.ALGORAND]: '',
      [BlockchainType.HEDERA]: '0.0.',
      [BlockchainType.STARKNET]: '0x',
      [BlockchainType.ZKSYNC]: '0x'
    }[blockchainType];

    // 如果地址已经有正确的前缀，直接返回
    if (prefix && address.startsWith(prefix)) {
      return address;
    }

    // 否则添加前缀
    return prefix ? `${prefix}${address}` : address;
  }

  /**
   * 检查区块链地址有效性
   */
  isValidBlockchainAddress(address: string, blockchainType: BlockchainType): boolean {
    // 简化验证，实际应用应使用对应区块链的地址验证库
    const patterns = {
      [BlockchainType.ETHEREUM]: /^0x[a-fA-F0-9]{40}$/,
      [BlockchainType.POLYGON]: /^0x[a-fA-F0-9]{40}$/,
      [BlockchainType.BINANCE_SMART_CHAIN]: /^0x[a-fA-F0-9]{40}$/,
      [BlockchainType.ARBITRUM]: /^0x[a-fA-F0-9]{40}$/,
      [BlockchainType.OPTIMISM]: /^0x[a-fA-F0-9]{40}$/,
      [BlockchainType.BASE]: /^0x[a-fA-F0-9]{40}$/,
      [BlockchainType.AVALANCHE]: /^0x[a-fA-F0-9]{40}$/,
      [BlockchainType.SOLANA]: /^[1-9A-HJ-NP-Za-km-z]{32,44}$/,
      [BlockchainType.NEAR]: /^[a-z0-9_-]{2,64}\.near$/,
      [BlockchainType.COSMOS]: /^cosmos1[qpzry9x8gf2tvdw0s3jn54khce6mua7l]{38}$/,
      [BlockchainType.CELESTIA]: /^celestia1[qpzry9x8gf2tvdw0s3jn54khce6mua7l]{38}$/,
      [BlockchainType.APTOS]: /^0x[a-fA-F0-9]{64}$/,
      [BlockchainType.SUI]: /^0x[a-fA-F0-9]{64}$/,
      [BlockchainType.CARDANO]: /^addr1[qpzry9x8gf2tvdw0s3jn54khce6mua7l]{58}$/,
      [BlockchainType.TEZOS]: /^tz1[1-9A-HJ-NP-Za-km-z]{33}$/,
      [BlockchainType.ALGORAND]: /^[A-Z2-7]{58}$/,
      [BlockchainType.HEDERA]: /^0\.0\.\d+$/,
      [BlockchainType.STARKNET]: /^0x[a-fA-F0-9]{64}$/,
      [BlockchainType.ZKSYNC]: /^0x[a-fA-F0-9]{64}$/
    }[blockchainType];

    return patterns ? patterns.test(address) : true;
  }
}

// 创建服务实例
export const decentralizedIdentityService = new DecentralizedIdentityService();

// 导出类型和枚举以方便使用
export * from '../types/decentralizedIdentity';