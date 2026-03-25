package com.im.auth.repository;

import com.im.auth.entity.BiometricAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BiometricAuthRepository extends JpaRepository<BiometricAuthEntity, UUID> {
    
    // Basic queries
    List<BiometricAuthEntity> findByUserId(UUID userId);
    
    List<BiometricAuthEntity> findByUserIdAndIsEnabled(UUID userId, Boolean isEnabled);
    
    List<BiometricAuthEntity> findByUserIdAndBiometricType(UUID userId, String biometricType);
    
    Optional<BiometricAuthEntity> findByUserIdAndDeviceId(UUID userId, String deviceId);
    
    Optional<BiometricAuthEntity> findByCredentialId(String credentialId);
    
    Optional<BiometricAuthEntity> findByKeyHandle(String keyHandle);
    
    List<BiometricAuthEntity> findByBiometricType(String biometricType);
    
    List<BiometricAuthEntity> findByDeviceOS(String deviceOS);
    
    List<BiometricAuthEntity> findBySecurityLevel(String securityLevel);
    
    // Complex queries
    @Query("SELECT b FROM BiometricAuthEntity b WHERE b.userId = :userId AND b.isEnabled = true AND b.lastUsedAt >= :since")
    List<BiometricAuthEntity> findRecentActiveBiometrics(@Param("userId") UUID userId, @Param("since") java.time.LocalDateTime since);
    
    @Query("SELECT b FROM BiometricAuthEntity b WHERE b.userId = :userId AND b.isEnabled = true AND b.biometricType IN :types")
    List<BiometricAuthEntity> findEnabledBiometricsByTypes(@Param("userId") UUID userId, @Param("types") List<String> types);
    
    @Query("SELECT COUNT(b) FROM BiometricAuthEntity b WHERE b.userId = :userId AND b.isEnabled = true")
    Long countEnabledBiometrics(@Param("userId") UUID userId);
    
    @Query("SELECT b FROM BiometricAuthEntity b WHERE b.userId = :userId AND b.isEnabled = true AND b.securityLevel = :level")
    List<BiometricAuthEntity> findEnabledBiometricsBySecurityLevel(@Param("userId") UUID userId, @Param("level") String level);
    
    @Query("SELECT b FROM BiometricAuthEntity b WHERE b.userId = :userId AND b.isEnabled = true AND b.residentKeyRequired = true")
    List<BiometricAuthEntity> findResidentKeyBiometrics(@Param("userId") UUID userId);
    
    @Query("SELECT b FROM BiometricAuthEntity b WHERE b.userId = :userId AND b.isEnabled = true AND b.userVerificationRequired = true")
    List<BiometricAuthEntity> findUserVerificationRequiredBiometrics(@Param("userId") UUID userId);
    
    @Query("SELECT b FROM BiometricAuthEntity b WHERE b.isEnabled = true AND b.lastUsedAt < :threshold")
    List<BiometricAuthEntity> findInactiveBiometrics(@Param("threshold") java.time.LocalDateTime threshold);
    
    @Query("SELECT b FROM BiometricAuthEntity b WHERE b.userId = :userId AND b.isEnabled = true AND b.cloneWarning = true")
    List<BiometricAuthEntity> findClonedBiometrics(@Param("userId") UUID userId);
    
    @Query("SELECT b FROM BiometricAuthEntity b WHERE b.userId = :userId AND b.isEnabled = true ORDER BY b.lastUsedAt DESC NULLS LAST")
    List<BiometricAuthEntity> findRecentBiometricsByUser(@Param("userId") UUID userId);
    
    @Query("SELECT b FROM BiometricAuthEntity b WHERE b.isEnabled = true AND b.signCount > :minSignCount")
    List<BiometricAuthEntity> findFrequentlyUsedBiometrics(@Param("minSignCount") Integer minSignCount);
    
    @Query("SELECT b FROM BiometricAuthEntity b WHERE b.userId = :userId AND b.isEnabled = true AND b.backupEligible = true AND b.backupState = true")
    List<BiometricAuthEntity> findBackedUpBiometrics(@Param("userId") UUID userId);
    
    @Query("SELECT b FROM BiometricAuthEntity b WHERE b.userId = :userId AND b.isEnabled = true AND b.deviceId = :deviceId AND b.biometricType = :type")
    Optional<BiometricAuthEntity> findSpecificDeviceBiometric(@Param("userId") UUID userId, 
                                                             @Param("deviceId") String deviceId, 
                                                             @Param("type") String type);
    
    @Query("SELECT b FROM BiometricAuthEntity b WHERE b.userId = :userId AND b.isEnabled = true AND b.transports LIKE %:transport%")
    List<BiometricAuthEntity> findBiometricsByTransport(@Param("userId") UUID userId, @Param("transport") String transport);
    
    @Query("SELECT DISTINCT b.biometricType FROM BiometricAuthEntity b WHERE b.userId = :userId AND b.isEnabled = true")
    List<String> findEnabledBiometricTypesByUser(@Param("userId") UUID userId);
    
    @Query("SELECT b FROM BiometricAuthEntity b WHERE b.rpId = :rpId AND b.isEnabled = true")
    List<BiometricAuthEntity> findByRelyingParty(@Param("rpId") String rpId);
    
    @Query("SELECT b FROM BiometricAuthEntity b WHERE b.userId = :userId AND b.isEnabled = true AND b.flags & :flagMask = :flagMask")
    List<BiometricAuthEntity> findByFlags(@Param("userId") UUID userId, @Param("flagMask") Integer flagMask);
    
    @Query("SELECT COUNT(b) FROM BiometricAuthEntity b WHERE b.isEnabled = true")
    Long countAllEnabledBiometrics();
    
    @Query("SELECT COUNT(b) FROM BiometricAuthEntity b WHERE b.biometricType = :type")
    Long countByBiometricType(@Param("type") String type);
    
    @Query("SELECT b FROM BiometricAuthEntity b WHERE b.userId = :userId AND b.isEnabled = true AND b.createdAt >= :startDate")
    List<BiometricAuthEntity> findRecentlyCreatedBiometrics(@Param("userId") UUID userId, @Param("startDate") java.time.LocalDateTime startDate);
    
    // Delete queries
    void deleteByUserId(UUID userId);
    
    void deleteByUserIdAndDeviceId(UUID userId, String deviceId);
    
    void deleteByIsEnabledFalse();
    
    @Query("DELETE FROM BiometricAuthEntity b WHERE b.isEnabled = true AND b.lastUsedAt < :threshold")
    int deleteInactiveBiometrics(@Param("threshold") java.time.LocalDateTime threshold);
    
    // Update queries
    @Query("UPDATE BiometricAuthEntity b SET b.isEnabled = :enabled WHERE b.userId = :userId")
    int updateEnabledStatusByUser(@Param("userId") UUID userId, @Param("enabled") Boolean enabled);
    
    @Query("UPDATE BiometricAuthEntity b SET b.signCount = b.signCount + 1, b.lastUsedAt = :now WHERE b.id = :id")
    int incrementSignCount(@Param("id") UUID id, @Param("now") java.time.LocalDateTime now);
}