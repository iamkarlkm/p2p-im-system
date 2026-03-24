package com.im.system.controller;

import com.im.system.entity.DecentralizedIdentityEntity;
import com.im.system.entity.VerifiableCredentialEntity;
import com.im.system.service.DecentralizedIdentityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST API 控制器 - 去中心化身份系统 (DID)
 * 
 * <p>提供基于区块链和去中心化身份标准的身份验证系统 API 接口</p>
 * 
 * @version 1.0.0
 * @since 2026-03-24
 */
@RestController
@RequestMapping("/api/did")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "去中心化身份系统", description = "基于区块链和W3C DID标准的身份验证系统")
public class DecentralizedIdentityController {

    private final DecentralizedIdentityService decentralizedIdentityService;

    /**
     * 注册新的去中心化身份
     * 
     * @param request 身份注册请求
     * @return 注册成功的身份信息
     */
    @PostMapping("/identities/register")
    @Operation(summary = "注册去中心化身份", description = "创建新的去中心化身份，支持多种区块链网络")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "身份注册成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "409", description = "身份已存在")
    })
    public ResponseEntity<DecentralizedIdentityEntity> registerIdentity(
            @RequestBody RegisterIdentityRequest request) {
        log.info("注册去中心化身份请求: {}", request.getIdentityAddress());
        DecentralizedIdentityEntity identity = decentralizedIdentityService.registerIdentity(
                request.getUserId(),
                request.getIdentityAddress(),
                request.getBlockchainType(),
                request.getPublicKey(),
                request.getMetadata()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(identity);
    }

    /**
     * 获取身份详情
     * 
     * @param identityId 身份ID
     * @return 身份详情
     */
    @GetMapping("/identities/{identityId}")
    @Operation(summary = "获取身份详情", description = "根据身份ID查询身份详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "身份详情查询成功"),
        @ApiResponse(responseCode = "404", description = "身份不存在")
    })
    public ResponseEntity<DecentralizedIdentityEntity> getIdentity(
            @PathVariable @Parameter(description = "身份ID") String identityId) {
        DecentralizedIdentityEntity identity = decentralizedIdentityService.getIdentityById(identityId);
        return ResponseEntity.ok(identity);
    }

    /**
     * 获取用户的所有身份
     * 
     * @param userId 用户ID
     * @return 用户身份列表
     */
    @GetMapping("/users/{userId}/identities")
    @Operation(summary = "获取用户所有身份", description = "查询指定用户的所有去中心化身份")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<List<DecentralizedIdentityEntity>> getUserIdentities(
            @PathVariable @Parameter(description = "用户ID") String userId) {
        List<DecentralizedIdentityEntity> identities = decentralizedIdentityService.getUserIdentities(userId);
        return ResponseEntity.ok(identities);
    }

    /**
     * 验证身份
     * 
     * @param identityId 身份ID
     * @param signature 签名数据
     * @return 验证结果
     */
    @PostMapping("/identities/{identityId}/verify")
    @Operation(summary = "验证身份", description = "验证身份签名有效性")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "验证完成"),
        @ApiResponse(responseCode = "404", description = "身份不存在")
    })
    public ResponseEntity<VerificationResult> verifyIdentity(
            @PathVariable @Parameter(description = "身份ID") String identityId,
            @RequestBody SignatureVerificationRequest request) {
        log.info("验证身份签名: identityId={}", identityId);
        boolean isValid = decentralizedIdentityService.verifyIdentitySignature(
                identityId,
                request.getMessage(),
                request.getSignature()
        );
        VerificationResult result = VerificationResult.builder()
                .identityId(identityId)
                .isValid(isValid)
                .verificationTime(System.currentTimeMillis())
                .build();
        return ResponseEntity.ok(result);
    }

    /**
     * 创建可验证凭证
     * 
     * @param request 凭证创建请求
     * @return 创建的凭证
     */
    @PostMapping("/verifiable-credentials/create")
    @Operation(summary = "创建可验证凭证", description = "签发W3C标准可验证凭证")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "凭证创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效")
    })
    public ResponseEntity<VerifiableCredentialEntity> createVerifiableCredential(
            @RequestBody CreateCredentialRequest request) {
        log.info("创建可验证凭证: issuer={}, subject={}", request.getIssuerId(), request.getSubjectId());
        VerifiableCredentialEntity credential = decentralizedIdentityService.createVerifiableCredential(
                request.getIssuerId(),
                request.getSubjectId(),
                request.getCredentialType(),
                request.getAttributes(),
                request.getExpiryTime(),
                request.getRevocable(),
                request.getMetadata()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(credential);
    }

    /**
     * 验证可验证凭证
     * 
     * @param credentialId 凭证ID
     * @return 验证结果
     */
    @PostMapping("/verifiable-credentials/{credentialId}/verify")
    @Operation(summary = "验证可验证凭证", description = "验证可验证凭证的有效性和完整性")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "验证完成"),
        @ApiResponse(responseCode = "404", description = "凭证不存在")
    })
    public ResponseEntity<CredentialVerificationResult> verifyVerifiableCredential(
            @PathVariable @Parameter(description = "凭证ID") String credentialId) {
        boolean isValid = decentralizedIdentityService.verifyVerifiableCredential(credentialId);
        CredentialVerificationResult result = CredentialVerificationResult.builder()
                .credentialId(credentialId)
                .isValid(isValid)
                .verificationTime(System.currentTimeMillis())
                .build();
        return ResponseEntity.ok(result);
    }

    /**
     * 生成零知识证明
     * 
     * @param request 证明生成请求
     * @return 证明生成结果
     */
    @PostMapping("/zk-proof/generate")
    @Operation(summary = "生成零知识证明", description = "生成基于零知识证明的身份验证")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "证明生成成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效")
    })
    public ResponseEntity<ZKPProofResult> generateZeroKnowledgeProof(
            @RequestBody GenerateZKPRequest request) {
        log.info("生成零知识证明: userId={}, proofType={}", request.getUserId(), request.getProofType());
        ZKPProofResult proof = decentralizedIdentityService.generateZeroKnowledgeProof(
                request.getUserId(),
                request.getProofType(),
                request.getWitnessData(),
                request.getRevealedAttributes(),
                request.getPrivacyLevel()
        );
        return ResponseEntity.ok(proof);
    }

    /**
     * 验证零知识证明
     * 
     * @param request 证明验证请求
     * @return 验证结果
     */
    @PostMapping("/zk-proof/verify")
    @Operation(summary = "验证零知识证明", description = "验证零知识证明的有效性")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "验证完成"),
        @ApiResponse(responseCode = "400", description = "请求参数无效")
    })
    public ResponseEntity<ZKPVerificationResult> verifyZeroKnowledgeProof(
            @RequestBody VerifyZKPRequest request) {
        boolean isValid = decentralizedIdentityService.verifyZeroKnowledgeProof(
                request.getProofData(),
                request.getVerificationKey(),
                request.getStatement()
        );
        ZKPVerificationResult result = ZKPVerificationResult.builder()
                .isValid(isValid)
                .verificationTime(System.currentTimeMillis())
                .build();
        return ResponseEntity.ok(result);
    }

    /**
     * 更新链上声誉评分
     * 
     * @param identityId 身份ID
     * @param request 声誉更新请求
     * @return 更新后的声誉评分
     */
    @PostMapping("/identities/{identityId}/reputation/update")
    @Operation(summary = "更新链上声誉评分", description = "更新身份的链上声誉评分")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "声誉评分更新成功"),
        @ApiResponse(responseCode = "404", description = "身份不存在")
    })
    public ResponseEntity<ReputationScore> updateReputationScore(
            @PathVariable @Parameter(description = "身份ID") String identityId,
            @RequestBody UpdateReputationRequest request) {
        log.info("更新声誉评分: identityId={}, scoreDelta={}", identityId, request.getScoreDelta());
        ReputationScore reputation = decentralizedIdentityService.updateReputationScore(
                identityId,
                request.getScoreDelta(),
                request.getReason(),
                request.getEvidence()
        );
        return ResponseEntity.ok(reputation);
    }

    /**
     * 同步跨链身份
     * 
     * @param identityId 身份ID
     * @param request 跨链同步请求
     * @return 同步结果
     */
    @PostMapping("/identities/{identityId}/cross-chain/sync")
    @Operation(summary = "同步跨链身份", description = "同步身份到其他区块链网络")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "跨链同步成功"),
        @ApiResponse(responseCode = "404", description = "身份不存在")
    })
    public ResponseEntity<CrossChainSyncResult> syncCrossChainIdentity(
            @PathVariable @Parameter(description = "身份ID") String identityId,
            @RequestBody CrossChainSyncRequest request) {
        log.info("跨链身份同步: identityId={}, targetChains={}", identityId, request.getTargetChains());
        CrossChainSyncResult result = decentralizedIdentityService.syncCrossChainIdentity(
                identityId,
                request.getTargetChains(),
                request.getForceSync()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * 获取系统统计信息
     * 
     * @return 系统统计概览
     */
    @GetMapping("/statistics/overview")
    @Operation(summary = "获取系统统计信息", description = "获取去中心化身份系统统计概览")
    public ResponseEntity<DIDSystemStatistics> getSystemStatistics() {
        DIDSystemStatistics statistics = decentralizedIdentityService.getSystemStatistics();
        return ResponseEntity.ok(statistics);
    }

    // 请求和响应类定义

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterIdentityRequest {
        private String userId;
        private String identityAddress;
        private String blockchainType;
        private String publicKey;
        private Map<String, Object> metadata;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignatureVerificationRequest {
        private String message;
        private String signature;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificationResult {
        private String identityId;
        private boolean isValid;
        private long verificationTime;
        private String verificationMethod;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCredentialRequest {
        private String issuerId;
        private String subjectId;
        private String credentialType;
        private Map<String, Object> attributes;
        private Long expiryTime;
        private Boolean revocable;
        private Map<String, Object> metadata;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CredentialVerificationResult {
        private String credentialId;
        private boolean isValid;
        private long verificationTime;
        private Boolean revoked;
        private Long expiryTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerateZKPRequest {
        private String userId;
        private String proofType;
        private Map<String, Object> witnessData;
        private List<String> revealedAttributes;
        private String privacyLevel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ZKPProofResult {
        private String proofId;
        private String proofData;
        private String verificationKey;
        private long generationTimeMs;
        private int proofSizeBytes;
        private String proofType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyZKPRequest {
        private String proofData;
        private String verificationKey;
        private String statement;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ZKPVerificationResult {
        private boolean isValid;
        private long verificationTimeMs;
        private String errorMessage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReputationRequest {
        private double scoreDelta;
        private String reason;
        private String evidence;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReputationScore {
        private String identityId;
        private double score;
        private int rank;
        private long lastUpdateTime;
        private List<String> positiveFactors;
        private List<String> negativeFactors;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrossChainSyncRequest {
        private List<String> targetChains;
        private boolean forceSync;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrossChainSyncResult {
        private String identityId;
        private Map<String, SyncStatus> chainSyncStatus;
        private List<String> failedChains;
        private long syncTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyncStatus {
        private String chain;
        private boolean synced;
        private String transactionHash;
        private long blockNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DIDSystemStatistics {
        private int totalIdentities;
        private int activeIdentities;
        private Map<String, Integer> identitiesByChain;
        private int totalCredentials;
        private int validCredentials;
        private int totalZKProofs;
        private double averageReputationScore;
        private long totalCrossChainSyncs;
        private Map<String, Object> performanceMetrics;
    }
}