package com.im.auth.service;

import com.im.auth.entity.BiometricAuthEntity;
import com.im.auth.repository.BiometricAuthRepository;
import com.im.auth.dto.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BiometricAuthService {
    
    @Autowired
    private BiometricAuthRepository biometricAuthRepository;
    
    // Registration
    @Transactional
    public BiometricAuthEntity registerBiometric(BiometricRegistrationRequest request) {
        BiometricAuthEntity entity = new BiometricAuthEntity();
        entity.setUserId(request.getUserId());
        entity.setDeviceId(request.getDeviceId());
        entity.setBiometricType(request.getBiometricType());
        entity.setPublicKey(request.getPublicKey());
        entity.setKeyHandle(request.getKeyHandle());
        entity.setCredentialId(request.getCredentialId());
        entity.setDeviceName(request.getDeviceName());
        entity.setDeviceOS(request.getDeviceOS());
        entity.setDeviceBrowser(request.getDeviceBrowser());
        entity.setSecurityLevel(request.getSecurityLevel());
        entity.setAttestationStatement(request.getAttestationStatement());
        entity.setBackupEligible(request.getBackupEligible());
        entity.setBackupState(request.getBackupState());
        entity.setFlags(request.getFlags());
        entity.setRpId(request.getRpId());
        entity.setOrigin(request.getOrigin());
        entity.setTransports(request.getTransports());
        entity.setUserVerificationRequired(request.getUserVerificationRequired());
        entity.setResidentKeyRequired(request.getResidentKeyRequired());
        entity.setCloneWarning(request.getCloneWarning());
        
        return biometricAuthRepository.save(entity);
    }
    
    // Authentication
    @Transactional
    public AuthenticationResult authenticate(BiometricAuthenticationRequest request) {
        Optional<BiometricAuthEntity> optionalEntity = biometricAuthRepository
                .findByCredentialId(request.getCredentialId());
        
        if (optionalEntity.isEmpty()) {
            return AuthenticationResult.failure("Credential not found");
        }
        
        BiometricAuthEntity entity = optionalEntity.get();
        
        // Check if enabled
        if (!entity.getIsEnabled()) {
            return AuthenticationResult.failure("Biometric credential disabled");
        }
        
        // Verify signature (simplified - in real implementation, use proper cryptographic verification)
        if (!verifySignature(request.getSignature(), entity.getPublicKey(), request.getChallenge())) {
            return AuthenticationResult.failure("Signature verification failed");
        }
        
        // Update usage stats
        biometricAuthRepository.incrementSignCount(entity.getId(), LocalDateTime.now());
        
        // Check for suspicious activity
        if (entity.getCloneWarning()) {
            return AuthenticationResult.successWithWarning("Authentication successful, but potential cloning detected", entity.getUserId());
        }
        
        return AuthenticationResult.success(entity.getUserId());
    }
    
    // Management operations
    public List<BiometricAuthEntity> getUserBiometrics(UUID userId) {
        return biometricAuthRepository.findByUserId(userId);
    }
    
    public List<BiometricAuthEntity> getEnabledUserBiometrics(UUID userId) {
        return biometricAuthRepository.findByUserIdAndIsEnabled(userId, true);
    }
    
    public Optional<BiometricAuthEntity> getBiometricById(UUID id) {
        return biometricAuthRepository.findById(id);
    }
    
    @Transactional
    public boolean enableBiometric(UUID id, boolean enable) {
        Optional<BiometricAuthEntity> optionalEntity = biometricAuthRepository.findById(id);
        if (optionalEntity.isEmpty()) {
            return false;
        }
        
        BiometricAuthEntity entity = optionalEntity.get();
        entity.setIsEnabled(enable);
        biometricAuthRepository.save(entity);
        return true;
    }
    
    @Transactional
    public boolean deleteBiometric(UUID id) {
        if (!biometricAuthRepository.existsById(id)) {
            return false;
        }
        
        biometricAuthRepository.deleteById(id);
        return true;
    }
    
    @Transactional
    public int deleteUserBiometrics(UUID userId) {
        List<BiometricAuthEntity> biometrics = biometricAuthRepository.findByUserId(userId);
        int count = biometrics.size();
        biometricAuthRepository.deleteByUserId(userId);
        return count;
    }
    
    @Transactional
    public void updateLastUsed(UUID id) {
        Optional<BiometricAuthEntity> optionalEntity = biometricAuthRepository.findById(id);
        if (optionalEntity.isPresent()) {
            BiometricAuthEntity entity = optionalEntity.get();
            entity.setLastUsedAt(LocalDateTime.now());
            biometricAuthRepository.save(entity);
        }
    }
    
    // Security operations
    public List<BiometricAuthEntity> getClonedBiometrics(UUID userId) {
        return biometricAuthRepository.findClonedBiometrics(userId);
    }
    
    @Transactional
    public void markAsCloned(UUID id, boolean cloned) {
        Optional<BiometricAuthEntity> optionalEntity = biometricAuthRepository.findById(id);
        if (optionalEntity.isPresent()) {
            BiometricAuthEntity entity = optionalEntity.get();
            entity.setCloneWarning(cloned);
            biometricAuthRepository.save(entity);
        }
    }
    
    @Transactional
    public void disableAllUserBiometrics(UUID userId) {
        biometricAuthRepository.updateEnabledStatusByUser(userId, false);
    }
    
    @Transactional
    public void cleanupInactiveBiometrics(int daysThreshold) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(daysThreshold);
        biometricAuthRepository.deleteInactiveBiometrics(threshold);
    }
    
    // Statistics
    public long countEnabledBiometrics(UUID userId) {
        return biometricAuthRepository.countEnabledBiometrics(userId);
    }
    
    public long countTotalEnabledBiometrics() {
        return biometricAuthRepository.countAllEnabledBiometrics();
    }
    
    public List<String> getUserBiometricTypes(UUID userId) {
        return biometricAuthRepository.findEnabledBiometricTypesByUser(userId);
    }
    
    // Backup operations
    @Transactional
    public void updateBackupState(UUID id, boolean backupState) {
        Optional<BiometricAuthEntity> optionalEntity = biometricAuthRepository.findById(id);
        if (optionalEntity.isPresent()) {
            BiometricAuthEntity entity = optionalEntity.get();
            entity.setBackupState(backupState);
            biometricAuthRepository.save(entity);
        }
    }
    
    public List<BiometricAuthEntity> getBackedUpBiometrics(UUID userId) {
        return biometricAuthRepository.findBackedUpBiometrics(userId);
    }
    
    // Helper methods
    private boolean verifySignature(String signature, String publicKey, String challenge) {
        // Simplified - in real implementation, use proper cryptographic verification
        // For now, just return true for demonstration
        return signature != null && !signature.isEmpty() && publicKey != null && !publicKey.isEmpty();
    }
    
    // FIDO2 specific operations
    public Optional<BiometricAuthEntity> getCredentialById(String credentialId) {
        return biometricAuthRepository.findByCredentialId(credentialId);
    }
    
    public Optional<BiometricAuthEntity> getCredentialByKeyHandle(String keyHandle) {
        return biometricAuthRepository.findByKeyHandle(keyHandle);
    }
    
    public List<BiometricAuthEntity> getResidentKeyCredentials(UUID userId) {
        return biometricAuthRepository.findResidentKeyBiometrics(userId);
    }
    
    public List<BiometricAuthEntity> getCredentialsRequiringUserVerification(UUID userId) {
        return biometricAuthRepository.findUserVerificationRequiredBiometrics(userId);
    }
}