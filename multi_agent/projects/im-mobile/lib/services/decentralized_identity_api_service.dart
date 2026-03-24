/// 去中心化身份系统移动端API服务
///
/// 基于区块链和W3C DID标准的身份验证系统移动端API客户端
///
/// @version 1.0.0
/// @since 2026-03-24

import 'dart:convert';
import 'package:http/http.dart' as http;
import 'decentralized_identity_model.dart';

/// 去中心化身份系统API客户端
class DecentralizedIdentityApiClient {
  static const String _baseUrl = '/api/did';
  static String? _authToken;

  /// 设置认证令牌
  static void setAuthToken(String token) {
    _authToken = token;
  }

  /// 获取请求头
  static Map<String, String> _getHeaders() {
    final headers = {
      'Content-Type': 'application/json',
    };
    if (_authToken != null) {
      headers['Authorization'] = 'Bearer $_authToken';
    }
    return headers;
  }

  /// 注册新的去中心化身份
  static Future<ApiResponse<DecentralizedIdentity>> registerIdentity(
    RegisterIdentityRequest request,
  ) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/identities/register'),
        headers: _getHeaders(),
        body: jsonEncode(request.toJson()),
      );

      final data = jsonDecode(response.body);
      if (response.statusCode == 201) {
        return ApiResponse<DecentralizedIdentity>(
          success: true,
          data: DecentralizedIdentity.fromJson(data['data']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      } else {
        return ApiResponse<DecentralizedIdentity>(
          success: false,
          error: ApiError.fromJson(data['error']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      }
    } catch (e) {
      return ApiResponse<DecentralizedIdentity>(
        success: false,
        error: ApiError(
          code: 'REGISTRATION_FAILED',
          message: e.toString(),
        ),
        timestamp: DateTime.now().millisecondsSinceEpoch,
      );
    }
  }

  /// 获取身份详情
  static Future<ApiResponse<DecentralizedIdentity>> getIdentity(
    String identityId,
  ) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/identities/${Uri.encodeComponent(identityId)}'),
        headers: _getHeaders(),
      );

      final data = jsonDecode(response.body);
      if (response.statusCode == 200) {
        return ApiResponse<DecentralizedIdentity>(
          success: true,
          data: DecentralizedIdentity.fromJson(data['data']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      } else {
        return ApiResponse<DecentralizedIdentity>(
          success: false,
          error: ApiError.fromJson(data['error']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      }
    } catch (e) {
      return ApiResponse<DecentralizedIdentity>(
        success: false,
        error: ApiError(
          code: 'GET_IDENTITY_FAILED',
          message: e.toString(),
        ),
        timestamp: DateTime.now().millisecondsSinceEpoch,
      );
    }
  }

  /// 获取用户的所有身份
  static Future<ApiResponse<List<DecentralizedIdentity>>> getUserIdentities(
    String userId,
  ) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/users/${Uri.encodeComponent(userId)}/identities'),
        headers: _getHeaders(),
      );

      final data = jsonDecode(response.body);
      if (response.statusCode == 200) {
        final List<dynamic> items = data['data'];
        return ApiResponse<List<DecentralizedIdentity>>(
          success: true,
          data: items.map((e) => DecentralizedIdentity.fromJson(e)).toList(),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      } else {
        return ApiResponse<List<DecentralizedIdentity>>(
          success: false,
          error: ApiError.fromJson(data['error']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      }
    } catch (e) {
      return ApiResponse<List<DecentralizedIdentity>>(
        success: false,
        error: ApiError(
          code: 'GET_USER_IDENTITIES_FAILED',
          message: e.toString(),
        ),
        timestamp: DateTime.now().millisecondsSinceEpoch,
      );
    }
  }

  /// 验证身份签名
  static Future<ApiResponse<VerificationResult>> verifyIdentity(
    String identityId,
    SignatureVerificationRequest request,
  ) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/identities/${Uri.encodeComponent(identityId)}/verify'),
        headers: _getHeaders(),
        body: jsonEncode(request.toJson()),
      );

      final data = jsonDecode(response.body);
      if (response.statusCode == 200) {
        return ApiResponse<VerificationResult>(
          success: true,
          data: VerificationResult.fromJson(data['data']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      } else {
        return ApiResponse<VerificationResult>(
          success: false,
          error: ApiError.fromJson(data['error']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      }
    } catch (e) {
      return ApiResponse<VerificationResult>(
        success: false,
        error: ApiError(
          code: 'VERIFICATION_FAILED',
          message: e.toString(),
        ),
        timestamp: DateTime.now().millisecondsSinceEpoch,
      );
    }
  }

  /// 创建可验证凭证
  static Future<ApiResponse<VerifiableCredential>> createVerifiableCredential(
    Map<String, dynamic> request,
  ) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/verifiable-credentials/create'),
        headers: _getHeaders(),
        body: jsonEncode(request),
      );

      final data = jsonDecode(response.body);
      if (response.statusCode == 201) {
        return ApiResponse<VerifiableCredential>(
          success: true,
          data: VerifiableCredential.fromJson(data['data']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      } else {
        return ApiResponse<VerifiableCredential>(
          success: false,
          error: ApiError.fromJson(data['error']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      }
    } catch (e) {
      return ApiResponse<VerifiableCredential>(
        success: false,
        error: ApiError(
          code: 'CREATE_CREDENTIAL_FAILED',
          message: e.toString(),
        ),
        timestamp: DateTime.now().millisecondsSinceEpoch,
      );
    }
  }

  /// 验证可验证凭证
  static Future<ApiResponse<Map<String, dynamic>>> verifyVerifiableCredential(
    String credentialId,
  ) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/verifiable-credentials/${Uri.encodeComponent(credentialId)}/verify'),
        headers: _getHeaders(),
      );

      final data = jsonDecode(response.body);
      if (response.statusCode == 200) {
        return ApiResponse<Map<String, dynamic>>(
          success: true,
          data: data['data'],
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      } else {
        return ApiResponse<Map<String, dynamic>>(
          success: false,
          error: ApiError.fromJson(data['error']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      }
    } catch (e) {
      return ApiResponse<Map<String, dynamic>>(
        success: false,
        error: ApiError(
          code: 'VERIFY_CREDENTIAL_FAILED',
          message: e.toString(),
        ),
        timestamp: DateTime.now().millisecondsSinceEpoch,
      );
    }
  }

  /// 生成零知识证明
  static Future<ApiResponse<ZKPProofResult>> generateZeroKnowledgeProof(
    Map<String, dynamic> request,
  ) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/zk-proof/generate'),
        headers: _getHeaders(),
        body: jsonEncode(request),
      );

      final data = jsonDecode(response.body);
      if (response.statusCode == 200) {
        return ApiResponse<ZKPProofResult>(
          success: true,
          data: ZKPProofResult.fromJson(data['data']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      } else {
        return ApiResponse<ZKPProofResult>(
          success: false,
          error: ApiError.fromJson(data['error']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      }
    } catch (e) {
      return ApiResponse<ZKPProofResult>(
        success: false,
        error: ApiError(
          code: 'GENERATE_ZKP_FAILED',
          message: e.toString(),
        ),
        timestamp: DateTime.now().millisecondsSinceEpoch,
      );
    }
  }

  /// 验证零知识证明
  static Future<ApiResponse<Map<String, dynamic>>> verifyZeroKnowledgeProof(
    Map<String, dynamic> request,
  ) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/zk-proof/verify'),
        headers: _getHeaders(),
        body: jsonEncode(request),
      );

      final data = jsonDecode(response.body);
      if (response.statusCode == 200) {
        return ApiResponse<Map<String, dynamic>>(
          success: true,
          data: data['data'],
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      } else {
        return ApiResponse<Map<String, dynamic>>(
          success: false,
          error: ApiError.fromJson(data['error']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      }
    } catch (e) {
      return ApiResponse<Map<String, dynamic>>(
        success: false,
        error: ApiError(
          code: 'VERIFY_ZKP_FAILED',
          message: e.toString(),
        ),
        timestamp: DateTime.now().millisecondsSinceEpoch,
      );
    }
  }

  /// 更新声誉评分
  static Future<ApiResponse<ReputationScore>> updateReputationScore(
    String identityId,
    Map<String, dynamic> request,
  ) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/identities/${Uri.encodeComponent(identityId)}/reputation/update'),
        headers: _getHeaders(),
        body: jsonEncode(request),
      );

      final data = jsonDecode(response.body);
      if (response.statusCode == 200) {
        return ApiResponse<ReputationScore>(
          success: true,
          data: ReputationScore.fromJson(data['data']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      } else {
        return ApiResponse<ReputationScore>(
          success: false,
          error: ApiError.fromJson(data['error']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      }
    } catch (e) {
      return ApiResponse<ReputationScore>(
        success: false,
        error: ApiError(
          code: 'UPDATE_REPUTATION_FAILED',
          message: e.toString(),
        ),
        timestamp: DateTime.now().millisecondsSinceEpoch,
      );
    }
  }

  /// 同步跨链身份
  static Future<ApiResponse<CrossChainSyncResult>> syncCrossChainIdentity(
    String identityId,
    Map<String, dynamic> request,
  ) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/identities/${Uri.encodeComponent(identityId)}/cross-chain/sync'),
        headers: _getHeaders(),
        body: jsonEncode(request),
      );

      final data = jsonDecode(response.body);
      if (response.statusCode == 200) {
        return ApiResponse<CrossChainSyncResult>(
          success: true,
          data: CrossChainSyncResult.fromJson(data['data']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      } else {
        return ApiResponse<CrossChainSyncResult>(
          success: false,
          error: ApiError.fromJson(data['error']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      }
    } catch (e) {
      return ApiResponse<CrossChainSyncResult>(
        success: false,
        error: ApiError(
          code: 'CROSS_CHAIN_SYNC_FAILED',
          message: e.toString(),
        ),
        timestamp: DateTime.now().millisecondsSinceEpoch,
      );
    }
  }

  /// 获取系统统计信息
  static Future<ApiResponse<DIDSystemStatistics>> getSystemStatistics() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/statistics/overview'),
        headers: _getHeaders(),
      );

      final data = jsonDecode(response.body);
      if (response.statusCode == 200) {
        return ApiResponse<DIDSystemStatistics>(
          success: true,
          data: DIDSystemStatistics.fromJson(data['data']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      } else {
        return ApiResponse<DIDSystemStatistics>(
          success: false,
          error: ApiError.fromJson(data['error']),
          timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
        );
      }
    } catch (e) {
      return ApiResponse<DIDSystemStatistics>(
        success: false,
        error: ApiError(
          code: 'GET_STATISTICS_FAILED',
          message: e.toString(),
        ),
        timestamp: DateTime.now().millisecondsSinceEpoch,
      );
    }
  }
}

/// 去中心化身份管理器
/// 提供高级功能如缓存、批量操作等
class DecentralizedIdentityManager {
  static final Map<String, DecentralizedIdentity> _identityCache = {};
  static final Map<String, VerifiableCredential> _credentialCache = {};

  /// 从缓存获取身份
  static DecentralizedIdentity? getCachedIdentity(String identityId) {
    return _identityCache[identityId];
  }

  /// 缓存身份
  static void cacheIdentity(DecentralizedIdentity identity) {
    _identityCache[identity.identityId] = identity;
  }

  /// 清除身份缓存
  static void clearIdentityCache() {
    _identityCache.clear();
  }

  /// 从缓存获取凭证
  static VerifiableCredential? getCachedCredential(String credentialId) {
    return _credentialCache[credentialId];
  }

  /// 缓存凭证
  static void cacheCredential(VerifiableCredential credential) {
    _credentialCredential[credential.credentialId] = credential;
  }

  /// 清除凭证缓存
  static void clearCredentialCache() {
    _credentialCache.clear();
  }

  /// 批量验证多个身份
  static Future<List<VerificationResult>> batchVerifyIdentities(
    List<String> identityIds,
    String message,
    Map<String, String> signatures,
  ) async {
    final results = <VerificationResult>[];
    
    for (final identityId in identityIds) {
      final signature = signatures[identityId];
      if (signature != null) {
        final request = SignatureVerificationRequest(
          message: message,
          signature: signature,
        );
        final response = await DecentralizedIdentityApiClient.verifyIdentity(
          identityId,
          request,
        );
        if (response.success && response.data != null) {
          results.add(response.data!);
        }
      }
    }
    
    return results;
  }

  /// 获取活跃身份列表
  static Future<List<DecentralizedIdentity>> getActiveIdentities(
    String userId,
  ) async {
    final response = await DecentralizedIdentityApiClient.getUserIdentities(userId);
    if (response.success && response.data != null) {
      return response.data!.where((identity) => identity.isActive).toList();
    }
    return [];
  }

  /// 获取身份的可验证凭证
  static Future<List<VerifiableCredential>> getIdentityCredentials(
    String identityId,
  ) async {
    // 这里可以实现凭证查询逻辑
    // 目前返回空列表，需要后端支持
    return [];
  }

  /// 检查身份是否已同步到指定链
  static bool isIdentitySyncedToChain(
    DecentralizedIdentity identity,
    BlockchainType chain,
  ) {
    return identity.syncedChains.contains(chain);
  }

  /// 生成DID标识符
  static String generateDIDIdentifier(
    BlockchainType blockchainType,
    String address,
  ) {
    switch (blockchainType) {
      case BlockchainType.ethereum:
        return 'did:eth:$address';
      case BlockchainType.polygon:
        return 'did:polygon:$address';
      case BlockchainType.solana:
        return 'did:sol:$address';
      case BlockchainType.binanceSmartChain:
        return 'did:bsc:$address';
      case BlockchainType.arbitrum:
        return 'did:arbitrum:$address';
      case BlockchainType.optimism:
        return 'did:optimism:$address';
      case BlockchainType.base:
        return 'did:base:$address';
      case BlockchainType.avalanche:
        return 'did:avax:$address';
      case BlockchainType.near:
        return 'did:near:$address';
      case BlockchainType.cosmos:
        return 'did:cosmos:$address';
      case BlockchainType.celestia:
        return 'did:celestia:$address';
      case BlockchainType.aptos:
        return 'did:aptos:$address';
      case BlockchainType.sui:
        return 'did:sui:$address';
      case BlockchainType.cardano:
        return 'did:cardano:$address';
      case BlockchainType.tezos:
        return 'did:tezos:$address';
      case BlockchainType.algorand:
        return 'did:algo:$address';
      case BlockchainType.hedera:
        return 'did:hedera:$address';
      case BlockchainType.starknet:
        return 'did:starknet:$address';
      case BlockchainType.zksync:
        return 'did:zksync:$address';
    }
  }

  /// 验证区块链地址格式
  static bool isValidBlockchainAddress(
    String address,
    BlockchainType blockchainType,
  ) {
    final patterns = {
      BlockchainType.ethereum: RegExp(r'^0x[a-fA-F0-9]{40}$'),
      BlockchainType.polygon: RegExp(r'^0x[a-fA-F0-9]{40}$'),
      BlockchainType.binanceSmartChain: RegExp(r'^0x[a-fA-F0-9]{40}$'),
      BlockchainType.arbitrum: RegExp(r'^0x[a-fA-F0-9]{40}$'),
      BlockchainType.optimism: RegExp(r'^0x[a-fA-F0-9]{40}$'),
      BlockchainType.base: RegExp(r'^0x[a-fA-F0-9]{40}$'),
      BlockchainType.avalanche: RegExp(r'^0x[a-fA-F0-9]{40}$'),
      BlockchainType.solana: RegExp(r'^[1-9A-HJ-NP-Za-km-z]{32,44}$'),
      BlockchainType.near: RegExp(r'^[a-z0-9_-]{2,64}\.near$'),
      BlockchainType.cosmos: RegExp(r'^cosmos1[qpzry9x8gf2tvdw0s3jn54khce6mua7l]{38}$'),
      BlockchainType.celestia: RegExp(r'^celestia1[qpzry9x8gf2tvdw0s3jn54khce6mua7l]{38}$'),
      BlockchainType.aptos: RegExp(r'^0x[a-fA-F0-9]{64}$'),
      BlockchainType.sui: RegExp(r'^0x[a-fA-F0-9]{64}$'),
      BlockchainType.cardano: RegExp(r'^addr1[qpzry9x8gf2tvdw0s3jn54khce6mua7l]{58}$'),
      BlockchainType.tezos: RegExp(r'^tz1[1-9A-HJ-NP-Za-km-z]{33}$'),
      BlockchainType.algorand: RegExp(r'^[A-Z2-7]{58}$'),
      BlockchainType.hedera: RegExp(r'^0\.0\.\d+$'),
      BlockchainType.starknet: RegExp(r'^0x[a-fA-F0-9]{64}$'),
      BlockchainType.zksync: RegExp(r'^0x[a-fA-F0-9]{64}$'),
    };

    final pattern = patterns[blockchainType];
    return pattern?.hasMatch(address) ?? false;
  }

  /// 计算声誉得分等级
  static String getReputationLevel(double score) {
    if (score >= 90) return 'Excellent';
    if (score >= 80) return 'Very Good';
    if (score >= 70) return 'Good';
    if (score >= 60) return 'Fair';
    if (score >= 40) return 'Average';
    if (score >= 20) return 'Below Average';
    return 'Poor';
  }

  /// 获取隐私级别描述
  static String getPrivacyLevelDescription(PrivacyLevel level) {
    switch (level) {
      case PrivacyLevel.public:
        return 'Public - Information is visible to everyone';
      case PrivacyLevel.private:
        return 'Private - Information is visible to authorized parties';
      case PrivacyLevel.confidential:
        return 'Confidential - Information requires special authorization';
      case PrivacyLevel.secret:
        return 'Secret - Information is highly restricted';
      case PrivacyLevel.topSecret:
        return 'Top Secret - Information is extremely restricted';
    }
  }
}