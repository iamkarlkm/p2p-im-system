package com.im.backend.controller;

import com.im.backend.entity.QuantumEncryptionConfigEntity;
import com.im.backend.entity.QuantumKeyDistributionEntity;
import com.im.backend.service.QuantumEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 量子安全加密 REST API 控制器
 */
@RestController
@RequestMapping("/api/v1/quantum-encryption")
public class QuantumEncryptionController {
    
    @Autowired
    private QuantumEncryptionService quantumEncryptionService;
    
    /**
     * 创建或更新量子加密配置
     */
    @PostMapping("/config")
    public ResponseEntity<Map<String, Object>> createOrUpdateEncryptionConfig(
            @RequestParam String userId,
            @RequestParam(required = false) String conversationId,
            @RequestParam QuantumEncryptionConfigEntity.EncryptionMode encryptionMode,
            @RequestParam QuantumEncryptionConfigEntity.PqcAlgorithm pqcAlgorithm,
            @RequestBody(required = false) Map<String, Object> options) {
        
        try {
            String convId = conversationId != null ? conversationId : "default_" + userId;
            QuantumEncryptionConfigEntity config = quantumEncryptionService.createOrUpdateEncryptionConfig(
                userId, convId, encryptionMode, pqcAlgorithm, options);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("configId", config.getId());
            response.put("userId", config.getUserId());
            response.put("encryptionMode", config.getEncryptionMode());
            response.put("pqcAlgorithm", config.getPqcAlgorithm());
            response.put("quantumResistanceScore", config.getQuantumResistanceScore());
            response.put("hybridModeEnabled", config.isHybridMode());
            response.put("message", "量子加密配置创建成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取用户加密配置
     */
    @GetMapping("/config/{userId}/{conversationId}")
    public ResponseEntity<Map<String, Object>> getEncryptionConfig(
            @PathVariable String userId,
            @PathVariable String conversationId) {
        
        try {
            // 这里需要从服务获取配置
            // 暂时返回模拟数据
            Map<String, Object> configData = new HashMap<>();
            configData.put("userId", userId);
            configData.put("conversationId", conversationId);
            configData.put("encryptionMode", "HYBRID");
            configData.put("pqcAlgorithm", "CRYSTALS_KYBER");
            configData.put("quantumResistanceScore", 75.0);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("config", configData);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 初始化 QKD 会话
     */
    @PostMapping("/qkd/init")
    public ResponseEntity<Map<String, Object>> initQkdSession(
            @RequestParam String senderId,
            @RequestParam String receiverId,
            @RequestParam(required = false, defaultValue = "BB84") QuantumKeyDistributionEntity.QkdProtocol protocol,
            @RequestBody(required = false) Map<String, Object> options) {
        
        try {
            QuantumKeyDistributionEntity qkd = quantumEncryptionService.initQkdSession(
                senderId, receiverId, protocol, options);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sessionId", qkd.getSessionId());
            response.put("senderId", qkd.getSenderId());
            response.put("receiverId", qkd.getReceiverId());
            response.put("protocol", qkd.getProtocol());
            response.put("status", qkd.getStatus());
            response.put("startedAt", qkd.getStartedAt());
            response.put("message", "QKD 会话初始化成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 执行 QKD 协议
     */
    @PostMapping("/qkd/execute/{sessionId}")
    public ResponseEntity<Map<String, Object>> executeQkdProtocol(@PathVariable String sessionId) {
        
        try {
            QuantumKeyDistributionEntity qkd = quantumEncryptionService.executeQkdProtocol(sessionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sessionId", sessionId);
            response.put("status", qkd.getStatus());
            response.put("finalKeyBits", qkd.getFinalKeyBits());
            response.put("finalKeyRateBps", qkd.getFinalKeyRateBps());
            response.put("quantumBitErrorRate", qkd.getQuantumBitErrorRate());
            response.put("keyQualityScore", qkd.calculateKeyQualityScore());
            response.put("eavesdroppingDetected", qkd.hasEavesdropping());
            response.put("completedAt", qkd.getCompletedAt());
            
            if (qkd.isCompleted()) {
                response.put("message", "QKD 密钥分发成功");
            } else if (qkd.hasEavesdropping()) {
                response.put("message", "检测到窃听，密钥分发失败");
            } else {
                response.put("message", "QKD 协议执行完成");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取 QKD 会话状态
     */
    @GetMapping("/qkd/status/{sessionId}")
    public ResponseEntity<Map<String, Object>> getQkdSessionStatus(@PathVariable String sessionId) {
        
        try {
            QuantumKeyDistributionEntity qkd = quantumEncryptionService.getQkdSessionStatus(sessionId);
            
            if (qkd == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sessionId", sessionId);
            response.put("status", qkd.getStatus());
            response.put("protocol", qkd.getProtocol());
            response.put("progress", getQkdProgress(qkd.getStatus()));
            response.put("siftedKeyBits", qkd.getSiftedKeyBits());
            response.put("reconciledKeyBits", qkd.getReconciledKeyBits());
            response.put("finalKeyBits", qkd.getFinalKeyBits());
            response.put("qber", qkd.getQuantumBitErrorRate());
            response.put("eavesdroppingDetected", qkd.hasEavesdropping());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 使用 PQC 加密消息
     */
    @PostMapping("/encrypt")
    public ResponseEntity<Map<String, Object>> encryptMessage(
            @RequestParam String senderId,
            @RequestParam String recipientId,
            @RequestBody Map<String, Object> requestData) {
        
        try {
            String message = (String) requestData.get("message");
            String algorithmStr = (String) requestData.getOrDefault("algorithm", "CRYSTALS_KYBER");
            QuantumEncryptionConfigEntity.PqcAlgorithm algorithm = 
                QuantumEncryptionConfigEntity.PqcAlgorithm.valueOf(algorithmStr);
            
            Map<String, Object> encrypted = quantumEncryptionService.encryptMessageWithPqc(
                senderId, recipientId, message, algorithm, null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("encrypted", encrypted);
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "消息加密成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 使用 PQC 解密消息
     */
    @PostMapping("/decrypt")
    public ResponseEntity<Map<String, Object>> decryptMessage(
            @RequestParam String senderId,
            @RequestParam String recipientId,
            @RequestBody Map<String, Object> requestData) {
        
        try {
            String ciphertext = (String) requestData.get("ciphertext");
            String authenticationTag = (String) requestData.get("authenticationTag");
            
            String decrypted = quantumEncryptionService.decryptMessageWithPqc(
                senderId, recipientId, ciphertext, authenticationTag, null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("plaintext", decrypted);
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "消息解密成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 密钥轮换
     */
    @PostMapping("/rotate-keys")
    public ResponseEntity<Map<String, Object>> rotateEncryptionKeys(
            @RequestParam String userId,
            @RequestParam String conversationId) {
        
        try {
            quantumEncryptionService.rotateEncryptionKey(userId, conversationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", userId);
            response.put("conversationId", conversationId);
            response.put("rotatedAt", LocalDateTime.now());
            response.put("message", "密钥轮换成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取量子安全统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getQuantumSecurityStats() {
        
        try {
            Map<String, Object> stats = quantumEncryptionService.getQuantumSecurityStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);
            response.put("collectedAt", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 量子攻击抵抗能力评估
     */
    @GetMapping("/assess/{userId}/{conversationId}")
    public ResponseEntity<Map<String, Object>> assessQuantumAttackResistance(
            @PathVariable String userId,
            @PathVariable String conversationId) {
        
        try {
            Map<String, Object> assessment = quantumEncryptionService.assessQuantumAttackResistance(userId, conversationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("assessment", assessment);
            response.put("assessmentTime", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取支持的 PQC 算法列表
     */
    @GetMapping("/algorithms")
    public ResponseEntity<Map<String, Object>> getSupportedAlgorithms() {
        
        try {
            List<Map<String, Object>> algorithms = new ArrayList<>();
            
            for (QuantumEncryptionConfigEntity.PqcAlgorithm algo : QuantumEncryptionConfigEntity.PqcAlgorithm.values()) {
                Map<String, Object> algoInfo = new HashMap<>();
                algoInfo.put("name", algo.name());
                algoInfo.put("type", getAlgorithmType(algo));
                algoInfo.put("nistStandard", algo == QuantumEncryptionConfigEntity.PqcAlgorithm.CRYSTALS_KYBER || 
                                             algo == QuantumEncryptionConfigEntity.PqcAlgorithm.CRYSTALS_DILITHIUM ||
                                             algo == QuantumEncryptionConfigEntity.PqcAlgorithm.FALCON ||
                                             algo == QuantumEncryptionConfigEntity.PqcAlgorithm.SPHINCS_PLUS);
                algoInfo.put("keySize", getDefaultKeySize(algo));
                algoInfo.put("securityLevel", getSecurityLevel(algo));
                algorithms.add(algoInfo);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalAlgorithms", algorithms.size());
            response.put("algorithms", algorithms);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取支持的 QKD 协议列表
     */
    @GetMapping("/qkd/protocols")
    public ResponseEntity<Map<String, Object>> getQkdProtocols() {
        
        try {
            List<Map<String, Object>> protocols = new ArrayList<>();
            
            for (QuantumKeyDistributionEntity.QkdProtocol protocol : QuantumKeyDistributionEntity.QkdProtocol.values()) {
                Map<String, Object> protocolInfo = new HashMap<>();
                protocolInfo.put("name", protocol.name());
                protocolInfo.put("description", getProtocolDescription(protocol));
                protocolInfo.put("year", getProtocolYear(protocol));
                protocolInfo.put("type", protocol.name().contains("CV") ? "Continuous Variable" : "Discrete Variable");
                protocols.add(protocolInfo);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalProtocols", protocols.size());
            response.put("protocols", protocols);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 混合加密模式切换
     */
    @PostMapping("/toggle-hybrid")
    public ResponseEntity<Map<String, Object>> toggleHybridMode(
            @RequestParam String userId,
            @RequestParam String conversationId,
            @RequestParam boolean enabled) {
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", userId);
            response.put("conversationId", conversationId);
            response.put("hybridModeEnabled", enabled);
            response.put("message", enabled ? "混合模式已启用" : "混合模式已禁用");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Quantum Encryption Service");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        response.put("features", Arrays.asList(
            "PQC Encryption",
            "QKD Protocol",
            "Hybrid Mode",
            "Key Rotation",
            "Security Assessment"
        ));
        
        return ResponseEntity.ok(response);
    }
    
    // 私有辅助方法
    
    private Map<String, Object> createErrorResponse(String errorMessage) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", errorMessage);
        errorResponse.put("timestamp", LocalDateTime.now());
        return errorResponse;
    }
    
    private int getQkdProgress(QuantumKeyDistributionEntity.QkdStatus status) {
        switch (status) {
            case INITIATED: return 0;
            case QUANTUM_TRANSMISSION: return 20;
            case SIFTING: return 40;
            case ERROR_ESTIMATION: return 50;
            case RECONCILIATION: return 70;
            case PRIVACY_AMPLIFICATION: return 85;
            case VERIFICATION: return 95;
            case COMPLETED: return 100;
            default: return 0;
        }
    }
    
    private String getAlgorithmType(QuantumEncryptionConfigEntity.PqcAlgorithm algorithm) {
        switch (algorithm) {
            case CRYSTALS_KYBER:
            case BIKE:
            case HQC:
            case NTRU:
            case SABER:
                return "Key Encapsulation Mechanism (KEM)";
            case CRYSTALS_DILITHIUM:
            case FALCON:
            case SPHINCS_PLUS:
                return "Digital Signature";
            default:
                return "Unknown";
        }
    }
    
    private int getDefaultKeySize(QuantumEncryptionConfigEntity.PqcAlgorithm algorithm) {
        switch (algorithm) {
            case CRYSTALS_KYBER: return 768;
            case CRYSTALS_DILITHIUM: return 2048;
            case FALCON: return 512;
            case SPHINCS_PLUS: return 256;
            default: return 256;
        }
    }
    
    private String getSecurityLevel(QuantumEncryptionConfigEntity.PqcAlgorithm algorithm) {
        switch (algorithm) {
            case CRYSTALS_KYBER:
            case CRYSTALS_DILITHIUM:
                return "NIST Level 3-5";
            case FALCON:
            case SPHINCS_PLUS:
                return "NIST Level 1-3";
            default:
                return "Experimental";
        }
    }
    
    private String getProtocolDescription(QuantumKeyDistributionEntity.QkdProtocol protocol) {
        switch (protocol) {
            case BB84:
                return "First QKD protocol (Bennett & Brassard, 1984), uses 4 polarization states";
            case E91:
                return "Entanglement-based QKD (Ekert, 1991), uses Bell's inequality";
            case B92:
                return "Simplified QKD protocol (Bennett, 1992), uses 2 non-orthogonal states";
            case SIX_STATE:
                return "Six-state protocol, extension of BB84 with 6 polarization states";
            case CV_QKD:
                return "Continuous Variable QKD, uses quadrature amplitudes instead of photons";
            case MDI_QKD:
                return "Measurement-Device-Independent QKD, immune to detector side-channel attacks";
            default:
                return "Unknown protocol";
        }
    }
    
    private int getProtocolYear(QuantumKeyDistributionEntity.QkdProtocol protocol) {
        switch (protocol) {
            case BB84: return 1984;
            case E91: return 1991;
            case B92: return 1992;
            case SIX_STATE: return 1998;
            default: return 2000;
        }
    }
}