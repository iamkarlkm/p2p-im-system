package com.im.system.controller;

import com.im.system.entity.ZKPVerifiableComputationEntity;
import com.im.system.entity.ZKPPrivacyProtectionEntity;
import com.im.system.service.ZKPVerifiableComputationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/zkp")
public class ZKPVerifiableComputationController {

    @Autowired
    private ZKPVerifiableComputationService zkpService;

    @PostMapping("/computations/create")
    public ResponseEntity<?> createComputation(
            @RequestParam String userId,
            @RequestParam(required = false) String sessionId,
            @RequestParam String computationType,
            @RequestParam(defaultValue = "GROTH16") String circuitType,
            @RequestParam(defaultValue = "128") Integer securityLevel) {
        
        try {
            ZKPVerifiableComputationEntity entity = zkpService.createComputation(
                userId, sessionId, computationType, circuitType, securityLevel);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "computationId", entity.getComputationId(),
                "status", entity.getComputationStatus(),
                "createdAt", entity.getCreatedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/computations/{computationId}/generate-proof")
    public ResponseEntity<?> generateProof(
            @PathVariable String computationId,
            @RequestBody Map<String, Object> inputs) {
        
        try {
            Map<String, Object> publicInputs = (Map<String, Object>) inputs.get("public");
            Map<String, Object> privateInputs = (Map<String, Object>) inputs.get("private");
            
            ZKPVerifiableComputationEntity entity = zkpService.generateProof(
                computationId, publicInputs, privateInputs);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "computationId", entity.getComputationId(),
                "proofGenerated", entity.getProofGenerated(),
                "generationTimeMs", entity.getGenerationTimeMs(),
                "proofSizeBytes", entity.getProofSizeBytes(),
                "status", entity.getComputationStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/computations/{computationId}/verify")
    public ResponseEntity<?> verifyProof(@PathVariable String computationId) {
        try {
            ZKPVerifiableComputationEntity entity = zkpService.verifyProof(computationId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "computationId", entity.getComputationId(),
                "verified", entity.getProofVerified(),
                "verificationTimeMs", entity.getVerificationTimeMs(),
                "status", entity.getComputationStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/computations/{computationId}")
    public ResponseEntity<?> getComputation(@PathVariable String computationId) {
        ZKPVerifiableComputationEntity entity = zkpService.getComputationById(computationId);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "computation", entity
        ));
    }

    @GetMapping("/users/{userId}/computations")
    public ResponseEntity<?> getUserComputations(
            @PathVariable String userId,
            @RequestParam(defaultValue = "20") Integer limit) {
        
        List<ZKPVerifiableComputationEntity> computations = zkpService.getUserComputations(userId, limit);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "count", computations.size(),
            "computations", computations
        ));
    }

    @PostMapping("/privacy-protections/create")
    public ResponseEntity<?> createPrivacyProtection(
            @RequestParam String userId,
            @RequestParam String protectionType,
            @RequestBody Map<String, Object> request) {
        
        try {
            Map<String, Object> attributes = (Map<String, Object>) request.get("attributes");
            String validToStr = (String) request.get("validTo");
            LocalDateTime validTo = validToStr != null ? LocalDateTime.parse(validToStr) : 
                LocalDateTime.now().plusYears(1);
            
            ZKPPrivacyProtectionEntity entity = zkpService.createPrivacyProtection(
                userId, protectionType, attributes, validTo);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "protectionId", entity.getProtectionId(),
                "status", entity.getProtectionStatus(),
                "validFrom", entity.getValidFrom(),
                "validTo", entity.getValidTo()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/privacy-protections/{protectionId}/verify")
    public ResponseEntity<?> verifyPrivacyProtection(
            @PathVariable String protectionId,
            @RequestBody Map<String, Object> request) {
        
        try {
            Map<String, Object> disclosedAttributes = (Map<String, Object>) request.get("disclosedAttributes");
            List<Map<String, Object>> predicates = (List<Map<String, Object>>) request.get("predicates");
            
            ZKPPrivacyProtectionEntity entity = zkpService.verifyPrivacyProtection(
                protectionId, disclosedAttributes, predicates);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "protectionId", entity.getProtectionId(),
                "verified", entity.getVerificationResult(),
                "verificationScore", entity.getVerificationScore(),
                "privacyPreservationScore", entity.getPrivacyPreservationScore(),
                "status", entity.getProtectionStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/privacy-protections/{protectionId}")
    public ResponseEntity<?> getPrivacyProtection(@PathVariable String protectionId) {
        ZKPPrivacyProtectionEntity entity = zkpService.getProtectionById(protectionId);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "protection", entity
        ));
    }

    @GetMapping("/users/{userId}/privacy-protections")
    public ResponseEntity<?> getUserPrivacyProtections(
            @PathVariable String userId,
            @RequestParam(defaultValue = "20") Integer limit) {
        
        List<ZKPPrivacyProtectionEntity> protections = zkpService.getUserProtections(userId, limit);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "count", protections.size(),
            "protections", protections
        ));
    }

    @PostMapping("/computations/batch-verify")
    public ResponseEntity<?> batchVerifyProofs(@RequestBody List<String> computationIds) {
        try {
            Map<String, Object> results = new HashMap<>();
            int verified = 0;
            int failed = 0;
            
            for (String computationId : computationIds) {
                try {
                    ZKPVerifiableComputationEntity entity = zkpService.verifyProof(computationId);
                    if (Boolean.TRUE.equals(entity.getProofVerified())) {
                        verified++;
                    } else {
                        failed++;
                    }
                } catch (Exception e) {
                    failed++;
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "total", computationIds.size(),
                "verified", verified,
                "failed", failed,
                "successRate", (double) verified / computationIds.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/statistics/overview")
    public ResponseEntity<?> getStatisticsOverview() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("timestamp", LocalDateTime.now());
        stats.put("activeComputations", 0);
        stats.put("totalProofsGenerated", 0);
        stats.put("totalProofsVerified", 0);
        stats.put("averageGenerationTimeMs", 0);
        stats.put("averageVerificationTimeMs", 0);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "statistics", stats
        ));
    }
}