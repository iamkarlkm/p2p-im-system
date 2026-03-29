package com.imsystem.controller;

import com.imsystem.entity.DecentralizedIdentityEntity;
import com.imsystem.entity.VerifiableCredentialEntity;
import com.imsystem.service.DecentralizedIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 去中心化身份 REST API 控制器
 * 提供完整的 DID 身份管理和验证 API
 */
@RestController
@RequestMapping("/api/v1/did")
@Tag(name = "Decentralized Identity", description = "去中心化身份 (DID) 管理 API")
public class DecentralizedIdentityController {

    private static final Logger logger = LoggerFactory.getLogger(DecentralizedIdentityController.class);
    
    @Autowired
    private DecentralizedIdentityService identityService;
    
    /**
     * 创建新的 DID 身份
     */
    @PostMapping("/identities")
    @Operation(summary = "创建 DID 身份", description = "基于区块链地址创建新的去中心化身份")
    public ResponseEntity<?> createIdentity(
            @RequestBody Map<String, Object> request) {
        
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String blockchainType = (String) request.get("blockchainType");
            String blockchainAddress = (String) request.get("blockchainAddress");
            String publicKey = (String) request.get("publicKey");
            
            DecentralizedIdentityEntity identity = identityService.createIdentity(
                    userId, blockchainType, blockchainAddress, publicKey);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "身份创建成功");
            response.put("data", identity);
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("API: 创建 DID 身份成功 - 用户ID: {}, 地址: {}", userId, blockchainAddress);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("API: 创建 DID 身份失败", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 验证 DID 身份
     */
    @GetMapping("/identities/{did}/verify")
    @Operation(summary = "验证 DID 身份", description = "验证指定 DID 身份的有效性")
    public ResponseEntity<?> verifyIdentity(
            @PathVariable("did") @Parameter(description = "DID 标识符") String did,
            @RequestParam(value = "address", required = false) @Parameter(description = "区块链地址") String address) {
        
        try {
            Map<String, Object> verification = identityService.verifyIdentity(did, address);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "身份验证完成");
            response.put("data", verification);
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("API: 验证 DID 身份成功 - DID: {}", did);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("API: 验证 DID 身份失败 - DID: {}", did, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 发行可验证凭证
     */
    @PostMapping("/credentials/issue")
    @Operation(summary = "发行可验证凭证", description = "发行新的可验证凭证")
    public ResponseEntity<?> issueCredential(
            @RequestBody Map<String, Object> request) {
        
        try {
            String issuerDid = (String) request.get("issuerDid");
            String holderDid = (String) request.get("holderDid");
            String credentialType = (String) request.get("credentialType");
            @SuppressWarnings("unchecked")
            Map<String, Object> credentialSubject = (Map<String, Object>) request.get("credentialSubject");
            
            VerifiableCredentialEntity credential = identityService.issueCredential(
                    issuerDid, holderDid, credentialType, credentialSubject);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "凭证发行成功");
            response.put("data", credential);
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("API: 发行可验证凭证成功 - 发行者: {}, 持有者: {}", issuerDid, holderDid);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("API: 发行可验证凭证失败", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 验证可验证凭证
     */
    @GetMapping("/credentials/{credentialId}/verify")
    @Operation(summary = "验证可验证凭证", description = "验证指定凭证的有效性")
    public ResponseEntity<?> verifyCredential(
            @PathVariable("credentialId") @Parameter(description = "凭证标识符") String credentialId) {
        
        try {
            Map<String, Object> verification = identityService.verifyCredential(credentialId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "凭证验证完成");
            response.put("data", verification);
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("API: 验证可验证凭证成功 - CredentialID: {}", credentialId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("API: 验证可验证凭证失败 - CredentialID: {}", credentialId, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 获取用户的所有 DID 身份
     */
    @GetMapping("/users/{userId}/identities")
    @Operation(summary = "获取用户身份列表", description = "获取指定用户的所有 DID 身份")
    public ResponseEntity<?> getUserIdentities(
            @PathVariable("userId") @Parameter(description = "用户ID") Long userId) {
        
        try {
            List<DecentralizedIdentityEntity> identities = identityService.getUserIdentities(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取身份列表成功");
            response.put("data", identities);
            response.put("count", identities.size());
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("API: 获取用户身份列表成功 - 用户ID: {}, 数量: {}", userId, identities.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("API: 获取用户身份列表失败 - 用户ID: {}", userId, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 获取用户的所有凭证
     */
    @GetMapping("/users/{did}/credentials")
    @Operation(summary = "获取用户凭证列表", description = "获取指定 DID 用户的所有可验证凭证")
    public ResponseEntity<?> getUserCredentials(
            @PathVariable("did") @Parameter(description = "用户 DID") String did) {
        
        try {
            List<VerifiableCredentialEntity> credentials = identityService.getUserCredentials(did);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取凭证列表成功");
            response.put("data", credentials);
            response.put("count", credentials.size());
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("API: 获取用户凭证列表成功 - DID: {}, 数量: {}", did, credentials.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("API: 获取用户凭证列表失败 - DID: {}", did, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 跨链同步身份
     */
    @PostMapping("/identities/{did}/sync")
    @Operation(summary = "跨链同步身份", description = "将身份同步到其他区块链网络")
    public ResponseEntity<?> syncIdentityCrossChain(
            @PathVariable("did") @Parameter(description = "DID 标识符") String did,
            @RequestBody Map<String, Object> request) {
        
        try {
            String targetBlockchainType = (String) request.get("targetBlockchainType");
            
            boolean success = identityService.syncIdentityCrossChain(did, targetBlockchainType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "跨链同步成功" : "跨链同步失败");
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("API: 跨链同步身份 {} - 目标链: {}, 结果: {}", 
                    did, targetBlockchainType, success ? "成功" : "失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("API: 跨链同步身份失败 - DID: {}", did, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 启用零知识证明
     */
    @PutMapping("/identities/{did}/zkp")
    @Operation(summary = "启用零知识证明", description = "为指定 DID 身份启用零知识证明功能")
    public ResponseEntity<?> enableZKP(
            @PathVariable("did") @Parameter(description = "DID 标识符") String did,
            @RequestBody Map<String, Object> request) {
        
        try {
            String zkpSchema = (String) request.get("zkpSchema");
            
            boolean success = identityService.enableZKP(did, zkpSchema);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "零知识证明启用成功" : "启用失败");
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("API: 启用零知识证明 - DID: {}, 模式: {}, 结果: {}", 
                    did, zkpSchema, success ? "成功" : "失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("API: 启用零知识证明失败 - DID: {}", did, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 更新声誉评分
     */
    @PutMapping("/identities/{did}/reputation")
    @Operation(summary = "更新声誉评分", description = "更新指定 DID 身份的声誉评分")
    public ResponseEntity<?> updateReputation(
            @PathVariable("did") @Parameter(description = "DID 标识符") String did,
            @RequestBody Map<String, Object> request) {
        
        try {
            Double scoreDelta = Double.valueOf(request.get("scoreDelta").toString());
            
            identityService.updateReputation(did, scoreDelta);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "声誉评分更新成功");
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("API: 更新声誉评分 - DID: {}, 变化值: {}", did, scoreDelta);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("API: 更新声誉评分失败 - DID: {}", did, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 获取身份详情
     */
    @GetMapping("/identities/{did}")
    @Operation(summary = "获取身份详情", description = "获取指定 DID 身份的详细信息")
    public ResponseEntity<?> getIdentityDetail(
            @PathVariable("did") @Parameter(description = "DID 标识符") String did) {
        
        try {
            Map<String, Object> verification = identityService.verifyIdentity(did, null);
            
            if (!(Boolean) verification.get("verified")) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "身份验证失败，无法获取详情");
                error.put("timestamp", LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取身份详情成功");
            response.put("data", verification);
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("API: 获取身份详情成功 - DID: {}", did);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("API: 获取身份详情失败 - DID: {}", did, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 搜索身份
     */
    @GetMapping("/identities/search")
    @Operation(summary = "搜索身份", description = "根据条件搜索 DID 身份")
    public ResponseEntity<?> searchIdentities(
            @RequestParam(value = "blockchainType", required = false) @Parameter(description = "区块链类型") String blockchainType,
            @RequestParam(value = "status", required = false) @Parameter(description = "状态") String status,
            @RequestParam(value = "minReputation", required = false) @Parameter(description = "最小声誉评分") Double minReputation,
            @RequestParam(value = "maxReputation", required = false) @Parameter(description = "最大声誉评分") Double maxReputation) {
        
        try {
            // 这里应该调用搜索服务，现在返回模拟数据
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "搜索完成");
            response.put("data", new HashMap<String, Object>() {{
                put("searchCriteria", new HashMap<String, Object>() {{
                    put("blockchainType", blockchainType);
                    put("status", status);
                    put("minReputation", minReputation);
                    put("maxReputation", maxReputation);
                }});
                put("resultsCount", 0);
                put("timestamp", LocalDateTime.now());
            }});
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("API: 搜索身份 - 条件: 类型={}, 状态={}, 声誉范围={}-{}", 
                    blockchainType, status, minReputation, maxReputation);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("API: 搜索身份失败", e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查 DID 服务运行状态")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Decentralized Identity Service");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }
}