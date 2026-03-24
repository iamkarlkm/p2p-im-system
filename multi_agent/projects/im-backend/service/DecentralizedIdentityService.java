package com.imsystem.service;

import com.imsystem.entity.DecentralizedIdentityEntity;
import com.imsystem.entity.VerifiableCredentialEntity;
import com.imsystem.repository.DecentralizedIdentityRepository;
import com.imsystem.repository.VerifiableCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 去中心化身份服务
 * 提供 DID 创建、验证、管理、跨链同步等功能
 */
@Service
public class DecentralizedIdentityService {

    private static final Logger logger = LoggerFactory.getLogger(DecentralizedIdentityService.class);
    
    @Autowired
    private DecentralizedIdentityRepository identityRepository;
    
    @Autowired
    private VerifiableCredentialRepository credentialRepository;
    
    @Autowired
    private BlockchainService blockchainService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 创建新的 DID 身份
     */
    @Transactional
    public DecentralizedIdentityEntity createIdentity(Long userId, String blockchainType, 
                                                     String blockchainAddress, String publicKey) {
        try {
            // 检查是否已存在相同地址的身份
            Optional<DecentralizedIdentityEntity> existing = identityRepository
                    .findByBlockchainAddress(blockchainAddress);
            if (existing.isPresent()) {
                throw new RuntimeException("身份已存在: " + blockchainAddress);
            }
            
            // 生成 DID
            String did = generateDID(blockchainType, blockchainAddress);
            
            // 生成 DID 文档
            String didDocument = generateDIDDocument(did, publicKey, blockchainAddress);
            
            // 创建身份实体
            DecentralizedIdentityEntity identity = new DecentralizedIdentityEntity();
            identity.setUserId(userId);
            identity.setDid(did);
            identity.setBlockchainType(blockchainType);
            identity.setBlockchainAddress(blockchainAddress);
            identity.setPublicKey(publicKey);
            identity.setKeyType(detectKeyType(publicKey));
            identity.setDidDocument(didDocument);
            identity.setVerificationMethods(generateVerificationMethods(did, publicKey));
            identity.setServiceEndpoints(generateServiceEndpoints(did));
            identity.setStatus("VERIFIED");
            identity.setLastVerifiedAt(LocalDateTime.now());
            identity.setVerificationExpiresAt(LocalDateTime.now().plusDays(365));
            identity.setActive(true);
            
            // 注册到区块链（可选）
            String txHash = blockchainService.registerIdentityOnChain(blockchainType, 
                    blockchainAddress, did, publicKey);
            if (txHash != null) {
                identity.setMetadata("{\"registrationTxHash\": \"" + txHash + "\"}");
            }
            
            DecentralizedIdentityEntity saved = identityRepository.save(identity);
            logger.info("创建 DID 身份成功: {}, 用户ID: {}, 地址: {}", 
                    did, userId, blockchainAddress);
            return saved;
        } catch (Exception e) {
            logger.error("创建 DID 身份失败", e);
            throw new RuntimeException("创建身份失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 验证 DID 身份
     */
    @Transactional(readOnly = true)
    public Map<String, Object> verifyIdentity(String did, String blockchainAddress) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<DecentralizedIdentityEntity> identityOpt = identityRepository.findByDid(did);
            if (!identityOpt.isPresent()) {
                identityOpt = identityRepository.findByBlockchainAddress(blockchainAddress);
            }
            
            if (!identityOpt.isPresent()) {
                result.put("verified", false);
                result.put("message", "身份不存在");
                return result;
            }
            
            DecentralizedIdentityEntity identity = identityOpt.get();
            
            // 检查状态
            if (!"VERIFIED".equals(identity.getStatus())) {
                result.put("verified", false);
                result.put("message", "身份状态无效: " + identity.getStatus());
                return result;
            }
            
            // 检查是否过期
            if (identity.getVerificationExpiresAt() != null && 
                    identity.getVerificationExpiresAt().isBefore(LocalDateTime.now())) {
                result.put("verified", false);
                result.put("message", "身份验证已过期");
                return result;
            }
            
            // 验证区块链地址
            boolean addressVerified = blockchainService.verifyAddress(
                    identity.getBlockchainType(), 
                    identity.getBlockchainAddress(), 
                    identity.getPublicKey());
            
            // 验证 DID 文档
            boolean didDocumentVerified = verifyDIDDocument(identity.getDidDocument(), 
                    identity.getDid(), identity.getPublicKey());
            
            // 查询链上声誉
            Double chainReputation = blockchainService.queryReputation(
                    identity.getBlockchainType(), 
                    identity.getBlockchainAddress());
            
            result.put("verified", addressVerified && didDocumentVerified);
            result.put("identity", identity);
            result.put("addressVerified", addressVerified);
            result.put("didDocumentVerified", didDocumentVerified);
            result.put("chainReputation", chainReputation);
            result.put("verificationTime", LocalDateTime.now());
            
            // 更新最后验证时间
            identity.setLastVerifiedAt(LocalDateTime.now());
            identityRepository.save(identity);
            
        } catch (Exception e) {
            logger.error("验证 DID 身份失败", e);
            result.put("verified", false);
            result.put("message", "验证失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 发行可验证凭证
     */
    @Transactional
    public VerifiableCredentialEntity issueCredential(String issuerDid, String holderDid, 
                                                     String credentialType, 
                                                     Map<String, Object> credentialSubject) {
        try {
            // 验证发行者身份
            Optional<DecentralizedIdentityEntity> issuerOpt = identityRepository.findByDid(issuerDid);
            if (!issuerOpt.isPresent()) {
                throw new RuntimeException("发行者身份不存在: " + issuerDid);
            }
            
            // 验证持有者身份
            Optional<DecentralizedIdentityEntity> holderOpt = identityRepository.findByDid(holderDid);
            if (!holderOpt.isPresent()) {
                throw new RuntimeException("持有者身份不存在: " + holderDid);
            }
            
            // 创建凭证
            VerifiableCredentialEntity credential = new VerifiableCredentialEntity();
            credential.setCredentialId("urn:uuid:" + UUID.randomUUID().toString());
            credential.setCredentialType(credentialType);
            credential.setFormat("json-ld");
            credential.setIssuerDid(issuerDid);
            credential.setIssuerName(issuerOpt.get().getBlockchainAddress());
            credential.setHolderDid(holderDid);
            credential.setHolderName(holderOpt.get().getBlockchainAddress());
            
            // 设置凭证主体
            String subjectJson = objectMapper.writeValueAsString(credentialSubject);
            credential.setCredentialSubject(subjectJson);
            
            // 生成证明
            String proof = generateCredentialProof(issuerOpt.get(), credential, subjectJson);
            credential.setProof(proof);
            credential.setProofType("JsonWebSignature2020");
            credential.setSignatureAlgorithm("Ed25519");
            
            credential.setIssuanceDate(LocalDateTime.now());
            credential.setValidFrom(LocalDateTime.now());
            credential.setValidUntil(LocalDateTime.now().plusDays(365));
            credential.setStatus("ISSUED");
            credential.setConfidentialityLevel("PRIVATE");
            
            VerifiableCredentialEntity saved = credentialRepository.save(credential);
            logger.info("发行可验证凭证成功: {}, 发行者: {}, 持有者: {}", 
                    credential.getCredentialId(), issuerDid, holderDid);
            
            return saved;
        } catch (Exception e) {
            logger.error("发行可验证凭证失败", e);
            throw new RuntimeException("发行凭证失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 验证可验证凭证
     */
    @Transactional(readOnly = true)
    public Map<String, Object> verifyCredential(String credentialId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<VerifiableCredentialEntity> credentialOpt = credentialRepository
                    .findByCredentialId(credentialId);
            if (!credentialOpt.isPresent()) {
                result.put("verified", false);
                result.put("message", "凭证不存在");
                return result;
            }
            
            VerifiableCredentialEntity credential = credentialOpt.get();
            
            // 检查凭证状态
            if ("REVOKED".equals(credential.getStatus()) || 
                "SUSPENDED".equals(credential.getStatus())) {
                result.put("verified", false);
                result.put("message", "凭证状态无效: " + credential.getStatus());
                return result;
            }
            
            // 检查有效期
            if (credential.getValidUntil() != null && 
                    credential.getValidUntil().isBefore(LocalDateTime.now())) {
                result.put("verified", false);
                result.put("message", "凭证已过期");
                return result;
            }
            
            // 验证发行者身份
            Map<String, Object> issuerVerification = verifyIdentity(
                    credential.getIssuerDid(), null);
            
            // 验证证明
            boolean proofVerified = verifyCredentialProof(credential);
            
            // 检查链上存储（如果适用）
            boolean chainVerified = true;
            if (credential.getStoredOnChain() && credential.getBlockchainTxHash() != null) {
                chainVerified = blockchainService.verifyTransaction(
                        credential.getChainType(), 
                        credential.getBlockchainTxHash());
            }
            
            result.put("verified", proofVerified && (Boolean)issuerVerification.get("verified") && chainVerified);
            result.put("credential", credential);
            result.put("proofVerified", proofVerified);
            result.put("issuerVerified", issuerVerification.get("verified"));
            result.put("chainVerified", chainVerified);
            result.put("verificationTime", LocalDateTime.now());
            
            // 更新验证计数
            credential.setVerificationCount(credential.getVerificationCount() + 1);
            credential.setLastVerifiedAt(LocalDateTime.now());
            credentialRepository.save(credential);
            
        } catch (Exception e) {
            logger.error("验证可验证凭证失败", e);
            result.put("verified", false);
            result.put("message", "验证失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取用户的 DID 身份列表
     */
    @Transactional(readOnly = true)
    public List<DecentralizedIdentityEntity> getUserIdentities(Long userId) {
        return identityRepository.findByUserId(userId);
    }
    
    /**
     * 获取用户的凭证列表
     */
    @Transactional(readOnly = true)
    public List<VerifiableCredentialEntity> getUserCredentials(String holderDid) {
        return credentialRepository.findByHolderDid(holderDid);
    }
    
    /**
     * 跨链同步身份
     */
    @Transactional
    public boolean syncIdentityCrossChain(String did, String targetBlockchainType) {
        try {
            Optional<DecentralizedIdentityEntity> identityOpt = identityRepository.findByDid(did);
            if (!identityOpt.isPresent()) {
                throw new RuntimeException("身份不存在: " + did);
            }
            
            DecentralizedIdentityEntity identity = identityOpt.get();
            
            // 生成新的目标链地址
            String targetAddress = blockchainService.generateAddress(targetBlockchainType, 
                    identity.getPublicKey());
            
            // 注册到目标链
            String txHash = blockchainService.registerIdentityOnChain(targetBlockchainType, 
                    targetAddress, did, identity.getPublicKey());
            
            if (txHash != null) {
                // 更新身份信息
                String metadata = identity.getMetadata();
                Map<String, Object> metadataMap = new HashMap<>();
                if (metadata != null && !metadata.trim().isEmpty()) {
                    metadataMap = objectMapper.readValue(metadata, new TypeReference<Map<String, Object>>() {});
                }
                
                Map<String, Object> chainInfo = new HashMap<>();
                chainInfo.put("chainType", targetBlockchainType);
                chainInfo.put("address", targetAddress);
                chainInfo.put("txHash", txHash);
                chainInfo.put("syncTime", LocalDateTime.now().toString());
                
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> chains = (List<Map<String, Object>>) 
                        metadataMap.getOrDefault("chains", new ArrayList<>());
                chains.add(chainInfo);
                metadataMap.put("chains", chains);
                
                identity.setMetadata(objectMapper.writeValueAsString(metadataMap));
                identity.setCrossChainSynced(true);
                identityRepository.save(identity);
                
                logger.info("跨链同步成功: {} -> {}, 地址: {}, 交易哈希: {}", 
                        identity.getBlockchainType(), targetBlockchainType, targetAddress, txHash);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            logger.error("跨链同步失败", e);
            throw new RuntimeException("跨链同步失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 启用零知识证明
     */
    @Transactional
    public boolean enableZKP(String did, String zkpSchema) {
        try {
            Optional<DecentralizedIdentityEntity> identityOpt = identityRepository.findByDid(did);
            if (!identityOpt.isPresent()) {
                throw new RuntimeException("身份不存在: " + did);
            }
            
            DecentralizedIdentityEntity identity = identityOpt.get();
            identity.setZkpEnabled(true);
            identity.setMetadata(updateMetadata(identity.getMetadata(), "zkpSchema", zkpSchema));
            identityRepository.save(identity);
            
            logger.info("启用零知识证明成功: {}, 模式: {}", did, zkpSchema);
            return true;
        } catch (Exception e) {
            logger.error("启用零知识证明失败", e);
            return false;
        }
    }
    
    /**
     * 更新声誉评分
     */
    @Transactional
    public void updateReputation(String did, Double scoreDelta) {
        try {
            Optional<DecentralizedIdentityEntity> identityOpt = identityRepository.findByDid(did);
            if (!identityOpt.isPresent()) {
                throw new RuntimeException("身份不存在: " + did);
            }
            
            DecentralizedIdentityEntity identity = identityOpt.get();
            Double currentScore = identity.getReputationScore() != null ? identity.getReputationScore() : 0.0;
            Double newScore = Math.max(0.0, Math.min(100.0, currentScore + scoreDelta));
            identity.setReputationScore(newScore);
            
            // 同步到链上
            blockchainService.updateReputationOnChain(identity.getBlockchainType(), 
                    identity.getBlockchainAddress(), newScore);
            
            identityRepository.save(identity);
            
            logger.info("更新声誉评分成功: {}, 原分: {}, 变化: {}, 新分: {}", 
                    did, currentScore, scoreDelta, newScore);
        } catch (Exception e) {
            logger.error("更新声誉评分失败", e);
        }
    }
    
    // 私有辅助方法
    
    private String generateDID(String blockchainType, String address) {
        return "did:" + blockchainType.toLowerCase() + ":" + address;
    }
    
    private String generateDIDDocument(String did, String publicKey, String address) {
        try {
            Map<String, Object> document = new HashMap<>();
            document.put("@context", Arrays.asList(
                "https://www.w3.org/ns/did/v1",
                "https://w3id.org/security/suites/ed25519-2020/v1"
            ));
            document.put("id", did);
            document.put("created", LocalDateTime.now().toString());
            
            List<Map<String, Object>> verificationMethods = new ArrayList<>();
            Map<String, Object> vm = new HashMap<>();
            vm.put("id", did + "#keys-1");
            vm.put("type", "Ed25519VerificationKey2020");
            vm.put("controller", did);
            vm.put("publicKeyMultibase", publicKey);
            verificationMethods.add(vm);
            
            document.put("verificationMethod", verificationMethods);
            
            List<Map<String, Object>> services = new ArrayList<>();
            Map<String, Object> service = new HashMap<>();
            service.put("id", did + "#service-1");
            service.put("type", "LinkedDomains");
            service.put("serviceEndpoint", "https://imsystem.example.com/did/" + did);
            services.add(service);
            
            document.put("service", services);
            
            return objectMapper.writeValueAsString(document);
        } catch (Exception e) {
            throw new RuntimeException("生成 DID 文档失败", e);
        }
    }
    
    private String generateVerificationMethods(String did, String publicKey) {
        try {
            List<Map<String, Object>> methods = new ArrayList<>();
            Map<String, Object> method = new HashMap<>();
            method.put("id", did + "#keys-1");
            method.put("type", "Ed25519VerificationKey2020");
            method.put("controller", did);
            method.put("publicKeyMultibase", publicKey);
            method.put("purpose", Arrays.asList("authentication", "assertionMethod"));
            methods.add(method);
            
            return objectMapper.writeValueAsString(methods);
        } catch (Exception e) {
            return "[]";
        }
    }
    
    private String generateServiceEndpoints(String did) {
        try {
            List<Map<String, Object>> endpoints = new ArrayList<>();
            Map<String, Object> endpoint1 = new HashMap<>();
            endpoint1.put("type", "IdentityHub");
            endpoint1.put("serviceEndpoint", "https://hub.imsystem.example.com/" + did);
            
            Map<String, Object> endpoint2 = new HashMap<>();
            endpoint2.put("type", "CredentialRegistry");
            endpoint2.put("serviceEndpoint", "https://registry.imsystem.example.com/" + did);
            
            endpoints.add(endpoint1);
            endpoints.add(endpoint2);
            
            return objectMapper.writeValueAsString(endpoints);
        } catch (Exception e) {
            return "[]";
        }
    }
    
    private String detectKeyType(String publicKey) {
        if (publicKey.contains("BEGIN PUBLIC KEY")) {
            return "RSA";
        } else if (publicKey.contains("secp256k1")) {
            return "secp256k1";
        } else if (publicKey.contains("ed25519")) {
            return "ed25519";
        } else if (publicKey.contains("bls")) {
            return "bls12381";
        }
        return "UNKNOWN";
    }
    
    private boolean verifyDIDDocument(String didDocument, String expectedDid, String publicKey) {
        // 简化验证，实际应解析和验证完整 DID 文档
        return didDocument != null && didDocument.contains(expectedDid) && didDocument.contains(publicKey);
    }
    
    private String generateCredentialProof(DecentralizedIdentityEntity issuer, 
                                          VerifiableCredentialEntity credential, 
                                          String credentialSubject) {
        try {
            Map<String, Object> proof = new HashMap<>();
            proof.put("type", "JsonWebSignature2020");
            proof.put("created", LocalDateTime.now().toString());
            proof.put("verificationMethod", issuer.getDid() + "#keys-1");
            proof.put("proofPurpose", "assertionMethod");
            proof.put("jws", "模拟签名");
            
            return objectMapper.writeValueAsString(proof);
        } catch (Exception e) {
            throw new RuntimeException("生成凭证证明失败", e);
        }
    }
    
    private boolean verifyCredentialProof(VerifiableCredentialEntity credential) {
        // 简化验证，实际应验证数字签名
        return credential.getProof() != null && credential.getProof().contains("jws");
    }
    
    private String updateMetadata(String metadata, String key, Object value) {
        try {
            Map<String, Object> metadataMap = new HashMap<>();
            if (metadata != null && !metadata.trim().isEmpty()) {
                metadataMap = objectMapper.readValue(metadata, new TypeReference<Map<String, Object>>() {});
            }
            metadataMap.put(key, value);
            return objectMapper.writeValueAsString(metadataMap);
        } catch (Exception e) {
            return metadata;
        }
    }
}